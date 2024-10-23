package com.great.project.db.cloudfoundryapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NewServiceKeyDto {
    private String name;
    @JsonProperty("service_instance_guid")
    private String serviceInstanceGuid;
}
