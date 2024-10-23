package com.great.project.db.db.manager.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.great.project.db.cloudfoundryapi.exception.CloudFoundryException;
import com.great.project.db.cloudfoundryapi.service.CloudFoundryService;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;
import com.great.project.db.db.manager.config.DbManagerConfig;
import com.great.project.db.db.manager.dto.ServiceInstanceCreationStatusDto;
import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.test.util.CredentialStoreEntriesDataFactory;
import com.great.project.db.test.util.DbManagerDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.great.project.db.cloudfoundryapi.dto.NewServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.NewServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.UserPwdDto;

/**
 * Test {@link CfUtil}
 * 
 * @author Gabriela Maciac
 */
public class CfUtilTest {

    private static final String NAMESPACE = "augero.managedDb";
    private static final String CF_USER_PWD = "cfUserPwd";
    private static final String MOCK_TOKEN = "token";
    private static final String GUID = "guid";
    private static final String MOCK_JSON = "{\"schema\": \"mock-schema\"}";

    private final CredentialStoreService credentialStoreService = mock(CredentialStoreService.class);
    private final CloudFoundryService cloudFoundryService = mock(CloudFoundryService.class);
    private CfUtil cfUtil;

    private final SchemaCredentials schemaCredentials = DbManagerDataFactory.createMinimumSchemaCredentials();
    private final CredentialStoreEntryDto credentialStoreEntryDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntryDto();
    private final DatabaseCredentials databaseCredentials = DbManagerDataFactory.createMinimumDatabaseCredentials();

    private final ServiceInstanceDto serviceInstanceDto = new ServiceInstanceDto();
    private final ServiceKeyDto serviceKeyDto = new ServiceKeyDto();
    private final ServiceInstanceCreationStatusDto serviceInstanceCreationStatusDto = new ServiceInstanceCreationStatusDto();

    @BeforeEach
    public void setUp() {
        serviceInstanceDto.setGuid(GUID);
        serviceKeyDto.setGuid(GUID);
        serviceInstanceCreationStatusDto.setLastOperationState("succeeded");
        serviceInstanceCreationStatusDto.setLastOperationType("create");
    }

    /**
     * Purpose: > Given valid SchemaCredentials, when calling createRealSchema, the correct SchemaCredentials is
     * returned. <br>
     * Prerequisites: > Mock JSON, CredentialStoreEntryDto, ServiceInstanceDto and ServiceKeDto. <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to return a mock token and the expected ServiceInstanceDto. <br>
     * 3. Stub CloudFoundryService to return a mock ServiceKeyDto.<br>
     * 4. Call CfUtil (class under test) to retrieve the actual SchemaCredentials. <br>
     * Expected Results: > The actual SchemaCredentials entity has the correct values.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Given valid SchemaCredentials, when calling createRealSchema, the correct SchemaCredentials is returned.")
    @Test
    public void returnSchemaCredentialsWhenCreatingRealSchema() throws CredentialStoreException, CloudFoundryException {
        // Arrange
        serviceKeyDto.setCredentials(MOCK_JSON);
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenReturn(MOCK_TOKEN);
        when(cloudFoundryService.createServiceInstance(any(String.class), any(NewServiceInstanceDto.class),
                any(boolean.class))).thenReturn(serviceInstanceDto);
        when(cloudFoundryService.createServiceKey(any(String.class), any(NewServiceKeyDto.class)))
                .thenReturn(serviceKeyDto);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        SchemaCredentials actual = cfUtil.createRealSchema(schemaCredentials);

        // Assert
        assertEquals(DbTypeEnum.HANA_SCHEMA.name(), actual.getMetadata().getSchemaType());
        assertEquals("mock-schema", actual.getMetadata().getSchema());
    }

    /**
     * Purpose: > Given invalid credentials JSON, when calling createRealSchema, an exception is thrown. <br>
     * Prerequisites: > Mock JSON, CredentialStoreEntryDto, ServiceInstanceDto and ServiceKeDto. <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to return a mock token and the expected ServiceInstanceDto. <br>
     * 3. Stub CloudFoundryService to return a mock ServiceKeyDto with wrong JSON.<br>
     * 4. Call CfUtil (class under test) to throw an exception. <br>
     * Expected Results: > A DbManagerException was thrown.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Given invalid credentials JSON, when calling createRealSchema, an exception is thrown.")
    @Test
    public void throwExceptionWhenSchemaCredentialsJsonIsNotParsed()
            throws CloudFoundryException, CredentialStoreException {
        // Arrange
        serviceKeyDto.setCredentials("wrong json");
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenReturn(MOCK_TOKEN);
        when(cloudFoundryService.createServiceInstance(any(String.class), any(NewServiceInstanceDto.class),
                any(boolean.class))).thenReturn(serviceInstanceDto);
        when(cloudFoundryService.createServiceKey(any(String.class), any(NewServiceKeyDto.class)))
                .thenReturn(serviceKeyDto);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        assertThrows(DbManagerException.class, () -> cfUtil.createRealSchema(schemaCredentials));
    }

    /**
     * Purpose: > Given incorrectly retrieved token (something went wrong when getting/parsing/extracting the token),
     * when calling createRealSchema, an exception is thrown. <br>
     * Prerequisites: > Mock CredentialStoreEntryDto. <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to throw an exception when calling getTokenFor for wrong UserPwdDto. <br>
     * 4. Call CfUtil (class under test) to throw an exception.<br>
     * Expected Results: > A DbManagerException was thrown.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Given incorrectly retrieved token, when calling createRealSchema, an exception is thrown.")
    @Test
    public void throwExceptionWhenCreatingRealSchema() throws CredentialStoreException, CloudFoundryException {
        // Arrange
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenThrow(CloudFoundryException.class);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        assertThrows(DbManagerException.class, () -> cfUtil.createRealSchema(schemaCredentials));
    }

    /**
     * Purpose: > Given valid DatabaseCredentials, when calling createRealDatabase, the correct DatabaseCredentials is
     * returned. <br>
     * Prerequisites: > Mock JSON, CredentialStoreEntryDto, ServiceInstanceDto and ServiceInstanceCreationStatusDto.
     * <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to return a mock token and the expected ServiceInstanceDto. <br>
     * 3. Stub CloudFoundryService to return a mock ServiceInstanceCreationStatusDto.<br>
     * 4. Call CfUtil (class under test) to retrieve the actual DatabaseCredentials. <br>
     * Expected Results: > The actual DatabaseCredentials entity has the correct values.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Given valid DatabaseCredentials, when calling createRealDatabase, the correct DatabaseCredentials is returned.")
    @Test
    public void returnDatabaseCredentialsWhenCreatingRealDatabase()
            throws CloudFoundryException, CredentialStoreException {
        // Arrange
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenReturn(MOCK_TOKEN);
        when(cloudFoundryService.createServiceInstance(any(String.class), any(NewServiceInstanceDto.class),
                any(boolean.class))).thenReturn(serviceInstanceDto);
        when(cloudFoundryService.retrieveServiceInstanceCreationStatus(any(String.class), any(String.class)))
                .thenReturn(serviceInstanceCreationStatusDto);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        cfUtil.instanceNameMaxLength = 100;
        DatabaseCredentials actual = cfUtil.createRealDatabase(databaseCredentials);

        // Assert
        assertEquals(serviceInstanceDto.getGuid(), actual.getMetadata().getServiceInstanceGuid());
        assertEquals(databaseCredentials.getMetadata().getTenantGuid(), actual.getMetadata().getTenantGuid());
    }

    /**
     * Purpose: > Given incorrectly retrieved token (something went wrong when getting/parsing/extracting the token),
     * when calling createRealDatabase, an exception is thrown. <br>
     * Prerequisites: > Mock CredentialStoreEntryDto. <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to throw an exception when calling getTokenFor for wrong UserPwdDto. <br>
     * 3. Call CfUtil (class under test) to throw an exception.<br>
     * Expected Results: > A DbManagerException was thrown.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Given incorrectly retrieved token, when calling createRealDatabase, an exception is thrown.")
    @Test
    public void throwExceptionWhenCreatingRealDatabase() throws CredentialStoreException, CloudFoundryException {
        // Arrange
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenThrow(CloudFoundryException.class);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        assertThrows(DbManagerException.class, () -> cfUtil.createRealDatabase(databaseCredentials));
    }

    /**
     * Purpose: > Return SchemaCredentials when calling deleteRealSchema. <br>
     * Prerequisites: > Mock CredentialStoreEntryDto and token. <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to return the mock Token. <br>
     * 3. Call CfUtil (class under test) to return the actual SchemaCredentials.<br>
     * Expected Results: > The actual SchemaCredentials entity has the right credentials.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Return SchemaCredentials when calling deleteRealSchema. ")
    @Test
    public void returnSchemaCredentialsWhenDeletingRealSchema() throws CloudFoundryException, CredentialStoreException {
        // Arrange
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenReturn(MOCK_TOKEN);
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        SchemaCredentials actual = cfUtil.deleteRealSchema(schemaCredentials);

        // Assert
        assertEquals(schemaCredentials.getCredentials(), actual.getCredentials());
    }

    /**
     * Purpose: > Given incorrectly retrieved token (something went wrong when getting/parsing/extracting the token),
     * when calling createRealDatabase, an exception is thrown. <br>
     * Prerequisites: > Mock CredentialStoreEntryDto. <br>
     * Design Steps: > 1. Stub CredentialStoreService to return the expected CredentialStoreEntryDto. <br>
     * 2. Stub CloudFoundryService to throw an exception when calling getTokenFor for wrong UserPwdDto. <br>
     * 4. Call CfUtil (class under test) to throw an exception.<br>
     * Expected Results: > A DbManagerException was thrown.<br>
     * 
     * @throws CredentialStoreException,
     *             CloudFoundryException
     */
    @DisplayName("Given incorrectly retrieved token, when calling createRealDatabase, an exception is thrown.")
    @Test
    public void throwExceptionWhenDeletingRealSchema() throws CredentialStoreException, CloudFoundryException {
        // Arrange
        when(credentialStoreService.getPassword(NAMESPACE, CF_USER_PWD)).thenReturn(credentialStoreEntryDto);
        when(cloudFoundryService.getTokenFor(any(UserPwdDto.class))).thenThrow(CloudFoundryException.class);
        cfUtil = new CfUtil(mock(DbManagerConfig.class), credentialStoreService, cloudFoundryService);

        // Act
        assertThrows(DbManagerException.class, () -> cfUtil.deleteRealSchema(schemaCredentials));
    }
}
