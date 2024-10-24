package com.great.project.db.cloudfoundryapi.service;

import com.great.project.db.cloudfoundryapi.exception.CloudFoundryException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.great.project.db.cloudfoundryapi.config.CloudFoundryConfig;
import com.great.project.db.cloudfoundryapi.dto.NewServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.NewServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.UserPwdDto;
import com.great.project.db.credentialstoreserviceapi.util.RequestUtil;
import com.great.project.db.db.manager.dto.ServiceInstanceCreationStatusDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;

/**
 * Offers Cloud Foundry interaction
 */
@Service
public class CloudFoundryService {

    private static final String LAST_OPERATION = "last_operation";
    private static final String ENTITY = "entity";

    private final RestTemplateBuilder restTemplateBuilder;
    private final CloudFoundryConfig cloudFoundryConfig;

    public CloudFoundryService(RestTemplateBuilder restTemplateBuilder, CloudFoundryConfig cloudFoundryConfig) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.cloudFoundryConfig = cloudFoundryConfig;
    }

    /**
     * Gets the Cloud Foundry authentication token necessary to perform the operations
     * 
     * @param userPwdDto
     *            dto containing the user and password pair for authentication
     * @return the token as string
     * @throws CloudFoundryException
     */
    public String getTokenFor(UserPwdDto userPwdDto) throws CloudFoundryException {
        RestTemplate template = restTemplateBuilder.build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic Y2Y6");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", userPwdDto.getUser());
        requestBody.add("password", userPwdDto.getPwd());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = template.postForEntity(cloudFoundryConfig.getLoginUrl() + "/oauth/token",
                request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(JsonSanitizer.sanitize(response.getBody()));
            return root.path("access_token").asText();
        } catch (JsonProcessingException e) {
            throw new CloudFoundryException(e.getMessage(), e);
        }
    }

    /**
     * Creates a Cloud Foundry HANA Cloud database or schema service instance
     * 
     * @param token
     * @param newServiceInstanceDto
     *            dto containing the parameters necessary to create an instance of the service
     * @param acceptsIncomplete
     *            parameter required by Cloud Foundry for some operations
     * @return a dto containing the response as string
     * @throws CloudFoundryException
     */
    public ServiceInstanceDto createServiceInstance(String token, NewServiceInstanceDto newServiceInstanceDto,
            boolean acceptsIncomplete) throws CloudFoundryException {
        RestTemplate template = restTemplateBuilder.build();
        HttpEntity<String> requestBody = new HttpEntity<>(mapToString(newServiceInstanceDto), createHeaders(token));

        MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        String url = cloudFoundryConfig.getApiUrl() + "/v2/service_instances";
        if (acceptsIncomplete) {
            url += "?accepts_incomplete=true";
        }

        ResponseEntity<String> response = template.postForEntity(url, requestBody, String.class, uriVariables);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(JsonSanitizer.sanitize(response.getBody()));
            ServiceInstanceDto serviceInstanceDto = new ServiceInstanceDto();
            serviceInstanceDto.setGuid(root.path("metadata").path("guid").asText());
            return serviceInstanceDto;
        } catch (JsonProcessingException e) {
            throw new CloudFoundryException(e.getMessage(), e);
        }
    }

    /**
     * Checks and reports the status of database instance creation
     * 
     * @param token
     * @param instanceName
     * @return the status of the operation
     * @throws CloudFoundryException
     */
    public ServiceInstanceCreationStatusDto retrieveServiceInstanceCreationStatus(String token,
            String serviceInstanceGuid) throws CloudFoundryException {
        ServiceInstanceCreationStatusDto status = new ServiceInstanceCreationStatusDto();
        RestTemplate template = restTemplateBuilder.build();
        HttpEntity<String> request = new HttpEntity<>(createHeaders(token));
        String requestUrl = cloudFoundryConfig.getApiUrl() + "/v2/service_instances/" + serviceInstanceGuid;

        ResponseEntity<String> response = template.exchange(requestUrl, HttpMethod.GET, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(JsonSanitizer.sanitize(response.getBody()));
            status.setLastOperationType(root.path(ENTITY).path(LAST_OPERATION).path("type").asText());
            status.setLastOperationState(root.path(ENTITY).path(LAST_OPERATION).path("state").asText());
        } catch (JsonProcessingException e) {
            throw new CloudFoundryException(e.getMessage(), e);
        }
        return status;
    }

    /**
     * Deletes a service instance using the Cloud Foundry API
     * 
     * @param token
     * @param serviceInstanceGuid
     */
    public void deleteServiceInstance(String token, String serviceInstanceGuid) {
        RestTemplate template = restTemplateBuilder.build();

        HttpEntity<String> request = new HttpEntity<>(createHeaders(token));

        template.exchange(cloudFoundryConfig.getApiUrl() + "/v2/service_instances/" + serviceInstanceGuid,
                HttpMethod.DELETE, request, String.class);
    }

    /**
     * Creates the service key with the credentials to access the service
     * 
     * @param token
     * @param newServiceKeyDto
     * @return
     * @throws CloudFoundryException
     */
    public ServiceKeyDto createServiceKey(String token, NewServiceKeyDto newServiceKeyDto)
            throws CloudFoundryException {
        RestTemplate template = restTemplateBuilder.build();
        HttpEntity<String> request = new HttpEntity<>(mapToString(newServiceKeyDto), createHeaders(token));

        ResponseEntity<String> response = template.postForEntity(cloudFoundryConfig.getApiUrl() + "/v2/service_keys",
                request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(JsonSanitizer.sanitize(response.getBody()));
            ServiceKeyDto serviceKeyDto = new ServiceKeyDto();
            serviceKeyDto.setGuid(root.path("metadata").path("guid").asText());
            serviceKeyDto.setCredentials(root.path(ENTITY).path("credentials").toString());
            return serviceKeyDto;
        } catch (JsonProcessingException e) {
            throw new CloudFoundryException(e.getMessage(), e);
        }
    }

    /**
     * Deletes the service key
     * 
     * @param token
     * @param serviceKeyGuid
     */
    public void deleteServiceKey(String token, String serviceKeyGuid) {
        RestTemplate template = restTemplateBuilder.build();

        HttpEntity<String> request = new HttpEntity<>(createHeaders(token));

        template.exchange(cloudFoundryConfig.getApiUrl() + "/v2/service_keys/" + serviceKeyGuid, HttpMethod.DELETE,
                request, String.class);
    }

    /**
     * Maps an object to a json formatted string
     * 
     * @param value
     * @return
     * @throws CloudFoundryException
     */
    protected static String mapToString(Object value) throws CloudFoundryException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CloudFoundryException(e.getMessage(), e);
        }
    }

    /**
     * Creates HTTP headers for a request using the given token for authentication
     * 
     * @param token
     * @return
     */
    protected static HttpHeaders createHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + RequestUtil.validateHeaders(token));
        return headers;
    }
}
