package com.great.project.db.db.manager.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.great.project.db.db.manager.dto.CreateTenantSchemaRequestDto;
import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.entity.DatabaseMetadata;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.entity.SchemaMetadata;
import com.great.project.db.db.manager.repo.DatabaseCredentialsRepository;
import com.great.project.db.db.manager.repo.SchemaCredentialsRepository;
import com.great.project.db.db.manager.util.CfUtil;
import com.great.project.db.db.manager.util.DbManagerException;
import com.great.project.db.db.manager.util.StatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to manage (create, retrieve, delete, etc.) tenant schemas and associated credentials stored with the the SAP
 * Credential Store service
 * 
 */
@Slf4j
@Service
public class DbManagerService {

    private static final String MSG_CONST = "{}: tenant: {}, space: {}, module: {}";
    private static final String LOG_SCHEMA = "schema: ";
    private static final String MSG_CONST_SCHEMA_ID = "{}: " + LOG_SCHEMA + "{}";
    private static final String SCHEMA_CREDENTIALS_NULL = "Schema credentials null.";
    private static final String LOG_TENANT = "tenant: ";
    private static final String LOG_SPACE = ", space: ";
    private static final String LOG_MODULE = ", module: ";
    private final SchemaCredentialsRepository schemaCredentialsRepository;
    private final DatabaseCredentialsRepository databaseCredentialsRepository;
    private final CfUtil cfUtil;

    public DbManagerService(CfUtil cfUtil, SchemaCredentialsRepository schemaCredentialsRepository,
            DatabaseCredentialsRepository databaseCredentialsRepository) {
        this.cfUtil = cfUtil;
        this.schemaCredentialsRepository = schemaCredentialsRepository;
        this.databaseCredentialsRepository = databaseCredentialsRepository;
    }

    /**
     * Retrieves the schema credentials for a given tenant, module and Cloud Foundry space
     * 
     * @param tenant
     * @param module
     * @param space
     * @return schema credentials
     */
    public SchemaCredentials getTenantSchemaFor(String tenant, String module, String space) {
        log.info(MSG_CONST, "Get tenant schema for", tenant, module, space);
        SchemaCredentials entity = schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(tenant, module, space);
        if (entity == null) {
            throw new DbManagerException(SCHEMA_CREDENTIALS_NULL);
        }
        return entity;
    }

    /**
     * Retrieves the schema credentials for a given schema Service Instance Name
     * 
     * @param schemaInstanceName
     * @return schema credentials
     */
    public SchemaCredentials getTenantSchemaByInstanceName(String schemaInstanceName) {
        log.info(MSG_CONST_SCHEMA_ID, "Get tenant schema for", schemaInstanceName);
        SchemaCredentials entity = schemaCredentialsRepository.findBySchemaInstanceName(schemaInstanceName);
        if (entity == null) {
            throw new DbManagerException(SCHEMA_CREDENTIALS_NULL);
        }
        return entity;
    }

    /**
     * Creates a schema for a given tenant, module and Cloud Foundry space
     * 
     * @param request
     *            a dto containing tenant, module and Cloud Foundry space
     * @return schema credentials for the newly created schema
     */
    public SchemaCredentials createTenantSchemaFor(CreateTenantSchemaRequestDto request) {
        log.info("Create tenant schema for: " + LOG_TENANT + request.getTenantGuid() + LOG_MODULE + request.getModule()
                + LOG_SPACE + request.getSpace());

        log.info("Getting database credentials...");
        DatabaseCredentials databaseCredentials = databaseCredentialsRepository
                .findByTenantGuid(request.getTenantGuid());
        log.info("Database credentials: " + (databaseCredentials == null ? "NULL" : databaseCredentials));

        if (databaseCredentials == null) {
            // this creates the database too
            databaseCredentials = createDatabaseAndSaveCredentials(request);
        } else {
            databaseCredentials = increaseDatabaseSubscriptionCountAndSaveCredentials(databaseCredentials);
        }

        log.info("Getting database credentials after creation...");
        log.info("Database credentials: " + (databaseCredentials == null ? "NULL" : databaseCredentials));

        log.info("Getting schema credentials...");
        SchemaCredentials schemaCredentials = schemaCredentialsRepository
                .findByTenantGuidAndModuleAndSpace(request.getTenantGuid(), request.getModule(), request.getSpace());
        log.info("Schema credentials: " + (schemaCredentials == null ? "NULL" : schemaCredentials));

        if (schemaCredentials == null) {
            if (databaseCredentials != null)
                schemaCredentials = createSchemaAndSaveCredentials(request, databaseCredentials);
        } else {
            schemaCredentials = inscreaseSchemaSubscriptionCountAndSaveCredentials(schemaCredentials);
        }
        log.info("Getting schema credentials after creation...");
        log.debug("Schema credentials: ", schemaCredentials);
        return schemaCredentials;
    }

    /**
     * Increases the subscription count for an existing schema. If the schema was marked for deletion that mark is reset
     * and the status is set to CREATED
     * 
     * @param schemaCredentials
     *            schema credentials to be updated
     * @return schema credentials with the updated subscription count
     */
    private SchemaCredentials inscreaseSchemaSubscriptionCountAndSaveCredentials(SchemaCredentials schemaCredentials) {
        schemaCredentials.getMetadata()
                .setSubscriptionCount(schemaCredentials.getMetadata().getSubscriptionCount() + 1);
        if (StatusEnum.MARKED_FOR_DELETION.name().equals(schemaCredentials.getMetadata().getStatus())) {
            schemaCredentials.getMetadata().setStatus(StatusEnum.CREATED.name());
            schemaCredentials.getMetadata().clearMarkedForDeletion();
        }
        schemaCredentials.getMetadata().setUpdatedAt(LocalDateTime.now());
        schemaCredentials = schemaCredentialsRepository.save(schemaCredentials);
        return schemaCredentials;
    }

    /**
     * Deletes a tenant schema
     * 
     * @param tenant
     * @param module
     * @param space
     * @param deleteImmediatly
     *            if this flag is set to true the schema is deleted immediately. If false, the schema will be merely
     *            marked for deletion.
     */
    public void deleteTenantSchemaFor(String tenant, String module, String space, boolean deleteImmediatly) {
        log.info("delete " + LOG_TENANT + tenant + LOG_MODULE + module + LOG_SPACE + space);
        var entity = schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(tenant, module, space);
        var logMessage = "Immediate deletion requested. Schema for tenant " + tenant + ", module " + module + ", space "
                + space + " was deleted";
        processDeleteSchema(deleteImmediatly, entity, logMessage);
    }

    /**
     * Deletes a tenant schema by it's Id
     * 
     * @param schemaInstanceName
     * @param deleteImmediatly
     *            if this flag is set to true the schema is deleted immediately. If false, the schema will be merely
     *            marked for deletion.
     */
    public void deleteTenantSchemaByInstanceName(String schemaInstanceName, boolean deleteImmediatly) {
        log.info("delete " + LOG_SCHEMA + schemaInstanceName);
        var entity = schemaCredentialsRepository.findBySchemaInstanceName(schemaInstanceName);
        var logMessage = "Immediate deletion requested. Schema Instance Name " + schemaInstanceName + " was deleted";
        processDeleteSchema(deleteImmediatly, entity, logMessage);
    }

    private void processDeleteSchema(boolean deleteImmediatly, SchemaCredentials entity, String logMessage) {
        if (entity != null) {
            entity.getMetadata().setSubscriptionCount(entity.getMetadata().getSubscriptionCount() - 1);
            if (entity.getMetadata().getSubscriptionCount() <= 0) {
                entity.getMetadata().setStatus(StatusEnum.MARKED_FOR_DELETION.name());
                entity.getMetadata().setMarkedForDeletionAt(LocalDateTime.now());
                entity.getMetadata().setSubscriptionCount(0L);
            }
            entity.getMetadata().setUpdatedAt(LocalDateTime.now());
            entity = schemaCredentialsRepository.save(entity);

            if (deleteImmediatly && entity.getMetadata().getSubscriptionCount() == 0) {
                deleteSchema(entity);
                log.info(logMessage);
            }
        } else {
            throw new DbManagerException(SCHEMA_CREDENTIALS_NULL);
        }
    }

    /**
     * Creates an actual schema and saves the credentials in the Credential Store
     * 
     * @param request
     * @param databaseCredentials
     * @return the credentials for the newly created schema
     */
    private SchemaCredentials createSchemaAndSaveCredentials(CreateTenantSchemaRequestDto request,
            DatabaseCredentials databaseCredentials) {
        SchemaCredentials schemaCredentials = new SchemaCredentials();

        final SchemaMetadata schemaMetadata = getSchemaMetadata(request, databaseCredentials);
        schemaCredentials.setMetadata(schemaMetadata);
        schemaCredentials = schemaCredentialsRepository.save(schemaCredentials);

        schemaCredentials = cfUtil.createRealSchema(schemaCredentials);

        schemaMetadata = schemaCredentials.getMetadata();
        schemaMetadata.setStatus(StatusEnum.CREATED.name());
        schemaMetadata.setStatusMessage("OK");
        schemaMetadata.setSubscriptionCount(1L);
        schemaCredentials = schemaCredentialsRepository.save(schemaCredentials);
        return schemaCredentials;
    }

    private static SchemaMetadata getSchemaMetadata(CreateTenantSchemaRequestDto request, DatabaseCredentials databaseCredentials) {
        SchemaMetadata schemaMetadata = new SchemaMetadata();
        schemaMetadata.setTenantGuid(request.getTenantGuid());
        schemaMetadata.setModule(request.getModule());
        schemaMetadata.setSpace(request.getSpace());
        final DatabaseMetadata metadata = databaseCredentials.getMetadata();
        schemaMetadata.setDatabaseId(metadata.getServiceInstanceGuid());
        schemaMetadata.setDatabaseName(metadata.getServiceInstanceName());
        schemaMetadata.setStatus(StatusEnum.CREATING.name());
        schemaMetadata.setCreatedAt(LocalDateTime.now());
        schemaMetadata.setSubscriptionCount(0L);
        return schemaMetadata;
    }

    /**
     * Increases the database subscription count and saves the credentials
     * 
     * @param databaseCredentials
     * @return updated credentials
     */
    private DatabaseCredentials increaseDatabaseSubscriptionCountAndSaveCredentials(
            DatabaseCredentials databaseCredentials) {
        final DatabaseMetadata metadata = databaseCredentials.getMetadata();
        metadata.setSubscriptionCount(metadata.getSubscriptionCount() + 1);
        if (StatusEnum.MARKED_FOR_DELETION.name().equals(metadata.getStatus())) {
            metadata.setStatus(StatusEnum.CREATED.name());
            metadata.clearMarkedForDeletion();
        }
        metadata.setUpdatedAt(LocalDateTime.now());
        databaseCredentials = databaseCredentialsRepository.save(databaseCredentials);
        return databaseCredentials;
    }

    /**
     * Creates a HANA Cloud database instance
     * 
     * @param request
     * @return the credentials for the newly created database
     */
    private DatabaseCredentials createDatabaseAndSaveCredentials(CreateTenantSchemaRequestDto request) {
        DatabaseCredentials databaseCredentials = new DatabaseCredentials();

        final DatabaseMetadata databaseMetadata = getDatabaseMetadata(request);
        databaseCredentials = databaseCredentialsRepository.save(databaseCredentials);
        Result result = new Result(databaseCredentials, databaseMetadata);
        result.databaseCredentials().setMetadata(result.databaseMetadata());

        databaseCredentials = cfUtil.createRealDatabase(result.databaseCredentials());

        databaseMetadata = databaseCredentials.getMetadata();
        databaseMetadata.setStatus(StatusEnum.CREATED.name());
        databaseMetadata.setStatusMessage("OK");
        databaseMetadata.setSubscriptionCount(1L);
        databaseCredentials = databaseCredentialsRepository.save(databaseCredentials);
        return databaseCredentials;
    }

    private static DatabaseMetadata getDatabaseMetadata(CreateTenantSchemaRequestDto request) {
        DatabaseMetadata databaseMetadata = new DatabaseMetadata();
        databaseMetadata.setTenantGuid(request.getTenantGuid());
        databaseMetadata.setStatus(StatusEnum.CREATING.name());
        databaseMetadata.setCreatedAt(LocalDateTime.now());
        databaseMetadata.setSubscriptionCount(0L);
        return databaseMetadata;
    }

    private record Result(DatabaseCredentials databaseCredentials, DatabaseMetadata databaseMetadata) {
    }

    /**
     * Deletes a schema
     * 
     * @param entity
     */
    private void deleteSchema(SchemaCredentials entity) {
        entity.getMetadata().setStatus(StatusEnum.DELETING.name());
        entity.getMetadata().setUpdatedAt(LocalDateTime.now());
        schemaCredentialsRepository.save(entity);

        entity = cfUtil.deleteRealSchema(entity);

        entity.getMetadata().setStatus(StatusEnum.DELETED.name());
        entity.getMetadata().setDeletedAt(LocalDateTime.now());
        schemaCredentialsRepository.save(entity);
    }

}
