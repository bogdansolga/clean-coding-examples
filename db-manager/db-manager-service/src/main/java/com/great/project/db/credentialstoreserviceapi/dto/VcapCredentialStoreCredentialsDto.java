package com.great.project.db.credentialstoreserviceapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class VcapCredentialStoreCredentialsDto {
    private String username;
    private String password;
    private String url;
    @JsonProperty("encryption")
    private CryptoKeysDto cryptoKeys;
}
