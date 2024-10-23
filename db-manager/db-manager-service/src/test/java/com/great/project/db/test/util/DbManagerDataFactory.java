package com.great.project.db.test.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.great.project.db.db.manager.dto.CreateTenantSchemaRequestDto;
import com.great.project.db.db.manager.entity.DatabaseCredentials;
import com.great.project.db.db.manager.entity.DatabaseMetadata;
import com.great.project.db.db.manager.entity.SchemaCredentials;
import com.great.project.db.db.manager.entity.SchemaMetadata;

/**
 * Class to create mock objects for SchemaCredentials, CreateTenantSchemaRequestDto, DatabaseCredentials
 * 
 * @author Gabriela Maciac
 */
public class DbManagerDataFactory {

    private DbManagerDataFactory() {

    }

    private static final String CREDENTIALS = "credentials";
    private static final String TENANT_GUID = "tenant-guid";
    private static final String TENANT_NAME = "tenant-name";
    private static final String MODULE = "module";
    private static final String SPACE = "space";
    private static final String SCHEMA_TYPE = "schema type";
    private static final String SERVICE_PLAN_GUID = "service-plan-guid";
    private static final String SERVICE_INSTANCE_GUID = "service-instance-guid";
    private static final String SERVICE_INSTANCE_NAME = "service-instance-name";
    private static final String SERVICE_KEY_GUID = "service-key-guid";
    private static final String SCHEMA = "schema";
    private static final long SUBSCRIPTION_COUNT = 1L;
    private static final String STATUS_MESSAGE = "status-message";

    private static final String SAMPLE_DATE = "2016-03-04 11:30";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final LocalDateTime CREATED_AT = LocalDateTime.parse(SAMPLE_DATE, DATE_FORMATTER);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.parse(SAMPLE_DATE, DATE_FORMATTER);
    private static final LocalDateTime MARKED_FOR_DELETION_AT = LocalDateTime.parse(SAMPLE_DATE, DATE_FORMATTER);
    private static final LocalDateTime DELETED_AT = LocalDateTime.parse(SAMPLE_DATE, DATE_FORMATTER);

    private static final String DATABASE_ID = "database-id";
    private static final String DATABASE_NAME = "database-name";

    private static final String DATABASE_METADATA_EXTRA = "database-metadata";
    private static final String DB_TYPE = "db-type";
    private static final String STATUS = "status";

    public static SchemaCredentials createMinimumSchemaCredentials() {
        SchemaCredentials schemaCredentials = new SchemaCredentials();

        SchemaMetadata schemaMetadata = new SchemaMetadata();
        schemaMetadata.setTenantGuid(TENANT_GUID);
        schemaMetadata.setTenantName(TENANT_NAME);
        schemaMetadata.setModule(MODULE);
        schemaMetadata.setSpace(SPACE);
        schemaMetadata.setSchemaType(SCHEMA_TYPE);
        schemaMetadata.setServicePlanGuid(SERVICE_PLAN_GUID);
        schemaMetadata.setServiceInstanceGuid(SERVICE_INSTANCE_GUID);
        schemaMetadata.setServiceKeyGuid(SERVICE_KEY_GUID);
        schemaMetadata.setSchema(SCHEMA);
        schemaMetadata.setServiceInstanceName(SERVICE_INSTANCE_NAME);
        schemaMetadata.setSubscriptionCount(SUBSCRIPTION_COUNT);
        schemaMetadata.setStatusMessage(STATUS_MESSAGE);
        schemaMetadata.setCreatedAt(CREATED_AT);
        schemaMetadata.setUpdatedAt(UPDATED_AT);
        schemaMetadata.setMarkedForDeletionAt(MARKED_FOR_DELETION_AT);
        schemaMetadata.setDeletedAt(DELETED_AT);
        schemaMetadata.setDatabaseId(DATABASE_ID);
        schemaMetadata.setDatabaseName(DATABASE_NAME);

        schemaCredentials.setCredentials(CREDENTIALS);
        schemaCredentials.setMetadata(schemaMetadata);

        return schemaCredentials;
    }

    public static CreateTenantSchemaRequestDto createMinimumCreateTenantSchemaRequestDto() {
        CreateTenantSchemaRequestDto createTenantSchemaRequestDto = new CreateTenantSchemaRequestDto();

        createTenantSchemaRequestDto.setModule(MODULE);
        createTenantSchemaRequestDto.setSpace(SPACE);
        createTenantSchemaRequestDto.setTenantGuid(TENANT_GUID);

        return createTenantSchemaRequestDto;
    }

    public static DatabaseCredentials createMinimumDatabaseCredentials() {
        DatabaseCredentials databaseCredentials = new DatabaseCredentials();
        DatabaseMetadata databaseMetadata = new DatabaseMetadata();

        databaseMetadata.setTenantGuid(TENANT_GUID + DATABASE_METADATA_EXTRA);
        databaseMetadata.setTenantName(TENANT_NAME + DATABASE_METADATA_EXTRA);
        databaseMetadata.setDbType(DB_TYPE);
        databaseMetadata.setServicePlanGuid(SERVICE_PLAN_GUID + DATABASE_METADATA_EXTRA);
        databaseMetadata.setServiceInstanceGuid(SERVICE_INSTANCE_GUID + DATABASE_METADATA_EXTRA);
        databaseMetadata.setServiceInstanceName(SERVICE_INSTANCE_NAME + DATABASE_METADATA_EXTRA);
        databaseMetadata.setSubscriptionCount(SUBSCRIPTION_COUNT);
        databaseMetadata.setStatusMessage(STATUS_MESSAGE + DATABASE_METADATA_EXTRA);
        databaseMetadata.setStatus(STATUS);
        databaseMetadata.setCreatedAt(CREATED_AT);
        databaseMetadata.setUpdatedAt(UPDATED_AT);
        databaseMetadata.setMarkedForDeletionAt(MARKED_FOR_DELETION_AT);
        databaseMetadata.setDeletedAt(DELETED_AT);

        databaseCredentials.setCredentials(CREDENTIALS + DATABASE_METADATA_EXTRA);
        databaseCredentials.setMetadata(databaseMetadata);

        return databaseCredentials;
    }
}
