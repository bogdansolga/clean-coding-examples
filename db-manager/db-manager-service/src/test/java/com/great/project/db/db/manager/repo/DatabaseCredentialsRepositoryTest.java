package com.great.project.db.db.manager.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;
import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.util.DbManagerException;
import com.great.project.db.test.util.CredentialStoreEntriesDataFactory;
import com.great.project.db.test.util.DbManagerDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test {@link DatabaseCredentialsRepository}
 * 
 * @author Gabriela Maciac
 */
public class DatabaseCredentialsRepositoryTest {

    private CredentialStoreService credentialStoreService;
    private DatabaseCredentialsRepository databaseCredentialsRepository;

    CredentialStoreEntryDto credentialStoreEntryDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntryDto();
    DatabaseCredentials databaseCredentials = DbManagerDataFactory.createMinimumDatabaseCredentials();

    @BeforeEach
    public void setUp() {
        credentialStoreService = mock(CredentialStoreService.class);
        databaseCredentialsRepository = new DatabaseCredentialsRepository(credentialStoreService);
    }

    /**
     * Purpose: > Given a valid DatabaseCredentials, when calling save method, the correct DatabaseCredentials is
     * returned. <br>
     * Prerequisites: > Mock DatabaseCredentials.<br>
     * Design Steps: > 1. Stub the CredentialStoreService to return the expected CredentialStoreEntryDto entity.<br>
     * 2. Call DatabaseCredentialsRepository (class under test) to retrieve the actual DatabaseCredentials entity.<br>
     * 3. Assert the actual entity. <br>
     * Expected Results: > The actual entity has the expected fields.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid DatabaseCredentials, when calling save method, the correct DatabaseCredentials is returned.")
    @Test
    public void returnDatabaseCredentialsWhenSavingCredentials() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.createUpdateKey(any(String.class), any(NewCredentialStoreEntryDto.class)))
                .thenReturn(credentialStoreEntryDto);

        // Act
        DatabaseCredentials actual = databaseCredentialsRepository.save(databaseCredentials);

        // Assert
        assertEquals(databaseCredentials.getMetadata().getTenantGuid(), actual.getMetadata().getTenantGuid());
        assertEquals(databaseCredentials.getCredentials(), actual.getCredentials());
    }

    /**
     * Purpose: > Given invalid namespace and NewCredentialStoreEntryDto, when calling save method, an exception is
     * thrown. <br>
     * Prerequisites: > Mock namespace and NewCredentialStoreEntryDto.<br>
     * Design Steps: > 1. Stub the CredentialStoreService to throw a CredentialStoreException.<br>
     * 2. Call DatabaseCredentialsRepository (class under test) to throw a DdManagerException.<br>
     * 3. Assert the exception thrown. <br>
     * Expected Results: > A DbManagerException is thrown.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given invalid namespace and NewCredentialStoreEntryDto, when calling save method, an exception is thrown.")
    @Test
    public void throwExceptionWhenSavingCredentials() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.createUpdateKey(any(String.class), any(NewCredentialStoreEntryDto.class)))
                .thenThrow(CredentialStoreException.class);

        // Act and assert
        assertThrows(DbManagerException.class, () -> databaseCredentialsRepository.save(databaseCredentials));
    }

}
