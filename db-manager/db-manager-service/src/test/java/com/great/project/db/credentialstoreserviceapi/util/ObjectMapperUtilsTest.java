package com.great.project.db.credentialstoreserviceapi.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.great.project.db.credentialstoreserviceapi.exception.CredentialStoreException;

/**
 * Test {@link ObjectMapperUtils}
 * 
 * @author Gabriela Maciac
 */
public class ObjectMapperUtilsTest {

    /**
     * Purpose: > Throw CredentialStoreException when calling ObjectMapperUtils.serializeToString method. <br>
     * Prerequisites: > Empty object. <br>
     * Design Steps: > Call ObjectMapperUtils.serializeToString giving it an empty object and expect an exception to be
     * thrown.<br>
     * Expected Results: > A CredentialStoreException exception is thrown. <br>
     */
    @DisplayName("Throw DbManagerException when calling ObjectMapperUtils.serializeToString method.")
    @Test
    public void throwExceptionWhenSerializingToString() {
        // Arrange
        Object mockObject = new Object();

        // Act and Assert
        assertThrows(CredentialStoreException.class, () -> ObjectMapperUtils.serializeToString(mockObject));
    }

    /**
     * Purpose: > Throw CredentialStoreException when calling ObjectMapperUtils.deserializeJsonString method. <br>
     * Prerequisites: > Invalid string. <br>
     * Design Steps: > Call ObjectMapperUtils.deserializeJsonString giving an invalid string and expect an exception to
     * be thrown.<br>
     * Expected Results: > A CredentialStoreException exception is thrown. <br>
     */
    @DisplayName("Throw DbManagerException when calling ObjectMapperUtils.deserializeJsonString method.")
    @Test
    public void throwExceptionWhenDeserializingJsonString() {
        // Arrange
        String anyString = "some string";

        // Act and Assert
        assertThrows(CredentialStoreException.class,
                () -> ObjectMapperUtils.deserializeJsonString(anyString, Object.class));
    }
}
