package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;

/**
 * Class for {@link PatientAdminService} exception in case of the link type used is not proper for the action
 * implemented (e.g. patient update).
 */
public class LinkTypeException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 1878605866877243368L;

    /**
     * Instantiates a new LinkTypeException runtime exception.
     *
     * @param message
     *            the message
     */
    public LinkTypeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new LinkTypeException runtime exception.
     * 
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public LinkTypeException(String message, Throwable reason) {
        super(message, reason);
    }

}
