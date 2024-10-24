package com.great.project.db.db.manager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Object Mapper utility class
 */
public class ObjectMapperUtils {

    private ObjectMapperUtils() {
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Serialize specified value as a String
     * 
     * @param value
     *            - the value to be serialized
     * @return {@link String}
     */
    public static String serializeToString(Object value) {
        try {
            return createMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new DbManagerException(e.getMessage(), e);
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
     */
    public static <T> T deserializeJsonString(String jsonString, Class<T> valueType) {
        try {
            return createMapper().readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            throw new DbManagerException(e.getMessage(), e);
        }
    }
}
