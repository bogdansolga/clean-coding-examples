package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;

/**
 * Class for {@link PatientAdminService} exception in case of {@link Patient} contains encounters
 */
public class PatientContainsEncountersException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new PatientContainsEncountersException runtime exception.
     *
     * @param message
     *            the message
     */
    public PatientContainsEncountersException(String message) {
        super(message);

    }

    /**
     * Instantiates a new PatientContainsEncountersException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public PatientContainsEncountersException(String message, Throwable reason) {
        super(message, reason);
    }

}
