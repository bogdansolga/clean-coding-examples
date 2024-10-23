package com.great.project.db.db.manager.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test {@link ObjectMapperUtils}
 * 
 * @author Gabriela Maciac
 */
public class ObjectMapperUtilsTest {

    /**
     * Purpose: > Throw DbManagerException when calling ObjectMapperUtils.serializeToString method. <br>
     * Prerequisites: > Empty object. <br>
     * Design Steps: > Call ObjectMapperUtils.serializeToString giving it an empty object and expect an exception to be
     * thrown.<br>
     * Expected Results: > A DbManagerException exception is thrown. <br>
     */
    @DisplayName("Throw DbManagerException when calling ObjectMapperUtils.serializeToString method.")
    @Test
    public void throwExceptionWhenSerializingToString() {
        // Arrange
        Object mockObject = new Object();

        // Act and Assert
        assertThrows(DbManagerException.class, () -> ObjectMapperUtils.serializeToString(mockObject));
    }

    /**
     * Purpose: > Throw DbManagerException when calling ObjectMapperUtils.deserializeJsonString method. <br>
     * Prerequisites: > Invalid string. <br>
     * Design Steps: > Call ObjectMapperUtils.deserializeJsonString giving an invalid string and expect an exception to
     * be thrown.<br>
     * Expected Results: > A DbManagerException exception is thrown. <br>
     */
    @DisplayName("Throw DbManagerException when calling ObjectMapperUtils.deserializeJsonString method.")
    @Test
    public void throwExceptionWhenDeserializingJsonString() {
        // Arrange
        String anyString = "some string";

        // Act and Assert
        assertThrows(DbManagerException.class, () -> ObjectMapperUtils.deserializeJsonString(anyString, Object.class));
    }
}
