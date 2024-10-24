package com.great.project.db.credentialstoreserviceapi.config;

import com.great.project.db.credentialstoreserviceapi.dto.CryptoKeysDto;
import com.great.project.db.credentialstoreserviceapi.dto.VcapCredentialStoreCredentialsDto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.great.project.core.util.VcapServicesUtil;

@Configuration
public class CredentialStoreConfig {

    @Value("${dbManager.credentialStoreServiceInstanceName}")
    private String credentialStoreServiceInstanceName;

    @Bean
    public VcapCredentialStoreCredentialsDto credentialStoreCredentials(VcapServicesUtil vcapServicesUtil) {
        JSONObject credJson = vcapServicesUtil.getServiceCredentialsByInstanceName(credentialStoreServiceInstanceName);
        return vcapServicesUtil.getCredentialsFromJSON(credJson, VcapCredentialStoreCredentialsDto.class);
    }

    @Bean
    public CryptoKeysDto cryptoKeys(VcapServicesUtil vcapServicesUtil) {
        return credentialStoreCredentials(vcapServicesUtil).getCryptoKeys();
    }
}
