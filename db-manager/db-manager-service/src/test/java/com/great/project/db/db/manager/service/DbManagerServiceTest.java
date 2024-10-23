package com.great.project.db.db.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.repo.DatabaseCredentialsRepository;
import com.great.project.db.db.manager.repo.SchemaCredentialsRepository;
import com.great.project.db.db.manager.util.CfUtil;
import com.great.project.db.db.manager.util.DbManagerException;
import com.great.project.db.db.manager.util.StatusEnum;
import com.great.project.db.test.util.DbManagerDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.great.project.db.db.manager.dto.CreateTenantSchemaRequestDto;

/**
 * Test {@link DbManagerService}
 * 
 * @author Gabriela Maciac
 */
public class DbManagerServiceTest {

    private static final String MOCK_TENANT = "tenant";
    private static final String MOCK_MODULE = "module";
    private static final String MOCK_SPACE = "space";
    private static final String MOCK_SCHEMA = "schema";
    private static final String SCHEMA_CREDENTIALS_NULL = "Schema credentials null.";

    private SchemaCredentialsRepository schemaCredentialsRepository;
    private DatabaseCredentialsRepository databaseCredentialsRepository;
    private CfUtil cfUtil;
    private DbManagerService dbManagerService;

    private SchemaCredentials schemaCredentialsEntity = DbManagerDataFactory.createMinimumSchemaCredentials();
    private CreateTenantSchemaRequestDto createTenantSchemaRequestDto = DbManagerDataFactory
            .createMinimumCreateTenantSchemaRequestDto();
    private DatabaseCredentials databaseCredentials = DbManagerDataFactory.createMinimumDatabaseCredentials();

    @BeforeEach
    public void setUp() {
        schemaCredentialsRepository = mock(SchemaCredentialsRepository.class);
        databaseCredentialsRepository = mock(DatabaseCredentialsRepository.class);
        cfUtil = mock(CfUtil.class);
        dbManagerService = new DbManagerService(cfUtil, schemaCredentialsRepository, databaseCredentialsRepository);
    }

    /**
     * Purpose: > Given valid schema parameters, when calling getTenantSchemaFor, the correct SchemaCredentials is
     * returned. <br>
     * Prerequisites: > Mock tenant, module and space.<br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return the expected SchemaCredentials entity.<br>
     * 2. Call DbManagerService (class under test) to retrieve the actual SchemaCredentials entity.<br>
     * 3. Assert the actual entity. <br>
     * Expected Results: > The expected entity has the right credentials.<br>
     */
    @DisplayName("Given valid schema parameters, when calling getTenantSchemaFor, the correct SchemaCredentials is returned.")
    @Test
    public void getSchemaCredentialsWhenRetreivingTenantSchemaForTenantModuleAndSpace() {
        // Arrange
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE))
                .thenReturn(schemaCredentialsEntity);

        // Act
        SchemaCredentials actual = dbManagerService.getTenantSchemaFor(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE);

        // Assert
        assertEquals(schemaCredentialsEntity.getCredentials(), actual.getCredentials());
    }

    /**
     * Purpose: > Given valid schema parameters, when calling getTenantSchemaByInstanceName, the correct
     * SchemaCredentials is returned. <br>
     * Prerequisites: > Mock schema.<br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return the expected SchemaCredentials entity.<br>
     * 2. Call DbManagerService (class under test) to retrieve the actual SchemaCredentials entity.<br>
     * 3. Assert the actual entity. <br>
     * Expected Results: > The expected entity has the right credentials.<br>
     */
    @DisplayName("Given valid schema parameters, when calling getTenantSchemaByInstanceName, the correct SchemaCredentials is returned.")
    @Test
    public void getSchemaCredentialsWhenRetreivingTenantSchemaByInstanceName() {
        // Arrange
        when(schemaCredentialsRepository.findBySchemaInstanceName(MOCK_SCHEMA)).thenReturn(schemaCredentialsEntity);

        // Act
        SchemaCredentials actual = dbManagerService.getTenantSchemaByInstanceName(MOCK_SCHEMA);

        // Assert
        assertEquals(schemaCredentialsEntity.getCredentials(), actual.getCredentials());
    }

    /**
     * Purpose: > Throw DbManagerException if the SchemaCredentials entity retrieved is null. <br>
     * Prerequisites: > Mock tenant, module and space.<br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return the null SchemaCredentials entity.<br>
     * 2. Call DbManagerService (class under test) to throw the exception.<br>
     * 3. Assert the exception message. <br>
     * Expected Results: > The expected exception message is equal to the actual one.<br>
     */
    @DisplayName("Throw DbManagerException if the SchemaCredentials entity retrieved is null.")
    @Test
    public void throwExceptionWhenGettingByTenantModuleAndSpaceSchemaWithNullSchemaCredentials() {
        // Arrange
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE))
                .thenReturn(null);

        // Act
        DbManagerException exception = assertThrows(DbManagerException.class,
                () -> dbManagerService.getTenantSchemaFor(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE));

        // Assert
        assertEquals(SCHEMA_CREDENTIALS_NULL, exception.getMessage());
    }

    /**
     * Purpose: > Throw DbManagerException if the SchemaCredentials entity retrieved by schema instance name is null.
     * <br>
     * Prerequisites: > Mock schema.<br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return the null SchemaCredentials entity.<br>
     * 2. Call DbManagerService (class under test) to throw the exception.<br>
     * 3. Assert the exception message. <br>
     * Expected Results: > The expected exception message is equal to the actual one.<br>
     */
    @DisplayName("Throw DbManagerException if the SchemaCredentials entity retrieved by schema instance name is null.")
    @Test
    public void throwExceptionWhenGettingTenantSchemaByInstanceNameWithNullSchemaCredentials() {
        // Arrange
        when(schemaCredentialsRepository.findBySchemaInstanceName(MOCK_SCHEMA)).thenReturn(null);

        // Act
        DbManagerException exception = assertThrows(DbManagerException.class,
                () -> dbManagerService.getTenantSchemaByInstanceName(MOCK_SCHEMA));

        // Assert
        assertEquals(SCHEMA_CREDENTIALS_NULL, exception.getMessage());
    }

    /**
     * Purpose: > Given a CreateTenantSchemaRequestDto, when calling createTenantSchemaFor, the correct
     * SchemaCredentials is returned. <br>
     * Prerequisites: > Mock DatabaseCredentials and CreateTenantSchemaRequestDto. <br>
     * Design Steps: > 1. Stub the DatabaseCredentialsRepository, SchemaCredentialsRepository and CfUtil to return the
     * expected DTOs.<br>
     * 2. Call DbManagerService (class under test) to return the actual SchemaCredentials.<br>
     * 3. Assert the returned SchemaCredentials object is equal to the actual one. <br>
     * Expected Results: > The expected SchemaCredentials object is equal to the actual one.<br>
     */
    @DisplayName("Given a CreateTenantSchemaRequestDto, when calling createTenantSchemaFor, the correct SchemaCredentials is returned.")
    @Test
    public void getSchemaCredentialsWhenCreatingTenantSchemaForRequest() {
        // Arrange
        when(databaseCredentialsRepository.findByTenantGuid(createTenantSchemaRequestDto.getTenantGuid()))
                .thenReturn(databaseCredentials);
        when(cfUtil.createRealDatabase(databaseCredentials)).thenReturn(databaseCredentials);
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(createTenantSchemaRequestDto.getTenantGuid(),
                createTenantSchemaRequestDto.getModule(), createTenantSchemaRequestDto.getSpace()))
                        .thenReturn(schemaCredentialsEntity);
        when(schemaCredentialsRepository.save(schemaCredentialsEntity)).thenReturn(schemaCredentialsEntity);

        // Act
        SchemaCredentials actual = dbManagerService.createTenantSchemaFor(createTenantSchemaRequestDto);

        // Assert
        assertEquals(schemaCredentialsEntity.getMetadata().getUpdatedAt(), actual.getMetadata().getUpdatedAt());
    }

    /**
     * Purpose: > Given a CreateTenantSchemaRequestDto, when calling createTenantSchemaFor with status
     * MARKED_FOR_DELETION, the correct SchemaCredentials is returned. <br>
     * Prerequisites: > Mock DatabaseCredentials and CreateTenantSchemaRequestDto. <br>
     * Design Steps: > 1. Stub the DatabaseCredentialsRepository, SchemaCredentialsRepository and CfUtil to return the
     * expected DTOs.<br>
     * 2. Set status as MARKED_FOR_DELETION for metadata.<br>
     * 3. Call DbManagerService (class under test) to return the actual SchemaCredentials.<br>
     * 4. Assert the returned SchemaCredentials object is equal to the actual one. <br>
     * Expected Results: > The expected SchemaCredentials object is equal to the actual one.<br>
     */
    @DisplayName("Given a CreateTenantSchemaRequestDto, when calling createTenantSchemaFor with status MARKED_FOR_DELETION, the correct "
            + "SchemaCredentials is returned.")
    @Test
    public void getSchemaCredentialsWhenCreatingTenantSchemaForRequestWithStatusMarkedForDeletion() {
        // Arrange
        when(databaseCredentialsRepository.findByTenantGuid(createTenantSchemaRequestDto.getTenantGuid()))
                .thenReturn(databaseCredentials);
        when(cfUtil.createRealDatabase(databaseCredentials)).thenReturn(databaseCredentials);
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(createTenantSchemaRequestDto.getTenantGuid(),
                createTenantSchemaRequestDto.getModule(), createTenantSchemaRequestDto.getSpace()))
                        .thenReturn(schemaCredentialsEntity);
        when(schemaCredentialsRepository.save(schemaCredentialsEntity)).thenReturn(schemaCredentialsEntity);

        schemaCredentialsEntity.getMetadata().setStatus(StatusEnum.MARKED_FOR_DELETION.name());
        databaseCredentials.getMetadata().setStatus(StatusEnum.MARKED_FOR_DELETION.name());

        // Act
        SchemaCredentials actual = dbManagerService.createTenantSchemaFor(createTenantSchemaRequestDto);

        // Assert
        assertEquals(StatusEnum.CREATED.name(), actual.getMetadata().getStatus());
        assertNull(actual.getMetadata().getMarkedForDeletionAt());
        assertNotNull(actual.getMetadata().getUpdatedAt());

        assertNull(schemaCredentialsEntity.getMetadata().getMarkedForDeletionAt());
        assertNull(databaseCredentials.getMetadata().getMarkedForDeletionAt());
    }

    /**
     * Purpose: > Given a CreateTenantSchemaRequestDto, when calling createTenantSchemaFor with null SchemaCredentials,
     * then the correct SchemaCredentials is returned. <br>
     * Prerequisites: > Mock DatabaseCredentials and CreateTenantSchemaRequestDto. <br>
     * Design Steps: > 1. Stub the DatabaseCredentialsRepository, SchemaCredentialsRepository to return the null.<br>
     * 2. Stub CfUtil to return the correct DTOs.<br>
     * 3. Call DbManagerService (class under test) to return the actual SchemaCredentials.<br>
     * 4. Assert the returned SchemaCredentials object is equal to the actual one. <br>
     * Expected Results: > The expected SchemaCredentials object is equal to the actual one.<br>
     */
    @DisplayName("Given a CreateTenantSchemaRequestDto, when calling createTenantSchemaFor with null SchemaCredentials, the correct "
            + "SchemaCredentials is returned.")
    @Test
    public void getSchemaCredentialsWhenCreatingTenantSchemaForRequestForNullCredentials() {
        // Arrange
        when(databaseCredentialsRepository.findByTenantGuid(createTenantSchemaRequestDto.getTenantGuid()))
                .thenReturn(null);
        when(cfUtil.createRealDatabase(databaseCredentials)).thenReturn(databaseCredentials);
        when(cfUtil.createRealSchema(schemaCredentialsEntity)).thenReturn(schemaCredentialsEntity);
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(createTenantSchemaRequestDto.getTenantGuid(),
                createTenantSchemaRequestDto.getModule(), createTenantSchemaRequestDto.getSpace())).thenReturn(null);
        when(schemaCredentialsRepository.save(any(SchemaCredentials.class))).thenReturn(schemaCredentialsEntity);
        when(databaseCredentialsRepository.save(any(DatabaseCredentials.class))).thenReturn(databaseCredentials);

        // Act
        SchemaCredentials actual = dbManagerService.createTenantSchemaFor(createTenantSchemaRequestDto);

        // Assert
        assertEquals(StatusEnum.CREATED.name(), actual.getMetadata().getStatus());
        assertEquals("OK", actual.getMetadata().getStatusMessage());
        assertEquals(1L, actual.getMetadata().getSubscriptionCount());
    }

    /**
     * Purpose: > Given a SchemaCredentials entity, when calling deleteTenantSchemaFor, verify that
     * SchemaCredentialsRepository.save was called. <br>
     * Prerequisites: > Mock tenant, module and space. <br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return the expected SchemaCredentials.<br>
     * 2. Call DbManagerService (class under test).<br>
     * 3. Verify that SchemaCredentialsRepository.save was called. <br>
     * Expected Results: > SchemaCredentialsRepository.save was called one time.<br>
     */
    @DisplayName("Given a SchemaCredentials entity, when calling deleteTenantSchemaFor, verify that "
            + "SchemaCredentialsRepository.save was called.")
    @Test
    public void saveSchemaWhenCallingDeleteTenantSchemaForTenantModuleAndSpace() {
        // Arrange
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE))
                .thenReturn(schemaCredentialsEntity);

        // Act
        dbManagerService.deleteTenantSchemaFor(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE, false);

        // Verify
        Mockito.verify(schemaCredentialsRepository).save(schemaCredentialsEntity);
    }

    /**
     * Purpose: > Given a SchemaCredentials entity, when calling deleteTenantSchemaFor, status of SchemaCredentials
     * entity is DELETED. <br>
     * Prerequisites: > Mock tenant, module and space. <br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository and CfUtil to return the expected SchemaCredentials.<br>
     * 2. Call DbManagerService (class under test) to delete the schema.<br>
     * 3. Assert that the status is now DELETED. <br>
     * Expected Results: > SchemaCredentials.status is DELETED.<br>
     */
    @DisplayName("Given a SchemaCredentials entity, when calling deleteTenantSchemaFor, status of SchemaCredentials"
            + " entity is DELETED.")
    @Test
    public void deleteTenantSchemaForTenantModuleSpaceAndBooleanImmediately() {
        // Arrange
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE))
                .thenReturn(schemaCredentialsEntity);
        when(schemaCredentialsRepository.save(any(SchemaCredentials.class))).thenReturn(schemaCredentialsEntity);
        when(cfUtil.deleteRealSchema(any(SchemaCredentials.class))).thenReturn(schemaCredentialsEntity);

        schemaCredentialsEntity.getMetadata().setSubscriptionCount(1L);

        // Act
        dbManagerService.deleteTenantSchemaFor(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE, true);

        // Assert
        assertEquals(StatusEnum.DELETED.name(), schemaCredentialsEntity.getMetadata().getStatus());
    }

    /**
     * Purpose: > Given a SchemaCredentials entity, when calling deleteTenantSchemaByInstanceName, status of
     * SchemaCredentials entity is DELETED. <br>
     * Prerequisites: > Mock schema <br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository and CfUtil to return the expected SchemaCredentials.<br>
     * 2. Call DbManagerService (class under test) to delete the schema.<br>
     * 3. Assert that the status is now DELETED. <br>
     * Expected Results: > SchemaCredentials.status is DELETED.<br>
     */
    @DisplayName("Given a SchemaCredentials entity, when calling deleteTenantSchemaByInstanceName, status of SchemaCredentials"
            + " entity is DELETED.")
    @Test
    public void deleteTenantSchemaByInstanceNameAndBooleanImmediately() {
        // Arrange
        when(schemaCredentialsRepository.findBySchemaInstanceName(MOCK_SCHEMA)).thenReturn(schemaCredentialsEntity);
        when(schemaCredentialsRepository.save(any(SchemaCredentials.class))).thenReturn(schemaCredentialsEntity);
        when(cfUtil.deleteRealSchema(any(SchemaCredentials.class))).thenReturn(schemaCredentialsEntity);

        schemaCredentialsEntity.getMetadata().setSubscriptionCount(1L);

        // Act
        dbManagerService.deleteTenantSchemaByInstanceName(MOCK_SCHEMA, true);

        // Assert
        assertEquals(StatusEnum.DELETED.name(), schemaCredentialsEntity.getMetadata().getStatus());
    }

    /**
     * Purpose: > Given a null SchemaCredentials entity, when calling deleteTenantSchemaFor, an exception with the
     * correct message is thrown. <br>
     * Prerequisites: > Mock tenant, module and space. <br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return null.<br>
     * 2. Call DbManagerService (class under test) to throw the exception.<br>
     * 3. Assert that the exception message is the expected one. <br>
     * Expected Results: > The exception message is the expected one.<br>
     */
    @DisplayName("Given a null SchemaCredentials entity, when calling deleteTenantSchemaFor, an exception with the correct message is thrown.")
    @Test
    public void throwExceptionWhenDeleteTenantSchemaForIsCalled() {
        // Arrange
        when(schemaCredentialsRepository.findByTenantGuidAndModuleAndSpace(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE))
                .thenReturn(null);

        // Act
        DbManagerException exception = assertThrows(DbManagerException.class,
                () -> dbManagerService.deleteTenantSchemaFor(MOCK_TENANT, MOCK_MODULE, MOCK_SPACE, false));

        // Assert
        assertEquals(SCHEMA_CREDENTIALS_NULL, exception.getMessage());
    }

    /**
     * Purpose: > Given a null SchemaCredentials entity, when calling deleteTenantSchemaByInstanceName, an exception
     * with the correct message is thrown. <br>
     * Prerequisites: > Mock schema. <br>
     * Design Steps: > 1. Stub the SchemaCredentialsRepository to return null.<br>
     * 2. Call DbManagerService (class under test) to throw the exception.<br>
     * 3. Assert that the exception message is the expected one. <br>
     * Expected Results: > The exception message is the expected one.<br>
     */
    @DisplayName("Given a null SchemaCredentials entity, when calling deleteTenantSchemaByInstanceName, an exception with the correct message is thrown.")
    @Test
    public void throwExceptionWhenDeleteTenantSchemaByInstanceNamesCalled() {
        // Arrange
        when(schemaCredentialsRepository.findBySchemaInstanceName(MOCK_SCHEMA)).thenReturn(null);

        // Act
        DbManagerException exception = assertThrows(DbManagerException.class,
                () -> dbManagerService.deleteTenantSchemaByInstanceName(MOCK_SCHEMA, false));

        // Assert
        assertEquals(SCHEMA_CREDENTIALS_NULL, exception.getMessage());
    }

}
