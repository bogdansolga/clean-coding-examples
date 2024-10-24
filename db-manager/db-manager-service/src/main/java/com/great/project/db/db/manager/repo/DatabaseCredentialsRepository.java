package com.great.project.db.db.manager.repo;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.entity.DatabaseMetadata;
import com.great.project.db.db.manager.util.DbManagerException;
import com.great.project.db.db.manager.util.ObjectMapperUtils;
import org.springframework.stereotype.Repository;

import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;

import lombok.extern.slf4j.Slf4j;

/**
 * The repository that offers the necessary methods to save or retrieve credentials for a tenant
 */
@Slf4j
@Repository
public class DatabaseCredentialsRepository {

    private static final String NAMESPACE = "augero.managedDb.tenantDatabases";
    private final CredentialStoreService credentialStoreService;

    public DatabaseCredentialsRepository(CredentialStoreService credentialStoreService) {
        this.credentialStoreService = credentialStoreService;
    }

    /**
     * Retrieves the credential for a given tenant
     * 
     * @param tenant
     *            guid/name of the tenant
     * @return the credentials
     */
    public DatabaseCredentials findByTenantGuid(String tenant) {
        try {
            log.info("Looking for existing key for tenant: " + tenant);

            CredentialStoreEntryDto credentialStoreEntryDto = credentialStoreService.getKey(NAMESPACE, tenant);
            if (credentialStoreEntryDto == null) {
                log.info("Key not found");
                return null;
            } else {
                log.info("Key retrieved");
            }
            DatabaseCredentials dto = new DatabaseCredentials();
            dto.setCredentials(credentialStoreEntryDto.getValue());
            DatabaseMetadata metadata = ObjectMapperUtils.deserializeJsonString(credentialStoreEntryDto.getMetadata(),
                    DatabaseMetadata.class);
            dto.setMetadata(metadata);
            return dto;
        } catch (CredentialStoreException e) {
            return null;
        }
    }

    /**
     * Saves the given credentials into the Credential Store
     * 
     * @param credentials
     * @return the credentials given
     */
    public DatabaseCredentials save(DatabaseCredentials credentials) {
        NewCredentialStoreEntryDto newEntry = new NewCredentialStoreEntryDto();
        newEntry.setName(credentials.getMetadata().getTenantGuid());
        newEntry.setValue(credentials.getCredentials());
        String metadata = ObjectMapperUtils.serializeToString(credentials.getMetadata());
        newEntry.setMetadata(metadata);
        try {
            credentialStoreService.createUpdateKey(NAMESPACE, newEntry);
        } catch (CredentialStoreException e) {
            throw new DbManagerException(e.getMessage(), e);
        }
        return credentials;
    }
}
