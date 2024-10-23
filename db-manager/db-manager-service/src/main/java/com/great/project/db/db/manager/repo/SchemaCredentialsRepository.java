package com.great.project.db.db.manager.repo;

import java.util.List;
import java.util.Optional;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.entity.SchemaMetadata;
import com.great.project.db.db.manager.util.DbManagerException;
import com.great.project.db.db.manager.util.ObjectMapperUtils;
import org.springframework.stereotype.Repository;

import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;

import lombok.extern.slf4j.Slf4j;

/**
 * Repository to retrive the schema credentials from the Credential Store
 * 
 * @author Norbert Paukner <norbert.paukner@cerner.com>
 */
@Slf4j
@Repository
public class SchemaCredentialsRepository {

    private static final String DEFAULT = "DEFAULT";
    private static final String NAMESPACE_DATABASES = "augero.managedDb.tenantDatabases";
    private static final String NAMESPACE_SCHEMAS = "augero.managedDb.tenantSchemas";
    private final CredentialStoreService credentialStoreService;

    public SchemaCredentialsRepository(CredentialStoreService credentialStoreService) {
        this.credentialStoreService = credentialStoreService;
    }

    /**
     * Retrieve a tenant credentials given the parameters
     * 
     * @param tenant
     * @param module
     * @param space
     * @return the credentials
     */
    public SchemaCredentials findByTenantGuidAndModuleAndSpace(String tenant, String module, String space) {
        try {
            String namespace = concatNamespace(tenant);
            String name = concatKeyName(module, space);
            CredentialStoreEntryDto credentialStoreEntryDto = credentialStoreService.getKey(namespace, name);

            SchemaCredentials dto = new SchemaCredentials();
            dto.setCredentials(credentialStoreEntryDto.getValue());
            SchemaMetadata metadata = ObjectMapperUtils.deserializeJsonString(credentialStoreEntryDto.getMetadata(),
                    SchemaMetadata.class);
            dto.setMetadata(metadata);
            return dto;
        } catch (CredentialStoreException e) {
            return null;
        }
    }

    /**
     * Retrieve a tenant credentials given the schema Service Instance Name
     * 
     * @param schemaInstanceName
     * @return the credentials
     */
    public SchemaCredentials findBySchemaInstanceName(String schemaInstanceName) {
        try {
            return getSchemaCredential(schemaInstanceName);
        } catch (CredentialStoreException e) {
            return null;
        }
    }

    private SchemaCredentials getSchemaCredential(String schemaInstanceName) throws CredentialStoreException {
        // First get all the tenant Id's for "augero.managedDb.tenantDatabases" namespace
        List<CredentialStoreEntriesEntryDto> tenantKeys = credentialStoreService.getKeys(NAMESPACE_DATABASES)
                                                                                .getContent();
        // Filter out the tenant's Credential Store key which has the searched schemaId
        for (var i = 0; i < tenantKeys.size(); i++) {
            String tenant = tenantKeys.get(i).getName();
            // get all the module/space keys for "augero.managedDb.tenantSchemas.<tenantId>"
            List<CredentialStoreEntriesEntryDto> schemaKeys = credentialStoreService.getKeys(concatNamespace(tenant))
                    .getContent();
            for (var j = 0; j < schemaKeys.size(); j++) {
                String moduleAndSpace = schemaKeys.get(j).getName();
                // get the specific module/space key for "augero.managedDb.tenantSchemas.<tenantId>"
                var credentialStoreEntryDto = credentialStoreService.getKey(concatNamespace(tenant), moduleAndSpace);
                // build the returning SchemaCredential object
                var schemaCredentials = new SchemaCredentials();
                schemaCredentials.setCredentials(credentialStoreEntryDto.getValue());
                Optional<SchemaMetadata> metadata = getKeyMetadata(credentialStoreEntryDto);
                if (metadata.isPresent() && (metadata.get().getServiceInstanceName() != null)) {
                    schemaCredentials.setMetadata(metadata.get());
                    if (metadata.get().getServiceInstanceName().equals(schemaInstanceName)) {
                        return schemaCredentials;
                    }
                } else {
                    log.info("Error occured when reading Credential Store key :"
                            .concat(credentialStoreEntryDto.getMetadata()));
                }
            }
        }
        return null;
    }

    private Optional<SchemaMetadata> getKeyMetadata(CredentialStoreEntryDto credentialStoreEntryDto) {
        return Optional.of(
                ObjectMapperUtils.deserializeJsonString(credentialStoreEntryDto.getMetadata(), SchemaMetadata.class));
    }

    /**
     * Save the given credentials into the Credential Store
     * 
     * @param credentials
     * @return the given credentials
     */
    public SchemaCredentials save(SchemaCredentials credentials) {
        String namespace = concatNamespace(credentials.getMetadata().getTenantGuid());
        String name = concatKeyName(credentials.getMetadata().getModule(), credentials.getMetadata().getSpace());

        NewCredentialStoreEntryDto newEntry = new NewCredentialStoreEntryDto();
        newEntry.setName(name);
        newEntry.setValue(credentials.getCredentials());
        String metadata = ObjectMapperUtils.serializeToString(credentials.getMetadata());
        newEntry.setMetadata(metadata);
        try {
            credentialStoreService.createUpdateKey(namespace, newEntry);
        } catch (CredentialStoreException e) {
            throw new DbManagerException(e.getMessage(), e);
        }
        return credentials;
    }

    /**
     * Utility to obtain the full name of the wanted credentials namespace (including tenant)
     * 
     * @param tenant
     * @return the concatenated name
     */
    private String concatNamespace(String tenant) {
        if (tenant == null) {
            tenant = DEFAULT;
        }
        return NAMESPACE_SCHEMAS + "." + tenant;
    }

    /**
     * Utility to obtain the full name for key retrieval from the Credential Store
     * 
     * @param module
     * @param space
     * @return the concatenated name
     */
    private String concatKeyName(String module, String space) {
        if (module == null) {
            module = DEFAULT;
        }
        if (space == null) {
            space = DEFAULT;
        }
        return module + "." + space;
    }
}
