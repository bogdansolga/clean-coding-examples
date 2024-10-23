package com.great.project.db.cloudfoundryapi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NewServiceInstanceDto {
    @JsonProperty("space_guid")
    private String spaceGuid;
    private String name;
    @JsonProperty("service_plan_guid")
    private String servicePlanGuid;
    @JsonRawValue
    private String parameters;
    private List<String> tags;
}
