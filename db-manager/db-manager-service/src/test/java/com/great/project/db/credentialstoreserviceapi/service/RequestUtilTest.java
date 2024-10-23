package com.great.project.db.credentialstoreserviceapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.great.project.db.credentialstoreserviceapi.util.RequestUtil;

/**
 * Test {@link RequestUtil}
 * 
 * @author Andrei Maneasa
 */
public class RequestUtilTest {

    private static final String VALID_TEXT_HEADER = "=_.+-Av23";
    private static final String INVALID_TEXT_HEADER = "=_.+-Av23#%!~";

    /**
     * Purpose: > Given valid text header, when calling validateHeaders, then validated text header is returned. <br>
     * Prerequisites: N/A.<br>
     * Design Steps: > Call RequestUtil validateHeaders <br>
     * Expected Results: > Validated text header is returned.<br>
     */
    @DisplayName("Given valid text header, when calling validateHeaders, then validated text header is returned.")
    @Test
    public void validTextHeader() {
        // Act
        String header = RequestUtil.validateHeaders(VALID_TEXT_HEADER);

        // Assert
        assertEquals(VALID_TEXT_HEADER, header);
    }

    /**
     * Purpose: > Given invalid text header, when calling validateHeaders, then exception is throw. <br>
     * Prerequisites: N/A.<br>
     * Design Steps: > Call RequestUtil validateHeaders <br>
     * Expected Results: > then exception with correct message is thrown.<br>
     */
    @DisplayName("Given invalid text header, when calling validateHeaders, then exception is thrown.")
    @Test
    public void invalidTextHeader() {
        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> RequestUtil.validateHeaders(INVALID_TEXT_HEADER));

        // Assert
        assertEquals(RequestUtil.HEADERS_VALIDATION_FAILED_MESSAGE, exception.getMessage());
    }

}
