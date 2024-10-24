package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;

/**
 * Class for {@link PatientAdminService} exception in case of multiple {@link Encounter} ids were found , defined as
 * unchecked exception.
 */
public class EncounterMultipleFoundException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -9014676038610742123L;

    /**
     * Instantiates a new EncounterMultipleFoundException runtime exception.
     *
     * @param message
     *            the message
     */
    public EncounterMultipleFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new EncounterMultipleFoundException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public EncounterMultipleFoundException(String message, Throwable reason) {
        super(message, reason);
    }
}
