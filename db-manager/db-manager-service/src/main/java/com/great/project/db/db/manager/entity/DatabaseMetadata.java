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
public class DatabaseMetadata {
    private String tenantGuid;
    private String tenantName;

    private String dbType;

    private String servicePlanGuid;
    private String serviceInstanceGuid;
    private String serviceInstanceName;

    // goes up at every subscription (and vice-versa)
    private Long subscriptionCount;

    private String status;
    private String statusMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime markedForDeletionAt;
    private LocalDateTime deletedAt;

    public void clearMarkedForDeletion() {
        this.markedForDeletionAt = null;
    }
}
