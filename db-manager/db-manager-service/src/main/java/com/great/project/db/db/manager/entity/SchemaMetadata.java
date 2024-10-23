package com.great.project.db.db.manager.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class SchemaMetadata {
    private String tenantGuid;
    private String tenantName;

    private String module;
    private String space;

    private String schemaType;

    private String servicePlanGuid;
    private String serviceInstanceGuid;
    private String serviceInstanceName;
    private String serviceKeyGuid;
    private String serviceKeyName;

    private String schema;
    private Long subscriptionCount;

    private String status;
    private String statusMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime markedForDeletionAt;
    private LocalDateTime deletedAt;

    private String databaseId;
    private String databaseName;

    public void clearMarkedForDeletion() {
        this.markedForDeletionAt = null;
    }
}
