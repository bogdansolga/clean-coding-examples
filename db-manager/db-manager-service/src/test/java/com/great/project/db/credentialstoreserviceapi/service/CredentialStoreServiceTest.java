package com.great.project.db.credentialstoreserviceapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.util.ObjectMapperUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.util.CryptoUtil;
import com.great.project.db.credentialstoreserviceapi.util.RequestUtil;
import com.great.project.db.test.util.CredentialStoreEntriesDataFactory;

/**
 * Test {@link CredentialStoreService}
 * 
 * @author Gabriela Maciac
 */
public class CredentialStoreServiceTest {

    private static final String MOCK_JSON_WITH_LAST = "{\"last\": \"false\"}";
    private static final String MOCK_JSON_RESPONSE_WITH_NAME = "{\"name\": \"credential-store-entry\"}";
    private static final String DROWSSAP_PATH = "password";
    private static final String KEY_PATH = "key";
    private static final String PARAM_SIZE = "?size=";
    private static final String NO_NAME = null;
    private static final String NO_BODY = null;
    private static final String NAMESPACE = "namespace";
    private static final String NAME = "name";

    private CredentialStoreEntriesDto credentialStoreEntriesDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntriesDto();
    private CredentialStoreEntryDto credentialStoreEntryDto = CredentialStoreEntriesDataFactory
            .createMinimumCredentialStoreEntryDto();
    private NewCredentialStoreEntryDto newCredentialStoreEntryDto = CredentialStoreEntriesDataFactory
            .createMinimumNewCredentialStoreDto();

    private RequestUtil requestUtil;
    private CryptoUtil cryptoUtil;
    private CredentialStoreService credentialStoreService;
    private ResponseEntity<String> response;

    @BeforeEach
    public void setUp() {
        requestUtil = mock(RequestUtil.class);
        cryptoUtil = mock(CryptoUtil.class);
        credentialStoreService = new CredentialStoreService(requestUtil, cryptoUtil);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic Y2Y6");
        response = new ResponseEntity<>("some response body", headers, HttpStatus.OK);
    }

    /**
     * Purpose: > Given a valid namespace, when calling getPasswords, the correct CredentialStoreEntriesDto is returned.
     * <br>
     * Prerequisites: > mock headers, response and namespace.<br>
     * Design Steps: > 1. Mock the HttpHeaders and ResponseEntity.<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace, when calling getPasswords, the correct CredentialStoreEntriesDto is returned.")
    @Test
    public void getPasswordsGivingValidNamespace() throws CredentialStoreException {
        // Arrange
        when(requestUtil.execute(HttpMethod.GET, DROWSSAP_PATH + "s", NAMESPACE, NO_NAME, NO_BODY))
                .thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn(MOCK_JSON_WITH_LAST);

        // Act
        CredentialStoreEntriesDto actual = credentialStoreService.getPasswords(NAMESPACE);

        // Assert
        assertEquals(credentialStoreEntriesDto.isLast(), actual.isLast());
    }

    /**
     * Purpose: > Given a valid namespace, when calling getPassword, the correct CredentialStoreEntryDto is returned.
     * <br>
     * Prerequisites: > mock headers, response, namespace and name.<br>
     * Design Steps: > 1. Mock the HttpHeaders and ResponseEntity.<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace and the name.
     * <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace and name, when calling getPassword, the correct CredentialStoreEntryDto is returned.")
    @Test
    public void getPasswordGivingValidNamespaceAndName() throws CredentialStoreException {
        // Arrange
        when(requestUtil.execute(HttpMethod.GET, DROWSSAP_PATH, NAMESPACE, NAME, NO_BODY)).thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn(MOCK_JSON_RESPONSE_WITH_NAME);

        // Act
        CredentialStoreEntryDto actual = credentialStoreService.getPassword(NAMESPACE, NAME);

        // Assert
        assertEquals(credentialStoreEntryDto.getName(), actual.getName());
    }

    /**
     * Purpose: > Given a valid namespace and NewCredentialStoreEntryDto, when calling createUpdatePassword, the correct
     * CredentialStoreEntryDto is returned. <br>
     * Prerequisites: > mock headers, response, namespace and NewCredentialStoreEntryDto.<br>
     * Design Steps: > 1. Mock the HttpHeaders, ResponseEntity and NewCredentialStoreEntryDto.<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace and
     * NewCredentialStoreEntryDto. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace and NewCredentialStoreEntryDto, when calling createUpdatePassword, the correct "
            + "CredentialStoreEntryDto is returned.")
    @Test
    public void getCredentialStoreEntryDtoWhenCreatingUpdatingPassword() throws CredentialStoreException {
        // Arrange
        String entryString = ObjectMapperUtils.serializeToString(newCredentialStoreEntryDto);
        String encryptedRequestBody = "some-encrypted-text";

        when(cryptoUtil.encrypt(entryString)).thenReturn(encryptedRequestBody);
        when(requestUtil.execute(HttpMethod.POST, DROWSSAP_PATH, NAMESPACE, NO_NAME, encryptedRequestBody))
                .thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn(MOCK_JSON_RESPONSE_WITH_NAME);

        // Act
        CredentialStoreEntryDto actual = credentialStoreService.createUpdatePassword(NAMESPACE,
                newCredentialStoreEntryDto);

        // Assert
        assertEquals(credentialStoreEntryDto.getName(), actual.getName());
    }

    /**
     * Purpose: > Given a valid namespace and name, when calling CredentialStoreService.deletePassword, verify that
     * RequestUtil.execute was called correctly. <br>
     * Prerequisites: > mock namespace and name.<br>
     * Design Steps: > 1. Call CredentialStoreService.deletePassword using the mock values.<br>
     * 2. Verify that RequestUtil.execute with the correct parameters was called exactly one time.<br>
     * Expected Results: > The method call is correct.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace and name, when calling CredentialStoreService.deletePassword, verify that "
            + "RequestUtil.execute was called correctly.")
    @Test
    public void verifyDeletePasswordMethodIsCalledCorrectly() throws CredentialStoreException {
        // Act
        credentialStoreService.deletePassword(NAMESPACE, NAME);

        // Verify
        Mockito.verify(requestUtil).execute(HttpMethod.DELETE, DROWSSAP_PATH, NAMESPACE, NAME, NO_BODY);
    }

    /**
     * Purpose: > Given a valid namespace, when calling getKeys, the correct CredentialStoreEntriesDto is returned. <br>
     * Prerequisites: > mock headers, response and namespace.<br>
     * Design Steps: > 1. Mock the HttpHeaders and ResponseEntity.<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace, when calling getKeys, the correct CredentialStoreEntriesDto is returned.")
    @Test
    public void getKeysGivingAValidNamespace() throws CredentialStoreException {
        // Arrange
        when(requestUtil.execute(HttpMethod.GET, KEY_PATH + "s" + PARAM_SIZE + 99999, NAMESPACE, NO_NAME, NO_BODY))
                .thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn(MOCK_JSON_WITH_LAST);

        // Act
        CredentialStoreEntriesDto actual = credentialStoreService.getKeys(NAMESPACE);

        // Assert
        assertEquals(credentialStoreEntriesDto.isLast(), actual.isLast());
    }

    /**
     * Purpose: > Given a valid namespace and name, when calling getKey, the correct CredentialStoreEntryDto is
     * returned. <br>
     * Prerequisites: > mock headers, response, namespace and name.<br>
     * Design Steps: > 1. Mock the HttpHeaders and ResponseEntity.<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace and the name.
     * <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace, when calling getKeys, the correct CredentialStoreEntriesDto is returned.")
    @Test
    public void getKeyGivingAValidNamespaceAndName() throws CredentialStoreException {
        // Arrange
        when(requestUtil.execute(HttpMethod.GET, KEY_PATH, NAMESPACE, NAME, NO_BODY)).thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn("{\"value\": \"value\"}");

        // Act
        CredentialStoreEntryDto actual = credentialStoreService.getKey(NAMESPACE, NAME);

        // Assert
        byte[] decodedValue = Base64.decodeBase64(credentialStoreEntryDto.getValue());
        String expectedDecodedString = new String(decodedValue);

        assertEquals(expectedDecodedString, actual.getValue());
    }

    /**
     * Purpose: > Given a valid namespace and NewCredentialStoreEntryDto, when calling createUpdateKey, the correct
     * CredentialStoreEntryDto is returned. <br>
     * Prerequisites: > mock headers, response, namespace and NewCredentialStoreEntryDto.<br>
     * Design Steps: > 1. Mock the HttpHeaders, ResponseEntity and NewCredentialStoreEntryDto.<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace and
     * NewCredentialStoreEntryDto. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     */
    @DisplayName("Given a valid namespace and NewCredentialStoreEntryDto, when calling createUpdatePassword, the correct "
            + "CredentialStoreEntryDto is returned.")
    @Test
    public void getCredentialStoreEntryDtoWhenCreatingUpdatingKey() throws CredentialStoreException {
        // Arrange
        when(requestUtil.execute(HttpMethod.POST, KEY_PATH, NAMESPACE, NO_NAME, null)).thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn(MOCK_JSON_RESPONSE_WITH_NAME);

        // Act
        CredentialStoreEntryDto actual = credentialStoreService.createUpdateKey(NAMESPACE, newCredentialStoreEntryDto);

        // Assert
        assertEquals(credentialStoreEntryDto.getName(), actual.getName());
    }

    /**
     * Purpose: > Given a valid namespace and NewCredentialStoreEntryDto, when calling createUpdateKey, the correct
     * CredentialStoreEntryDto is returned. <br>
     * Prerequisites: > mock headers, response, namespace and NewCredentialStoreEntryDto.<br>
     * Design Steps: > 1. Set null value on newCredentialStoreEntryDto.value<br>
     * 2. Stub RequestUtil and CryptoUtil to return mock values for the response.<br>
     * 3. Call CredentialStoreService (class under test) to retrieve the actual DTO given the namespace and
     * NewCredentialStoreEntryDto with the null value. <br>
     * Expected Results: > The expected DTO is equal to the actual one.<br>
     */
    @DisplayName("Given a valid namespace and NewCredentialStoreEntryDto, when calling createUpdatePassword, the correct "
            + "CredentialStoreEntryDto is returned.")
    @Test
    public void getCredentialStoreEntryDtoWhenCreatingUpdatingKeyWithNullValue() throws CredentialStoreException {
        // Arrange
        newCredentialStoreEntryDto.setValue(null);
        when(requestUtil.execute(HttpMethod.POST, KEY_PATH, NAMESPACE, NO_NAME, null)).thenReturn(response);
        when(cryptoUtil.decrypt(response.getBody())).thenReturn(MOCK_JSON_RESPONSE_WITH_NAME);

        // Act
        CredentialStoreEntryDto actual = credentialStoreService.createUpdateKey(NAMESPACE, newCredentialStoreEntryDto);

        // Assert
        assertEquals(credentialStoreEntryDto.getName(), actual.getName());
    }

    /**
     * Purpose: > Given a valid namespace and name, when calling CredentialStoreService.deleteKey, verify that
     * RequestUtil.execute was called correctly. <br>
     * Prerequisites: > mock namespace and name.<br>
     * Design Steps: > 1. Call CredentialStoreService.deleteKey using the mock values.<br>
     * 2. Verify that RequestUtil.execute with the correct parameters was called exactly one time.<br>
     * Expected Results: > The method call is correct.<br>
     * 
     * @throws CredentialStoreException
     */
    @DisplayName("Given a valid namespace and name, when calling CredentialStoreService.deletePassword, verify that "
            + "RequestUtil.execute was called correctly.")
    @Test
    public void verifyDeleteKeyMethodIsCalledCorrectly() throws CredentialStoreException {
        // Act
        credentialStoreService.deleteKey(NAMESPACE, NAME);

        // Verify
        Mockito.verify(requestUtil).execute(HttpMethod.DELETE, KEY_PATH, NAMESPACE, NAME, NO_BODY);
    }
}
