package com.great.project.db.db.manager.controller;

import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.great.project.db.db.manager.config.DbManagerConfig;
import com.great.project.db.db.manager.dto.CreateTenantSchemaRequestDto;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.service.DbManagerService;
import com.cerner.augero.resiliency.ResilienceOptions;
import com.cerner.augero.resiliency.ResilienceWrapper;
import com.sap.cloud.sdk.cloudplatform.resilience.ResilienceRuntimeException;
import com.sap.hcp.cf.logging.common.LogContext;

import lombok.extern.slf4j.Slf4j;

/**
 * The controller that exposes the endpoint for the functionality provided by the DB Manager
 * 
 * @author Norbert Paukner <norbert.paukner@cerner.com>
 */
@RestController
@RequestMapping("/v1/tenants")
@Slf4j
public class DbManagerController {

    private static final String TOO_MANY_REQUESTS = "Too many requests!";

    private final DbManagerService dbManagerService;
    private final ResilienceWrapper resilienceWrapper;
    private final DbManagerConfig dbManagerConfig;

    public DbManagerController(DbManagerService dbManagerService, ResilienceWrapper resilienceWrapper,
            DbManagerConfig dbManagerConfig) {
        this.dbManagerService = dbManagerService;
        this.resilienceWrapper = resilienceWrapper;
        this.dbManagerConfig = dbManagerConfig;
    }

    @ExceptionHandler(ResilienceRuntimeException.class)
    public ResponseEntity<Object> handleResilienceRuntimeException(ResilienceRuntimeException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(TOO_MANY_REQUESTS);
    }

    // The framework binder used for binding the HTTP request parameters to the model
    // needs to bee explicitly configured to disallow certain attributes in order to avoid "Mass Assignment: Insecure
    // Binder Configuration" vulnerability
    // reported by Fortify .
    @InitBinder
    public void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields((String.valueOf(dbManagerConfig.getMaxConcurrentCalls())),
                String.valueOf(dbManagerConfig.getMaxWaitDurationInSeconds()));
    }

    /**
     * Creates a new schema for an existing tenant coresponding to the desired module If the tenant doesn't have already
     * a dedicated database (HANA Cloud) instance, it will be created
     * 
     * @param request
     *            the request DTO provides the name/guid of the tenant, the module for which a schema is to be created
     *            and the Cloud Foundry space. Note that the space provided here is to locate the entry in the
     *            Credential Store and may not match the space where the database instance or the schema is created.
     * @return returns the credentials for the newly created schema
     */
    @PostMapping
    public ResponseEntity<Object> createTenantSchemaFor(@RequestBody CreateTenantSchemaRequestDto request) {
        final Supplier<SchemaCredentials> operationSupplier = buildSupplierForCreateOperation(request);
        final ResilienceOptions resilienceOptions = buildResilienceOptions();
        return ResponseEntity.ok()
                .body(resilienceWrapper.performOperation("dbCreateOperation", operationSupplier, resilienceOptions));
    }

    private Supplier<SchemaCredentials> buildSupplierForCreateOperation(CreateTenantSchemaRequestDto request) {
        return () -> {
            setSecurityContext();
            return dbManagerService.createTenantSchemaFor(request);
        };
    }

    /**
     * Gets the credentials for an existing database schema.
     * 
     * @param tenant
     * @param module
     * @param space
     * @return the credentials
     */
    @GetMapping
    public ResponseEntity<Object> getTenantSchemaFor(@RequestParam String tenant, @RequestParam String module,
            @RequestParam String space) {
        final Supplier<SchemaCredentials> operationSupplier = buildSupplierForGetSchemaOperation(tenant, module, space);
        final ResilienceOptions resilienceOptions = buildResilienceOptions();
        return ResponseEntity.ok()
                .body(resilienceWrapper.performOperation("getDbSchemaOperation", operationSupplier, resilienceOptions));
    }

    private Supplier<SchemaCredentials> buildSupplierForGetSchemaOperation(String tenant, String module, String space) {
        return () -> {
            setSecurityContext();
            return dbManagerService.getTenantSchemaFor(tenant, module, space);
        };
    }

    /**
     * Gets the credentials for an existing database schema by schema Service Instance Name.
     * 
     * @param schemaInstanceName
     * @return the credentials
     */
    @GetMapping("/schema")
    public ResponseEntity<Object> getTenantSchemaByInstanceName(@RequestParam String schemaInstanceName) {
        final Supplier<SchemaCredentials> operationSupplier = buildSupplierForGetSchemaByInstanceNameOperation(
                schemaInstanceName);
        final ResilienceOptions resilienceOptions = buildResilienceOptions();
        return ResponseEntity.ok().body(resilienceWrapper.performOperation("getDbSchemaByInstanceNameOperation",
                operationSupplier, resilienceOptions));
    }

    private Supplier<SchemaCredentials> buildSupplierForGetSchemaByInstanceNameOperation(String schemaInstanceName) {
        return () -> {
            setSecurityContext();
            return dbManagerService.getTenantSchemaByInstanceName(schemaInstanceName);
        };
    }

    /**
     * Deletes an existing schema
     * 
     * @param tenant
     * @param module
     * @param space
     * @param deleteImmediatly
     *            if this is set to true, the schema is deleted immediately. if it is set to false (the default) the
     *            schema is only marked for deletion in the Credential Store.
     */
    @DeleteMapping
    public void deleteTenantSchemaFor(@RequestParam String tenant, @RequestParam String module,
            @RequestParam String space, @RequestParam(defaultValue = "false") boolean deleteImmediatly) {
        final Supplier<Object> operationSupplier = buildSupplierForDeleteSchemaOperation(tenant, module, space,
                deleteImmediatly);
        final ResilienceOptions resilienceOptions = buildResilienceOptions();
        resilienceWrapper.performOperation("deleteDbSchemaOperation", operationSupplier, resilienceOptions);
    }

    private Supplier<Object> buildSupplierForDeleteSchemaOperation(String tenant, String module, String space,
            boolean deleteImmediatly) {
        return () -> {
            setSecurityContext();
            dbManagerService.deleteTenantSchemaFor(tenant, module, space, deleteImmediatly);
            return new Object();
        };
    }

    /**
     * Deletes an existing schema by it's Service Instance Name
     * 
     * @param schemaInstanceName
     * @param deleteImmediatly
     *            if this is set to true, the schema is deleted immediately. if it is set to false (the default) the
     *            schema is only marked for deletion in the Credential Store.
     */
    @DeleteMapping("/schema")
    public void deleteTenantSchemaByInstanceName(@RequestParam String schemaInstanceName,
            @RequestParam(defaultValue = "false") boolean deleteImmediatly) {
        final Supplier<Object> operationSupplier = buildSupplierForDeleteSchemaByInstanceNameOperation(
                schemaInstanceName, deleteImmediatly);
        final ResilienceOptions resilienceOptions = buildResilienceOptions();
        resilienceWrapper.performOperation("deleteDbSchemaByInstanceNameOperation", operationSupplier,
                resilienceOptions);
    }

    private Supplier<Object> buildSupplierForDeleteSchemaByInstanceNameOperation(String schemaInstanceName,
            boolean deleteImmediatly) {
        return () -> {
            setSecurityContext();
            dbManagerService.deleteTenantSchemaByInstanceName(schemaInstanceName, deleteImmediatly);
            return new Object();
        };
    }

    private void setSecurityContext() {
        SecurityContext context = SecurityContextHolder.getContext();
        String correlationId = LogContext.getCorrelationId();
        SecurityContextHolder.setContext(context);
        LogContext.initializeContext(correlationId);
    }

    private ResilienceOptions buildResilienceOptions() {
        return ResilienceOptions.builder()
                .withBulkhead(dbManagerConfig.getMaxConcurrentCalls(), dbManagerConfig.getMaxWaitDurationInSeconds())
                .build();
    }
}
