package com.great.project.db.credentialstoreserviceapi.exception;

import com.cerner.augero.core.exception.AugeroException;

/**
 * Instantiate a new CredentialStoreException by message and/or reason.
 */
public class CredentialStoreException extends AugeroException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 6242151884649358328L;

    /**
     * Instantiates a new CredentialStoreException checked exception.
     *
     * @param message
     *            the message
     */
    public CredentialStoreException(String message) {
        super(message);
    }

    /**
     * Instantiates a new CredentialStoreException checked exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public CredentialStoreException(String message, Throwable reason) {
        super(message, reason);
    }
}
