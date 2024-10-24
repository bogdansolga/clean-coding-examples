package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;

/**
 * Class for {@link PatientAdminService} exception in case of {@link Patient} entity was not created , defined as
 * unchecked exception.
 */
public class PatientCreateFailException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -7634675331736902518L;

    /**
     * Instantiates a new PatientCreateFailException runtime exception.
     *
     * @param message
     *            the message
     */
    public PatientCreateFailException(String message) {
        super(message);
    }

    /**
     * Instantiates a new PatientCreateFailException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public PatientCreateFailException(String message, Throwable reason) {
        super(message, reason);
    }

}
