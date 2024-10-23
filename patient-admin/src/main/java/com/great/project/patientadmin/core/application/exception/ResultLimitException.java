package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;

/**
 * Class for exception in case of multiple results returned by secondary adapter , defined as
 * unchecked exception.
 *
 * @author Catalin Matache
 */
public class ResultLimitException extends AugeroRuntimeException {

    /**
     * Instantiates a new ResultLimitException runtime exception.
     *
     * @param message
     *            the message
     */
    public ResultLimitException(String message) {
        super(message);
    }

    /**
     * Instantiates a new ResultLimitException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public ResultLimitException(String message, Throwable reason) {
        super(message, reason);
    }
}
