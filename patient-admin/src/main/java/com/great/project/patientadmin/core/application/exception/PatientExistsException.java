package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.eventhandler.PatientCreatedEventHandler;

/**
 * Class for {@link PatientCreatedEventHandler} exception in case the patient with the given identifier already exists
 * in the database, defined as unchecked exception.
 * 
 * @author Gabriela Maciac
 */
public class PatientExistsException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -1567006527442094889L;

    /**
     * Instantiates a new PatientExistsException runtime exception.
     *
     * @param message
     *            the message
     */
    public PatientExistsException(String message) {
        super(message);
    }

    /**
     * Instantiates a new PatientExistsException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public PatientExistsException(String message, Throwable reason) {
        super(message, reason);
    }
}