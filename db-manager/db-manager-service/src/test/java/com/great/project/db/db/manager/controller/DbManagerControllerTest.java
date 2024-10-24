package com.great.project.db.db.manager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.great.project.db.db.manager.config.DbManagerConfig;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.service.DbManagerService;
import com.great.project.db.test.util.DbManagerDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.great.project.db.db.manager.dto.CreateTenantSchemaRequestDto;
import com.great.project.resiliency.ResilienceWrapper;

/**
 * Test {@link DbManagerController}
 */
@ExtendWith(MockitoExtension.class)
public class DbManagerControllerTest {

    private static final String TENANT = "tenant";
    private static final String MODULE = "module";
    private static final String SPACE = "space";
    private static final String SCHEMA = "schema";

    @Mock
    private DbManagerService dbManagerService;
    @Spy
    private ResilienceWrapper resilienceWrapper;
    @Mock
    private DbManagerConfig dbMnagerConfig;
    @InjectMocks
    private DbManagerController dbManagerController;

    private SchemaCredentials schemaCredentials = DbManagerDataFactory.createMinimumSchemaCredentials();
    private CreateTenantSchemaRequestDto createTenantSchemaRequestDto = DbManagerDataFactory
            .createMinimumCreateTenantSchemaRequestDto();

    @BeforeEach
    public void setUp() {
        when(dbMnagerConfig.getMaxConcurrentCalls()).thenReturn(5);
        when(dbMnagerConfig.getMaxWaitDurationInSeconds()).thenReturn(5);
    }

    /**
     * Purpose: > Given a mock CreateTenantSchemaRequestDto, when calling createTenantSchemaFor method, the correct DTO
     * is returned. <br>
     * Prerequisites: > mock CreateTenantSchemaRequestDto<br>
     * Design Steps: > First, mock DbManagerService to return the correct DTO. Second, call DbManagerController (class
     * under test) to retrieve the actual DTO. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     */
    @DisplayName("Given a mock CreateTenantSchemaRequestDto, when calling createTenantSchemaFor method, the correct DTO is returned.")
    @Test
    public void createTenantSchemaForRequest() {
        // Arrange
        when(dbManagerService.createTenantSchemaFor(createTenantSchemaRequestDto)).thenReturn(schemaCredentials);

        // Act
        SchemaCredentials actualSchemaCredentials = (SchemaCredentials) dbManagerController
                .createTenantSchemaFor(createTenantSchemaRequestDto).getBody();

        // Assert
        assertEquals(schemaCredentials.getCredentials(), actualSchemaCredentials.getCredentials());
    }

    /**
     * Purpose: > Given a mock CreateTenantSchemaRequestDto, when calling getTenantSchemaFor method, the correct DTO is
     * returned. <br>
     * Prerequisites: > mock CreateTenantSchemaRequestDto<br>
     * Design Steps: > First, mock DbManagerService to return the correct DTO. Second, call DbManagerController (class
     * under test) to retrieve the actual DTO. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     */
    @DisplayName("Given a mock CreateTenantSchemaRequestDto, when calling getTenantSchemaFor method, the correct DTO is returned.")
    @Test
    public void getCorrectDtoWhenRetrivingTenantSchemaForTenantModuleAndSpace() {
        // Arrange
        when(dbManagerService.getTenantSchemaFor(TENANT, MODULE, SPACE)).thenReturn(schemaCredentials);

        // Act
        SchemaCredentials actualSchemaCredentials = (SchemaCredentials) dbManagerController
                .getTenantSchemaFor(TENANT, MODULE, SPACE).getBody();

        // Assert
        assertEquals(schemaCredentials.getCredentials(), actualSchemaCredentials.getCredentials());
    }

    /**
     * Purpose: > Given a mock CreateTenantSchemaRequestDto, when calling getSchemaByInstanceName method, the correct
     * DTO is returned. <br>
     * Prerequisites: > mock CreateTenantSchemaRequestDto<br>
     * Design Steps: > First, mock DbManagerService to return the correct DTO. Second, call DbManagerController (class
     * under test) to retrieve the actual DTO. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     */
    @DisplayName("Given a mock CreateTenantSchemaRequestDto, when calling getSchemaByInstanceName method, the correct DTO is returned.")
    @Test
    public void getCorrectDtoWhenRetrivingTenantSchemaByInstanceName() {
        // Arrange
        when(dbManagerService.getTenantSchemaByInstanceName(SCHEMA)).thenReturn(schemaCredentials);

        // Act
        SchemaCredentials actualSchemaCredentials = (SchemaCredentials) dbManagerController
                .getTenantSchemaByInstanceName(SCHEMA).getBody();

        // Assert
        assertEquals(schemaCredentials.getCredentials(), actualSchemaCredentials.getCredentials());
    }

    /**
     * Purpose: > When calling deleteTenantSchemaFor then DbManagerService.deleteTenantSchemaFor is called exactly one
     * time. <br>
     * Prerequisites: > N/A <br>
     * Design Steps: > First call deleteTenantSchemaFor in DbManagerController (class under test). Second, verify that
     * the deleteTenantSchemaFor method in DbManagerService is called.<br>
     * Expected Results: > The method is called one time.<br>
     */
    @DisplayName("When calling deleteTenantSchemaFor then DbManagerService.deleteTenantSchemaFor is called exactly one time.")
    @Test
    public void deleteTenantSchemaForTenantModuleSpaceAndBooleanIsCalledOnce() {
        // Act
        dbManagerController.deleteTenantSchemaFor(TENANT, MODULE, SPACE, true);

        // Verify
        Mockito.verify(dbManagerService).deleteTenantSchemaFor(TENANT, MODULE, SPACE, true);
    }

    /**
     * Purpose: > When calling deleteTenantSchemaFor then DbManagerService.deleteTenantSchemaByInstanceName is called
     * exactly one time. <br>
     * Prerequisites: > N/A <br>
     * Design Steps: > First call deleteTenantSchemaById in DbManagerController (class under test). Second, verify that
     * the deleteTenantSchemaFor method in DbManagerService is called.<br>
     * Expected Results: > The method is called one time.<br>
     */
    @DisplayName("When calling deleteTenantSchemaByInstanceName then DbManagerService.deleteTenantSchemaByInstanceName is called exactly one time.")
    @Test
    public void deleteTenantSchemaByInstanceNameAndBooleanIsCalledOnce() {
        // Act
        dbManagerController.deleteTenantSchemaByInstanceName(SCHEMA, true);

        // Verify
        Mockito.verify(dbManagerService).deleteTenantSchemaByInstanceName(SCHEMA, true);
    }

}
