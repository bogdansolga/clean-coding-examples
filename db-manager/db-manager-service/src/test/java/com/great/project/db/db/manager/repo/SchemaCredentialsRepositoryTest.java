package com.great.project.db.db.manager.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.util.DbManagerException;
import com.great.project.db.test.util.CredentialStoreEntriesDataFactory;
import com.great.project.db.test.util.DbManagerDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test {@link SchemaCredentialsRepository}
 * 
 * @author Gabriela Maciac
 */
public class SchemaCredentialsRepositoryTest {

    private static final String TENANT_NAME = "some-name";
    private static final String METADATA_JSON = "{\"tenantName\": \"some-name\"}";
    private static final String METADATA_JSON_VALID = "{\"tenantName\": \"some-name\",\"serviceInstanceName\": \"schema-schemaId\"}";
    private static final String MOCK_TENANT = "tenant";
    private static final String MOCK_MODULE = "module";
    private static final String MOCK_SPACE = "space";
    private static final String MOCK_SCHEMA = "schema-schemaId";
    private static final String MOCK_SPACE_MODULE = "credential-store-entry";
    private static final String NAMESPACE = "augero.managedDb.tenantSchemas.tenant";
    private static final String NAME = "module.space";
    private static final String DEFAULT_NAMESPACE = "augero.managedDb.tenantSchemas.DEFAULT";
    private static final String NAMESPACE_DATABASES = "augero.managedDb.tenantDatabases";
    private static final String NAMESPACE_SCHEMA = "augero.managedDb.tenantSchemas.credential-store-entry";
    private static final String DEFAULT_NAME = "DEFAULT.DEFAULT";

    CredentialStoreEntryDto credentialStoreEntryDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntryDto();
    CredentialStoreEntriesDto credentialStoreEntriesDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntriesDto();
    List<CredentialStoreEntriesEntryDto> credentialStoreEntriesEntryDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntriesEntryDtoList();
    SchemaCredentials schemaCredentials = DbManagerDataFactory.createMinimumSchemaCredentials();

    private CredentialStoreService credentialStoreService;

    private SchemaCredentialsRepository schemaCredentialsRepository;

    @BeforeEach
    public void setUp() {
        credentialStoreService = mock(CredentialStoreService.class);
        schemaCredentialsRepository = new SchemaCredentialsRepository(credentialStoreService);
    }

    /**
     * Purpose: > Given valid namespace and name, when calling findByTenantGuidAndModuleAndSpace, then the correct
     * SchemaCredentials is returned.<br>
     * Prerequisites: > Mock namespace and name. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to return the expected CredentialStoreEntryDto.<br>
     * 2. Set on the CredentialStoreEntryDto metadata a valid JSON.<br>
     * 3. Call SchemaCredentialsRepository (class under test) to returned the actual SchemaCredentials.<br>
     * 4. Assert the expected SchemaCredentials is equal to the actual one. <br>
     * Expected Results: > The expected SchemaCredentials is equal to the actual one.<br>
     */
    @DisplayName("Given valid namespace and name, when calling findByTenantGuidAndModuleAndSpace, then the correct SchemaCredentials is returned.")
    @Test
    public void returnSchemaCredentialsWhenFindingByTenantGuidAndModuleAndSpace() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.getKey(NAMESPACE, NAME)).thenReturn(credentialStoreEntryDto);
        credentialStoreEntryDto.setMetadata(METADATA_JSON);

        // Act
        SchemaCredentials actual = schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT,
                MOCK_MODULE, MOCK_SPACE);

        // Assert
        assertEquals(credentialStoreEntryDto.getValue(), actual.getCredentials());
        assertEquals(TENANT_NAME, actual.getMetadata().getTenantName());
    }

    /**
     * Purpose: > Given valid namespace and name, when calling findBySchemaId, then the correct SchemaCredentials is
     * returned.<br>
     * Prerequisites: > Mock namespace and name. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to return the expected CredentialStoreEntryDto.<br>
     * 2. Set on the CredentialStoreEntryDto metadata a valid JSON.<br>
     * 3. Call SchemaCredentialsRepository (class under test) to returned the actual SchemaCredentials.<br>
     * 4. Assert the expected SchemaCredentials is equal to the actual one. <br>
     * Expected Results: > The expected SchemaCredentials is equal to the actual one.<br>
     */
    @DisplayName("Given valid namespace and name, when calling findBySchemaId, then the correct SchemaCredentials is returned.")
    @Test
    public void returnSchemaCredentialsWhenFindingBySchemaInstanceName() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.getKeys(NAMESPACE_DATABASES)).thenReturn(credentialStoreEntriesDto);
        credentialStoreEntriesDto.setContent(credentialStoreEntriesEntryDto);
        when(credentialStoreService.getKeys(NAMESPACE_SCHEMA)).thenReturn(credentialStoreEntriesDto);
        credentialStoreEntriesDto.setContent(credentialStoreEntriesEntryDto);
        when(credentialStoreService.getKey(NAMESPACE_SCHEMA, MOCK_SPACE_MODULE)).thenReturn(credentialStoreEntryDto);
        credentialStoreEntryDto.setMetadata(METADATA_JSON_VALID);

        // Act
        SchemaCredentials actual = schemaCredentialsRepository.findBySchemaInstanceName(MOCK_SCHEMA);

        // Assert
        assertEquals(credentialStoreEntryDto.getValue(), actual.getCredentials());
        assertEquals(TENANT_NAME, actual.getMetadata().getTenantName());
    }

    /**
     * Purpose: > Given invalid namespace and name, when calling findByTenantGuidAndModuleAndSpace, then a null
     * SchemaCredentials is returned.<br>
     * Prerequisites: > Mock namespace and name. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to throw an exception.<br>
     * 2. Call SchemaCredentialsRepository (class under test) to returned the actual SchemaCredentials.<br>
     * 4. Assert the actual SchemaCredentials is null. <br>
     * Expected Results: > The SchemaCredentials returned is null.<br>
     */
    @DisplayName("Given invalid namespace and name, when calling findByTenantGuidAndModuleAndSpace, then a null SchemaCredentials is returned.")
    @Test
    public void returnNullSchemaCredentialsWhenExceptionIsThrown() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.getKey(NAMESPACE, NAME)).thenThrow(CredentialStoreException.class);

        // Act
        SchemaCredentials actual = schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT,
                MOCK_MODULE, MOCK_SPACE);

        // Assert
        assertNull(actual);
    }

    /**
     * Purpose: > Given invalid namespace and name, when calling findBySchemaInstanceName, then a null SchemaCredentials
     * is returned.<br>
     * Prerequisites: > Mock namespace and name. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to throw an exception.<br>
     * 2. Call SchemaCredentialsRepository (class under test) to returned the actual SchemaCredentials.<br>
     * 4. Assert the actual SchemaCredentials is null. <br>
     * Expected Results: > The SchemaCredentials returned is null.<br>
     */
    @DisplayName("Given invalid namespace and name, when calling findBySchemaInstanceName, then a null SchemaCredentials is returned.")
    @Test
    public void returnNullSchemaCredentialsWhenFindBySchemaInstanceNameExceptionIsThrown()
            throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.getKey(NAMESPACE, NAME)).thenThrow(CredentialStoreException.class);

        // Act
        SchemaCredentials actual = schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT,
                MOCK_MODULE, MOCK_SPACE);

        // Assert
        assertNull(actual);
    }

    /**
     * Purpose: > Given valid namespace and name, when calling findByTenantGuidAndModuleAndSpace with default values,
     * then the correct SchemaCredentials is returned.<br>
     * Prerequisites: > Mock namespace and name. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to return the expected CredentialStoreEntryDto.<br>
     * 2. Set on the CredentialStoreEntryDto metadata a valid JSON.<br>
     * 3. Call SchemaCredentialsRepository (class under test) to returned the actual SchemaCredentials.<br>
     * 4. Assert the expected SchemaCredentials has the expected values. <br>
     * Expected Results: > The actual SchemaCredentials has correct values.<br>
     */
    @DisplayName("Given valid namespace and name, when calling findByTenantGuidAndModuleAndSpace with default values, then the correct "
            + "SchemaCredentials is returned.")
    @Test
    public void returnSchemaCredentialsWhenFindingTenantGuidAndModuleAndSpaceWithDefaultValues()
            throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.getKey(DEFAULT_NAMESPACE, DEFAULT_NAME)).thenReturn(credentialStoreEntryDto);
        credentialStoreEntryDto.setMetadata(METADATA_JSON);

        // Act
        SchemaCredentials actual = schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(null, null, null);

        // Assert
        assertEquals(credentialStoreEntryDto.getValue(), actual.getCredentials());
        assertEquals(TENANT_NAME, actual.getMetadata().getTenantName());
    }

    /**
     * Purpose: > Return the saved SchemaCredentials when calling the save method.<br>
     * Prerequisites: > Mock CredentialStoreEntryDto. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to return the expected CredentialStoreEntryDto.<br>
     * 2. Call SchemaCredentialsRepository (class under test) to returned the actual SchemaCredentials.<br>
     * 3. Assert the expected SchemaCredentials is equal to the actual one. <br>
     * Expected Results: > The expected SchemaCredentials is equal to the actual one.<br>
     */
    @DisplayName("Return the saved SchemaCredentials when calling the save method.")
    @Test
    public void returnSchemaCredentialsWhenSavingSchemaCredentials() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.createUpdateKey(any(String.class), any(NewCredentialStoreEntryDto.class)))
                .thenReturn(credentialStoreEntryDto);

        // Act
        SchemaCredentials actual = schemaCredentialsRepository.save(schemaCredentials);

        // Assert
        assertEquals(schemaCredentials.getCredentials(), actual.getCredentials());
        assertEquals(schemaCredentials.getMetadata(), actual.getMetadata());
    }

    /**
     * Purpose: > Given invalid namespace and NewCredentialStoreEntryDto, when saving SchemaCredentials, then an
     * exception is thrown. <br>
     * Prerequisites: > N/A. <br>
     * Design Steps: > 1. Stub the CredentialStoreService to throw an exception.<br>
     * 2. Call SchemaCredentialsRepository (class under test) to throw the exception.<br>
     * 3. Assert that the exception was thrown <br>
     * Expected Results: > A DbManagerException was thrown.<br>
     */
    @DisplayName("Given invalid namespace and NewCredentialStoreEntryDto, when saving SchemaCredentials, then an exception is thrown.")
    @Test
    public void throwExceptionWhenSavingSchemaCredentials() throws CredentialStoreException {
        // Arrange
        when(credentialStoreService.createUpdateKey(any(String.class), any(NewCredentialStoreEntryDto.class)))
                .thenThrow(CredentialStoreException.class);

        // Act and assert
        assertThrows(DbManagerException.class, () -> schemaCredentialsRepository.save(schemaCredentials));
    }
}
