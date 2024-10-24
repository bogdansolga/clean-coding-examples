package com.great.project.db.db.manager.util;

import java.util.Arrays;
import java.util.UUID;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.db.manager.config.DbManagerConfig;
import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.entity.DatabaseMetadata;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.entity.SchemaMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.great.project.db.cloudfoundryapi.dto.NewServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.NewServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.UserPwdDto;
import com.great.project.db.cloudfoundryapi.exception.CloudFoundryException;
import com.great.project.db.cloudfoundryapi.service.CloudFoundryService;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;
import com.great.project.db.db.manager.dto.ServiceInstanceCreationStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to manage Cloud Foundry API interaction
 */
@Slf4j
@Component
public class CfUtil {

    private static final String DB_NAME_PREFIX = "db-";
    private static final String SCHEMA_NAME_PREFIX = "schema-";

    private final DbManagerConfig dbManagerConfig;
    private final CredentialStoreService credentialStoreService;
    private final CloudFoundryService cloudFoundryService;

    private UserPwdDto cfUserPwd;

    @Value("${dbManager.instance.password}")
    private String dbSystemPassword;

    @Value("${dbManager.instance.memory}")
    long dbMemory;

    @Value("${dbManager.instance.ipList}")
    String dbWhitelistIps;

    @Value("${dbManager.instance.enableScriptServer}")
    Boolean enableScriptServer;

    @Value("${dbManager.instance.name.maxLength}")
    int instanceNameMaxLength;

    /**
     * Instantiates a new cf util.
     *
     * @param dbManagerConfig
     *            the database manager config
     * @param credentialStoreService
     *            the credential store service
     * @param cloudFoundryService
     *            the cloud foundry service
     */
    public CfUtil(DbManagerConfig dbManagerConfig, CredentialStoreService credentialStoreService,
            CloudFoundryService cloudFoundryService) {
        this.dbManagerConfig = dbManagerConfig;
        this.credentialStoreService = credentialStoreService;
        this.cloudFoundryService = cloudFoundryService;
        initializeUserPwdDto();
    }

    /**
     * Creates a HANA Cloud database schema.
     *
     * @param schemaCredentials
     *            the schema credentials
     * @return the schema credentials
     */
    public SchemaCredentials createRealSchema(SchemaCredentials schemaCredentials) {
        try {
            SchemaMetadata schemaMetadata = schemaCredentials.getMetadata();
            createSchemaServiceInstance(schemaMetadata);
            createSchemaServiceKey(schemaCredentials);
            return schemaCredentials;
        } catch (CloudFoundryException e) {
            throw new DbManagerException(e.getMessage(), e);
        }
    }

    /**
     * Creates a HANA Cloud database instance
     *
     * @param databaseCredentials
     *            the database credentials
     * @return the database credentials
     */
    public DatabaseCredentials createRealDatabase(DatabaseCredentials databaseCredentials) {
        DatabaseMetadata databaseMetadata = databaseCredentials.getMetadata();
        try {
            createDbServiceInstance(databaseMetadata);
            checkDbCreated(databaseMetadata.getServiceInstanceName(), databaseMetadata.getServiceInstanceGuid());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DbManagerException(e.getMessage(), e);
        } catch (CloudFoundryException e) {
            throw new DbManagerException(e.getMessage(), e);

        }
        return databaseCredentials;
    }

    /**
     * Checks the database creation status. As the creation takes very long, it implements a countdown with a
     * granularity of half a minute.
     *
     * @param dbName
     *            the database instace name
     * @return true, if successful
     * @throws InterruptedException
     *             the interrupted exception
     * @throws CloudFoundryException
     *             the cloud foundry exception
     */
    private boolean checkDbCreated(String dbName, String serviceInstanceGuid)
            throws InterruptedException, CloudFoundryException {
        log.info("Checking database creation status...");

        boolean dbCreated = false;

        double timeCounter = 0;
        while (!dbCreated) {
            String token = cloudFoundryService.getTokenFor(cfUserPwd);
            ServiceInstanceCreationStatusDto creationStatus = cloudFoundryService
                    .retrieveServiceInstanceCreationStatus(token, serviceInstanceGuid);
            if ("create".equals(creationStatus.getLastOperationType())
                    && "in progress".equals(creationStatus.getLastOperationState())) {
                log.info("DB " + dbName + " not ready. " + timeCounter + " minutes passed.");
                timeCounter = timeCounter + 0.5;
                Thread.sleep(30000); // half a minute
            } else if ("create".equals(creationStatus.getLastOperationType())
                    && "succeeded".equals(creationStatus.getLastOperationState())) {
                log.info("DB " + dbName + " READY");
                dbCreated = true;
            } else {
                String msg = "DB creation failed: states = " + creationStatus.getLastOperationState() + " type = "
                        + creationStatus.getLastOperationType();
                log.error(msg);
                throw new DbManagerException(msg);
            }
        }
        return dbCreated;
    }

    /**
     * Delete an existing schema given the corresponding credentials
     *
     * @param entity
     *            schema credentials for the schema to be deleted
     * @return the schema credentials
     */
    public SchemaCredentials deleteRealSchema(SchemaCredentials entity) {
        try {
            deleteServiceKey(entity.getMetadata().getServiceKeyGuid());
            deleteServiceInstance(entity.getMetadata().getServiceInstanceGuid());
            return entity;
        } catch (CloudFoundryException e) {
            throw new DbManagerException(e.getMessage(), e);
        }
    }

    /**
     * Actual creation of a HANA Cloud database service instance
     * 
     * @param entity
     *            parameters for the database to be created
     * @return void
     * @throws CloudFoundryException
     */
    public void createDbServiceInstance(DatabaseMetadata entity) throws CloudFoundryException {
        log.info("Creating database instance...");
        String dbName = DB_NAME_PREFIX + entity.getTenantGuid();

        int computedNameLength = instanceNameMaxLength - DB_NAME_PREFIX.length();

        if (dbName.length() > instanceNameMaxLength)
            throw new CloudFoundryException("Given name length should not exceed " + computedNameLength);

        NewServiceInstanceDto newDbInstanceDto = new NewServiceInstanceDto();
        newDbInstanceDto.setSpaceGuid(dbManagerConfig.getSpaceGuid());
        newDbInstanceDto.setName(dbName);
        newDbInstanceDto.setServicePlanGuid(dbManagerConfig.getHanaCloudServicePlanGuid());
        newDbInstanceDto.setTags(Arrays.asList("cerner-hana-db"));

        newDbInstanceDto.setParameters("{\"data\":{\"edition\":\"cloud\",\"memory\":" + dbMemory
                + ",\"systempassword\":\"" + dbSystemPassword + "\",\"whitelistIPs\":[" + dbWhitelistIps
                + "],\"enabledservices\":{\"scriptserver\": " + enableScriptServer + "}}}");

        String token = cloudFoundryService.getTokenFor(cfUserPwd);
        ServiceInstanceDto dbInstanceDto = cloudFoundryService.createServiceInstance(token, newDbInstanceDto, true);

        entity.setServiceInstanceGuid(dbInstanceDto.getGuid());
        entity.setServiceInstanceName(dbName);

    }

    /**
     * Creates a schema instance
     * 
     * @param entity
     *            parameters for the schema to be created
     * @throws CloudFoundryException
     */
    private void createSchemaServiceInstance(SchemaMetadata entity) throws CloudFoundryException {
        log.info("Creating schema...");
        String serviceInstanceName = SCHEMA_NAME_PREFIX + UUID.randomUUID();

        NewServiceInstanceDto newServiceInstanceDto = new NewServiceInstanceDto();
        newServiceInstanceDto.setSpaceGuid(dbManagerConfig.getSpaceGuid());
        newServiceInstanceDto.setName(serviceInstanceName);
        newServiceInstanceDto.setServicePlanGuid(dbManagerConfig.getSchemaServicePlanGuid());
        newServiceInstanceDto.setTags(Arrays.asList("cerner-hana-schema"));
        // db id from db instance creation
        newServiceInstanceDto.setParameters("{\"database_id\":\"" + entity.getDatabaseId() + "\"}");

        String token = cloudFoundryService.getTokenFor(cfUserPwd);
        ServiceInstanceDto serviceInstanceDto = cloudFoundryService.createServiceInstance(token, newServiceInstanceDto,
                false);

        entity.setSchemaType(DbTypeEnum.HANA_SCHEMA.name());
        entity.setServiceInstanceGuid(serviceInstanceDto.getGuid());
        entity.setServiceInstanceName(serviceInstanceName);
        entity.setServicePlanGuid(dbManagerConfig.getSchemaServicePlanGuid());
    }

    /**
     * Creates a schema service key in order to be able to access the credentials for schema connection (host, user,
     * password, etc.)
     * 
     * @param entity
     *            credentials for the schema for which to create the service key
     * @throws CloudFoundryException
     */
    private void createSchemaServiceKey(SchemaCredentials entity) throws CloudFoundryException {
        log.info("Creating Service Key...");
        String serviceKeyName = "my-key";
        NewServiceKeyDto newServiceKeyDto = new NewServiceKeyDto();
        newServiceKeyDto.setName(serviceKeyName);
        newServiceKeyDto.setServiceInstanceGuid(entity.getMetadata().getServiceInstanceGuid());

        String token = cloudFoundryService.getTokenFor(cfUserPwd);
        ServiceKeyDto serviceKeyDto = cloudFoundryService.createServiceKey(token, newServiceKeyDto);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(serviceKeyDto.getCredentials());
            String schema = root.path("schema").asText();

            entity.getMetadata().setServiceKeyGuid(serviceKeyDto.getGuid());
            entity.getMetadata().setServiceKeyName(serviceKeyName);
            entity.setCredentials(serviceKeyDto.getCredentials());
            entity.getMetadata().setSchema(schema);
        } catch (JsonProcessingException e) {
            throw new DbManagerException(e.getMessage(), e);
        }
    }

    /**
     * Deletes a Cloud Foundry service instance
     * 
     * @param guid
     *            the id of the instance to be deleted
     * @throws CloudFoundryException
     */
    private void deleteServiceInstance(String guid) throws CloudFoundryException {
        String token = cloudFoundryService.getTokenFor(cfUserPwd);
        cloudFoundryService.deleteServiceInstance(token, guid);
    }

    /**
     * Deletes a service key
     * 
     * @param guid
     *            the id of the service key to be deleted
     * @throws CloudFoundryException
     */
    private void deleteServiceKey(String guid) throws CloudFoundryException {
        String token = cloudFoundryService.getTokenFor(cfUserPwd);
        cloudFoundryService.deleteServiceKey(token, guid);
    }

    /**
     * Initializes the value for the cfUserPwd class field
     */
    private void initializeUserPwdDto() {
        try {
            CredentialStoreEntryDto credentialStoreEntryDto = credentialStoreService.getPassword("augero.managedDb",
                    "cfUserPwd");
            cfUserPwd = new UserPwdDto();
            cfUserPwd.setUser(credentialStoreEntryDto.getUsername());
            cfUserPwd.setPwd(credentialStoreEntryDto.getValue());
        } catch (CredentialStoreException e) {
            String message = "Could not get the password for the authorized user";
            log.error(message, e);
            // throw exception and prevent initialization of this CfUtil Component
            throw new DbManagerException(message, e);
        }
    }

}
