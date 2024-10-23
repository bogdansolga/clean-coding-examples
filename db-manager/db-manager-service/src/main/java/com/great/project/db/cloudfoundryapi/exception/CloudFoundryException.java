package com.great.project.db.cloudfoundryapi.exception;

import com.cerner.augero.core.exception.AugeroException;

/**
 * Instantiate CloudFoundryException by message and/or cause.
 * 
 * @author Gabriela Maciac
 */
public class CloudFoundryException extends AugeroException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -3132641216668567159L;

    /**
     * Instantiates a new CloudFoundryException.
     *
     * @param message
     *            - the exception message
     */
    public CloudFoundryException(String message) {
        super(message);
    }

    /**
     * Instantiates a new CloudFoundryException.
     *
     * @param message
     *            the exception message
     * @param cause
     *            the cause of the exception
     */
    public CloudFoundryException(String message, Throwable cause) {
        super(message, cause);
    }
}
