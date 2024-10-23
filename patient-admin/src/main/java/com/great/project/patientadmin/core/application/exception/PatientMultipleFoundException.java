package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;

/**
 * Class for {@link PatientAdminService} exception in case of multiple {@link Patient} were found, defined as unchecked
 * exception.
 * 
 * @author Gorasteanu Mihai
 */

public class PatientMultipleFoundException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -5232772494868079420L;

    /**
     * Instantiates a new PatientMultipleFoundException runtime exception.
     *
     * @param message
     *            the message
     */
    public PatientMultipleFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new PatientMultipleFoundException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public PatientMultipleFoundException(String message, Throwable reason) {
        super(message, reason);
    }
}
