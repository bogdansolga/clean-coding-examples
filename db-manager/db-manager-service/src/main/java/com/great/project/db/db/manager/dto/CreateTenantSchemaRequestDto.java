package com.great.project.db.db.manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateTenantSchemaRequestDto {
    private String tenantGuid;
    private String module;
    private String space;
}
