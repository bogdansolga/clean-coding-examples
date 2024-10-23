package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;

/**
 * Exception in case of failure during event publishing
 */
public class EventPublishingException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 1050052447996229801L;

    /**
     * Instantiates a new EventPublishingException runtime exception.
     *
     * @param message
     *            the message
     */
    public EventPublishingException(String message) {
        super(message);
    }

    /**
     * Instantiates a new EventPublishingException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public EventPublishingException(String message, Throwable reason) {
        super(message, reason);
    }
}
