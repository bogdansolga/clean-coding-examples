package com.great.project.db.credentialstoreserviceapi.dto;

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
public class CredentialStoreEntryDto {
    private String id;
    private String name;
    private String modifiedAt;
    private String metadata;
    private String value;
    private String username;
    private String type;
}
