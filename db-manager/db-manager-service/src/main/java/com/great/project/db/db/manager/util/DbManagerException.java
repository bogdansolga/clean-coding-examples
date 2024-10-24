package com.great.project.db.db.manager.util;

import com.great.project.core.exception.AugeroRuntimeException;

/**
 * Instantiate a new DbManagerException by message and/or reason.
 */
public class DbManagerException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 7083791737446656674L;

    /**
     * Instantiates a new DbManagerException runtime exception.
     *
     * @param message
     *            the message
     */
    public DbManagerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new DbManagerException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public DbManagerException(String message, Throwable reason) {
        super(message, reason);
    }

}
