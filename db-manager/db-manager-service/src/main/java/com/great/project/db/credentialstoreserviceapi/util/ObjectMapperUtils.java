package com.great.project.db.credentialstoreserviceapi.util;

import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Object Mapper utility class
 */
public class ObjectMapperUtils {

    private ObjectMapperUtils() {
    }

    /**
     * Serialize specified value as a String
     * 
     * @param value
     *            - the value to be serialized
     * @return {@link String}
     * @throws CredentialStoreException
     */
    public static String serializeToString(Object value) throws CredentialStoreException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new CredentialStoreException(e.getMessage(), e);
        }
    }

    /**
     * Deserialize JSON String to specified class type
     * 
     * @param <T>
     *            - type
     * @param jsonString
     *            - JSON String
     * @param valueType
     *            - class type
     * @return <T> T
     * @throws CredentialStoreException
     */
    public static <T> T deserializeJsonString(String jsonString, Class<T> valueType) throws CredentialStoreException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            throw new CredentialStoreException(e.getMessage(), e);
        }
    }
}
