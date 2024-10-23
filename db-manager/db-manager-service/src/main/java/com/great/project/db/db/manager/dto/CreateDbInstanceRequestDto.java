package com.great.project.db.db.manager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateDbInstanceRequestDto {
    private String tenantGuid;
    @JsonProperty("space_guid")
    private String spaceGuid;
    private String name;
}
