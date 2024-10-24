package com.great.project.db.credentialstoreserviceapi.util;

import java.nio.charset.StandardCharsets;

import com.great.project.db.credentialstoreserviceapi.dto.VcapCredentialStoreCredentialsDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import org.apache.commons.codec.binary.Base64;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Class to pack and send the requests to Cloud Foundry API for the Credential Store
 */
@Slf4j
@Component
public class RequestUtil {

    // Allows alphanumeric characters a-zA-Z0-9 and = _ . + -
    private static final String VALIDATION_REGEX = "[a-zA-Z0-9=_.+-]+";
    public static final String HEADERS_VALIDATION_FAILED_MESSAGE = "Headers validation failed";

    private static final String NAME_QUERY = "?name=";

    private final RestTemplateBuilder restTemplateBuilder;
    private final VcapCredentialStoreCredentialsDto credentialStoreCredentials;

    public RequestUtil(RestTemplateBuilder restTemplateBuilder,
            VcapCredentialStoreCredentialsDto credentialStoreCredentials) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.credentialStoreCredentials = credentialStoreCredentials;
    }

    /**
     * Method to send the actual request
     * 
     * @param httpMethod
     *            HTTP method to send the request (GET, POST, DELETE, etc)
     * @param path
     *            the path added to the basic url to complete it
     * @param namespace
     *            the namespace from the Credential Store for which the request is done
     * @param name
     *            name of the desired credentials in the form module.space
     * @param requestBody
     *            the body of the request
     * @return a standard response entity
     * @throws CredentialStoreException
     */
    public ResponseEntity<String> execute(HttpMethod httpMethod, String path, String namespace, String name,
            String requestBody) throws CredentialStoreException {
        log.info("Http Method: " + httpMethod + ", path: " + path + ", namespace: " + namespace + ", name: " + name);
        if (requestBody == null) {
            requestBody = "";
        }

        if (name != null) {
            path += NAME_QUERY;
            path += name;
        }

        String url = credentialStoreCredentials.getUrl() + "/" + path;
        String auth = credentialStoreCredentials.getUsername() + ":" + credentialStoreCredentials.getPassword();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "jose"));

        /*
         * Fortify finding, resolution is described in ADO item 222486
         */
        headers.add("Authorization", "Basic " + validateHeaders(new String(encodedAuth)));
        headers.add("sapcp-credstore-namespace", validateHeaders(namespace));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        RestTemplate template = restTemplateBuilder.build();

        try {
            return template.exchange(Jsoup.clean(url, Whitelist.basicWithImages()), httpMethod, request, String.class);
        } catch (RestClientException e) {
            throw new CredentialStoreException(e.getMessage(), e);
        }
    }

    /*
     * Fortify finding, resolution is described in ADO item 222485 ; Documentation on Wiki page, chapter 'Header
     * Manipulation': https://wiki.cerner.com/x/Q4APlg ; Fortify Rule which trust this validation needs to be created
     */
    public static String validateHeaders(String header) {
        if (!header.matches(VALIDATION_REGEX)) {
            throw new IllegalArgumentException(HEADERS_VALIDATION_FAILED_MESSAGE);
        }
        return header;
    }
}
