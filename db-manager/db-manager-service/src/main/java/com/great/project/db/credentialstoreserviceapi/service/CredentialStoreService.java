package com.great.project.db.credentialstoreserviceapi.service;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.great.project.db.credentialstoreserviceapi.util.ObjectMapperUtils;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.great.project.db.credentialstoreserviceapi.util.CryptoUtil;
import com.great.project.db.credentialstoreserviceapi.util.RequestUtil;

/**
 * Service to interact with the Credential Store service from SAP
 */
@Service
public class CredentialStoreService {

    private static final String DROWSSAP_PATH = "password";
    private static final String KEY_PATH = "key";
    private static final String PARAM_SIZE = "size=99999"; // SAP CF API maximum entries to be retrieved
    public static final String NO_NAME = null;
    public static final String NO_BODY = null;

    private final RequestUtil requestUtil;
    private final CryptoUtil cryptoUtil;

    public CredentialStoreService(RequestUtil requestUtil, CryptoUtil cryptoUtil) {
        this.requestUtil = requestUtil;
        this.cryptoUtil = cryptoUtil;
    }

    /**
     * Get all the passwords for the namespace
     * 
     * @param namespace
     * @return Credential Store entries
     * @throws CredentialStoreException
     */
    public CredentialStoreEntriesDto getPasswords(String namespace) throws CredentialStoreException {
        ResponseEntity<String> response = requestUtil.execute(HttpMethod.GET, DROWSSAP_PATH + "s", namespace, NO_NAME,
                NO_BODY);
        String decryptedCredentialsString = cryptoUtil.decrypt(response.getBody());
        return ObjectMapperUtils.deserializeJsonString(decryptedCredentialsString, CredentialStoreEntriesDto.class);
    }

    /**
     * Get a single password for a given namespace and name
     * 
     * @param namespace
     * @param name
     * @return the password
     * @throws CredentialStoreException
     */
    public CredentialStoreEntryDto getPassword(String namespace, String name) throws CredentialStoreException {
        ResponseEntity<String> response = requestUtil.execute(HttpMethod.GET, DROWSSAP_PATH, namespace, name, NO_BODY);
        String decryptedCredentialString = cryptoUtil.decrypt(response.getBody());
        return ObjectMapperUtils.deserializeJsonString(decryptedCredentialString, CredentialStoreEntryDto.class);
    }

    /**
     * Creates or updates a password
     * 
     * @param namespace
     * @param newEntry
     * @return
     * @throws CredentialStoreException
     */
    public CredentialStoreEntryDto createUpdatePassword(String namespace, NewCredentialStoreEntryDto newEntry)
            throws CredentialStoreException {
        String entryString = ObjectMapperUtils.serializeToString(newEntry);
        String encryptedRequestBody = cryptoUtil.encrypt(entryString);
        ResponseEntity<String> response = requestUtil.execute(HttpMethod.POST, DROWSSAP_PATH, namespace, NO_NAME,
                encryptedRequestBody);
        String decryptedCredentialString = cryptoUtil.decrypt(response.getBody());
        return ObjectMapperUtils.deserializeJsonString(decryptedCredentialString, CredentialStoreEntryDto.class);
    }

    /**
     * Deletes a password
     * 
     * @param namespace
     * @param name
     * @throws CredentialStoreException
     */
    public void deletePassword(String namespace, String name) throws CredentialStoreException {
        requestUtil.execute(HttpMethod.DELETE, DROWSSAP_PATH, namespace, name, NO_BODY);
    }

    /**
     * Get a list of the keys from the Credential Store given namespace
     * 
     * @param namespace
     * @return dto with the key list
     * @throws CredentialStoreException
     */
    public CredentialStoreEntriesDto getKeys(String namespace) throws CredentialStoreException {
        ResponseEntity<String> response = requestUtil.execute(HttpMethod.GET, KEY_PATH + "s" + "?" + PARAM_SIZE,
                namespace, NO_NAME, NO_BODY);
        String decryptedCredentialsString = cryptoUtil.decrypt(response.getBody());
        return ObjectMapperUtils.deserializeJsonString(decryptedCredentialsString, CredentialStoreEntriesDto.class);
    }

    /**
     * Get the requested key from the Credential Store
     * 
     * @param namespace
     * @param name
     * @return the key
     * @throws CredentialStoreException
     */
    public CredentialStoreEntryDto getKey(String namespace, String name) throws CredentialStoreException {
        ResponseEntity<String> response = requestUtil.execute(HttpMethod.GET, KEY_PATH, namespace, name, NO_BODY);
        if (response.getStatusCode() == HttpStatus.I_AM_A_TEAPOT || response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
        }
        String decryptedCredentialString = cryptoUtil.decrypt(response.getBody());
        CredentialStoreEntryDto dto = ObjectMapperUtils.deserializeJsonString(decryptedCredentialString,
                CredentialStoreEntryDto.class);
        decodeValue(dto);
        return dto;
    }

    /**
     * Creates or updates a key from the Credential Store
     * 
     * @param namespace
     * @param newEntry
     * @return the key
     * @throws CredentialStoreException
     */
    public CredentialStoreEntryDto createUpdateKey(String namespace, NewCredentialStoreEntryDto newEntry)
            throws CredentialStoreException {
        encodeValue(newEntry);
        String entryString = ObjectMapperUtils.serializeToString(newEntry);
        String encryptedRequestBody = cryptoUtil.encrypt(entryString);
        ResponseEntity<String> response = requestUtil.execute(HttpMethod.POST, KEY_PATH, namespace, NO_NAME,
                encryptedRequestBody);
        String decryptedCredentialString = cryptoUtil.decrypt(response.getBody());
        return ObjectMapperUtils.deserializeJsonString(decryptedCredentialString, CredentialStoreEntryDto.class);
    }

    /**
     * Deletes a key from the Credential Store
     * 
     * @param namespace
     * @param name
     * @throws CredentialStoreException
     */
    public void deleteKey(String namespace, String name) throws CredentialStoreException {
        requestUtil.execute(HttpMethod.DELETE, KEY_PATH, namespace, name, NO_BODY);
    }

    /**
     * Encodes the given value to Base64
     * 
     * @param newEntry
     */
    private void encodeValue(NewCredentialStoreEntryDto newEntry) {
        if (newEntry.getValue() == null) {
            newEntry.setValue("{}");
        }
        newEntry.setValue(Base64.encodeBase64String(newEntry.getValue().getBytes()));
    }

    /**
     * Decodes the given value from Base64 to string
     * 
     * @param dto
     */
    private void decodeValue(CredentialStoreEntryDto dto) {
        dto.setValue(new String(Base64.decodeBase64(dto.getValue())));
    }
}
