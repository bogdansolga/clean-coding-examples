package com.great.project.db.cloudfoundryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.great.project.db.cloudfoundryapi.config.CloudFoundryConfig;
import com.great.project.db.cloudfoundryapi.dto.NewServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.NewServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceInstanceDto;
import com.great.project.db.cloudfoundryapi.dto.ServiceKeyDto;
import com.great.project.db.cloudfoundryapi.dto.UserPwdDto;
import com.great.project.db.cloudfoundryapi.exception.CloudFoundryException;
import com.great.project.db.db.manager.dto.ServiceInstanceCreationStatusDto;

/**
 * Test {@link CloudFoundryService}
 * 
 * @author Andrei Maneasa
 */
public class CloudFoundryServiceTest {

    private static final String EXPECTED_OPERATION_STATE = "test-state";
    private static final String EXPECTED_OPERATION_TYPE = "test-type";
    private static final String EXPECTED_ACCESS_TOKEN = "test-accessToken";
    private static final String EXPECTED_GUID = "test-guid";
    private static final String TEST_SERVICE_INSTANCE_GUID = "test-serviceInstanceGuid";
    private static final String TEST_DB_SERVICE_GUI = "test-db_service_gui";
    private static final String TEST_SERVICE_KEY_GUID = "test-serviceKeyGuid";
    private static final String TEST_TOKEN = "test-token";
    private static final String SLASH = "/";
    private static final String API_URL = "http://localhost:8080";
    private static final String OAUTH_TOKEN_PATH = SLASH + "oauth/token";
    private static final String SERVICE_KEYS_PATH = SLASH + "v2/service_keys";
    private static final String SERVICE_INSTANCES_PATH = SLASH + "v2/service_instances";

    private static final String MOCK_GET_TOKEN_JSON = "{\"access_token\": \"test-accessToken\" }";
    private static final String MOCK_SERVICE_INSTANCE_CREATION_STATUS_JSON = "{\"entity\": " + "{\"last_operation\": "
            + "{\"type\": \"test-type\", \"state\": \"test-state\"}}}";
    private static final String MOCK_CREATE_SERVICE_KEY_JSON = "{\"metadata\": { \"guid\": \"test-guid\" }, \"entity\": "
            + "{ \"credentials\": \"test-credentials\"}}";
    private static final String MOCK_CREATE_SERVICE_INSTANCE_JSON = "{ \"metadata\": {\"guid\": \"test-guid\"}}";

    private UserPwdDto userPwdDto = createMinimumUserPwdDto();
    private NewServiceInstanceDto serviceInstanceDto = createMinimumNewServiceInstanceDto();
    private HttpHeaders headers = CloudFoundryService.createHeaders(TEST_TOKEN);
    private NewServiceKeyDto newServiceKeyDto = createMinimumNewServiceKeyDto();

    private RestTemplateBuilder restTemplateBuilder;
    private CloudFoundryConfig cloudFoundryConfig;
    private RestTemplate restTemplate;
    private CloudFoundryService cloudFoundryService;

    @BeforeEach
    public void setUp() {
        restTemplateBuilder = mock(RestTemplateBuilder.class);
        cloudFoundryConfig = mock(CloudFoundryConfig.class);
        restTemplate = mock(RestTemplate.class);
        cloudFoundryService = new CloudFoundryService(restTemplateBuilder, cloudFoundryConfig);
    }

    /**
     * Purpose: > Given a valid UserPwdDto credentials, when calling getTokenFor, the valid token String is returned.
     * <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity and ResponseEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to retrieve the actual token. <br>
     * Expected Results: > The expected token is equal to the actual one.<br>
     * 
     * @throws CloudFoundryException
     */
    @DisplayName("Given valid UserPwdDto credentials, when calling getTokenFor, then valid token String is returned.")
    @Test
    public void givenValidUserPwdDtoCredentialsWhenCallTokenForThenValidTokenIsReturn() throws CloudFoundryException {
        // Arrange
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(createRequestBody(), createBasicHeaders());
        ResponseEntity<String> responseEntity = new ResponseEntity<>(MOCK_GET_TOKEN_JSON, createBasicHeaders(),
                HttpStatus.OK);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getLoginUrl()).thenReturn(API_URL);
        when(restTemplate.postForEntity(cloudFoundryConfig.getLoginUrl() + OAUTH_TOKEN_PATH, request, String.class))
                .thenReturn(responseEntity);
        // Act
        String token = cloudFoundryService.getTokenFor(userPwdDto);
        // Assert
        assertEquals(EXPECTED_ACCESS_TOKEN, token);
    }

    /**
     * Purpose: > Given valid token, NewServiceInstanceDto and set parameter acceptsIncomplete to true, when calling
     * createServiceInstance, then ServiceInstanceDto is returned. <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity and ResponseEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to retrieve the actual ServiceInstanceDto. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CloudFoundryException
     */
    @DisplayName("Given valid token, NewServiceInstanceDto and set parameter acceptsIncomplete to true, when calling createServiceInstance, then ServiceInstanceDto is returned.")
    @Test
    public void getServiceInstanceDtoWhenCreateServiceInstanceWithValidTokenAndNewServiceInstanceDtoAndAcceptsIncompleteTrue()
            throws CloudFoundryException {
        // Arrange
        HttpEntity<String> request = new HttpEntity<>(CloudFoundryService.mapToString(serviceInstanceDto), headers);
        MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        ResponseEntity<String> responseEntity = new ResponseEntity<>(MOCK_CREATE_SERVICE_INSTANCE_JSON, headers,
                HttpStatus.OK);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getApiUrl()).thenReturn(API_URL);
        when(restTemplate.postForEntity(API_URL + "/v2/service_instances?accepts_incomplete=true", request,
                String.class, uriVariables)).thenReturn(responseEntity);
        // Act
        ServiceInstanceDto createServiceInstance = cloudFoundryService.createServiceInstance(TEST_TOKEN,
                serviceInstanceDto, true);
        // Assert
        assertEquals(EXPECTED_GUID, createServiceInstance.getGuid());
    }

    /**
     * Purpose: > Given valid token, NewServiceInstanceDto and set parameter acceptsIncomplete to false, when calling
     * createServiceInstance, then ServiceInstanceDto is returned. <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity and ResponseEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to retrieve the actual ServiceInstanceDto. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CloudFoundryException
     */
    @DisplayName("Given valid token, NewServiceInstanceDto and set parameter acceptsIncomplete to false, when calling createServiceInstance, then ServiceInstanceDto is returned.")
    @Test
    public void getServiceInstanceDtoWhenCreateServiceInstanceWithValidTokenAndNewServiceInstanceDtoAndAcceptsIncompleteFalse()
            throws CloudFoundryException {
        // Arrange
        HttpEntity<String> request = new HttpEntity<>(CloudFoundryService.mapToString(serviceInstanceDto), headers);
        MultiValueMap<String, String> uriVariables = new LinkedMultiValueMap<>();
        ResponseEntity<String> responseEntity = new ResponseEntity<>(MOCK_CREATE_SERVICE_INSTANCE_JSON, headers,
                HttpStatus.OK);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getApiUrl()).thenReturn(API_URL);
        when(restTemplate.postForEntity(API_URL + SERVICE_INSTANCES_PATH, request, String.class, uriVariables))
                .thenReturn(responseEntity);
        // Act
        ServiceInstanceDto createServiceInstance = cloudFoundryService.createServiceInstance(TEST_TOKEN,
                serviceInstanceDto, false);
        // Assert
        assertEquals(EXPECTED_GUID, createServiceInstance.getGuid());
    }

    /**
     * Purpose: > Given valid token and a dbName, when calling serviceInstanceCreationStatus, then
     * ServiceInstanceCreationStatusDto is returned. <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity and ResponseEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to retrieve the actual ServiceInstanceCreationStatusDto. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CloudFoundryException
     */
    @DisplayName("Given valid token and a dbName, when calling serviceInstanceCreationStatus, then ServiceInstanceCreationStatusDto is returned.")
    @Test
    public void givenValidTokenAndDbNameWhenCallServiceInstanceCreationStatusThenServiceInstanceCreationStatusDtoIsReturn()
            throws CloudFoundryException {
        // Arrange
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(MOCK_SERVICE_INSTANCE_CREATION_STATUS_JSON,
                HttpStatus.OK);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getApiUrl()).thenReturn(API_URL);
        when(restTemplate.exchange(API_URL + SERVICE_INSTANCES_PATH + SLASH + TEST_DB_SERVICE_GUI, HttpMethod.GET,
                request, String.class)).thenReturn(responseEntity);

        // Act
        ServiceInstanceCreationStatusDto serviceInstanceCreationStatusDto = cloudFoundryService
                .retrieveServiceInstanceCreationStatus(TEST_TOKEN, TEST_DB_SERVICE_GUI);
        // Assert
        assertEquals(EXPECTED_OPERATION_TYPE, serviceInstanceCreationStatusDto.getLastOperationType());
        assertEquals(EXPECTED_OPERATION_STATE, serviceInstanceCreationStatusDto.getLastOperationState());
    }

    /**
     * Purpose: > Given valid token and a serviceInstanceGuid, when calling deleteServiceInstance, then serviceInstance
     * is deleted. <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to deleted the serviceInstance. <br>
     * Expected Results: > Verify if deleteServiceInstance method was called once.<br>
     */
    @DisplayName("Given valid token and a serviceInstanceGuid, when calling deleteServiceInstance, then serviceInstance is deleted.")
    @Test
    public void givenValidTokenAndServiceInstanceGuidWhenCallServiceInstanceGuidThenServiceInstanceIsDeleted() {
        // Arrange
        HttpEntity<String> request = new HttpEntity<>(headers);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getApiUrl()).thenReturn(API_URL);
        // Act
        cloudFoundryService.deleteServiceInstance(TEST_TOKEN, TEST_SERVICE_INSTANCE_GUID);
        // Assert
        verify(restTemplate).exchange(API_URL + SERVICE_INSTANCES_PATH + SLASH + TEST_SERVICE_INSTANCE_GUID,
                HttpMethod.DELETE, request, String.class);
    }

    /**
     * Purpose: > Given valid token and a newServiceKeyDto, when calling createServiceKey, then ServiceKeyDto is
     * returned. <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity and ResponseEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to retrieve the actual ServiceKeyDto. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CloudFoundryException
     */
    @DisplayName("Given valid token and a newServiceKeyDto, when calling createServiceKey, then ServiceKeyDto is returned.")
    @Test
    public void givenValidTokenAndNewServiceKeyDtoWhenCallCreateServiceKeyThenServiceKeyDtoIsReturn()
            throws CloudFoundryException {
        // Arrange
        HttpEntity<String> request = new HttpEntity<>(CloudFoundryService.mapToString(newServiceKeyDto), headers);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(MOCK_CREATE_SERVICE_KEY_JSON, HttpStatus.OK);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getApiUrl()).thenReturn(API_URL);
        when(restTemplate.postForEntity(API_URL + SERVICE_KEYS_PATH, request, String.class)).thenReturn(responseEntity);
        // Act
        ServiceKeyDto serviceKeyDto = cloudFoundryService.createServiceKey(TEST_TOKEN, newServiceKeyDto);
        // Assert
        assertEquals(EXPECTED_GUID, serviceKeyDto.getGuid());
        assertEquals("\"test-credentials\"", serviceKeyDto.getCredentials());
    }

    /**
     * Purpose: > Given valid token and a serviceKeyGuid, when calling deleteServiceKey, then serviceKey is deleted.
     * <br>
     * Prerequisites: > Mock RestTemplateBuilder, CloudFoundryConfig and RestTemplate.<br>
     * Design Steps: > 1. Create RequestEntity.<br>
     * 2. Return stub values for the mocked objects.<br>
     * 3. Call CloudFoundryService (class under test) to deleted the serviceKey. <br>
     * Expected Results: > Verify if deleteServiceKey method was called once.<br>
     */
    @DisplayName("Given valid token and a serviceKeyGuid, when calling deleteServiceKey, then serviceKey is deleted.")
    @Test
    public void givenValidTokenAndServiceKeyGuidWhenCallDeleteServiceKeyThenserviceKeyIsDeleted() {
        // Arrange
        HttpEntity<String> request = new HttpEntity<>(headers);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        when(cloudFoundryConfig.getApiUrl()).thenReturn(API_URL);
        // Act
        cloudFoundryService.deleteServiceKey(TEST_TOKEN, TEST_SERVICE_KEY_GUID);
        // Assert
        verify(restTemplate).exchange(API_URL + SERVICE_KEYS_PATH + SLASH + TEST_SERVICE_KEY_GUID, HttpMethod.DELETE,
                request, String.class);
    }

    private UserPwdDto createMinimumUserPwdDto() {
        UserPwdDto dto = new UserPwdDto();
        dto.setUser("test-user");
        dto.setPwd("test-pwd");
        return dto;
    }

    private HttpHeaders createBasicHeaders() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        header.add("Authorization", "Basic Y2Y6");
        return header;
    }

    private MultiValueMap<String, String> createRequestBody() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", userPwdDto.getUser());
        requestBody.add("password", userPwdDto.getPwd());
        return requestBody;
    }

    private NewServiceInstanceDto createMinimumNewServiceInstanceDto() {
        NewServiceInstanceDto dto = new NewServiceInstanceDto();
        dto.setSpaceGuid("test-spaceGuid");
        dto.setName("test-name");
        dto.setServicePlanGuid("test-servicePlanGuid");
        dto.setParameters("test-parameters");
        dto.setTags(Arrays.asList("tag1", "tag2", "tag3"));
        return dto;
    }

    private NewServiceKeyDto createMinimumNewServiceKeyDto() {
        NewServiceKeyDto dto = new NewServiceKeyDto();
        dto.setName("test-name");
        dto.setServiceInstanceGuid(TEST_SERVICE_INSTANCE_GUID);
        return dto;
    }
}
