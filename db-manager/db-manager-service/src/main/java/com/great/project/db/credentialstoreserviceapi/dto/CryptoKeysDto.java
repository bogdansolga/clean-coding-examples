package com.great.project.db.credentialstoreserviceapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CryptoKeysDto {
    @JsonProperty("client_private_key")
    private String clienPrivateKey;
    @JsonProperty("server_public_key")
    private String serverPublicKey;
}
