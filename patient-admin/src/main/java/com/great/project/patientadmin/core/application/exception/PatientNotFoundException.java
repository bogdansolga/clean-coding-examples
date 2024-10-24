package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;

/**
 * Class for {@link PatientAdminService} exception in case of {@link Patient} entity was not found , defined as
 * unchecked exception.
 */
public class PatientNotFoundException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -3355306051691862032L;

    /**
     * Instantiates a new PatientNotFoundException runtime exception.
     *
     * @param message
     *            the message
     */
    public PatientNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new PatientNotFoundException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public PatientNotFoundException(String message, Throwable reason) {
        super(message, reason);
    }

}
