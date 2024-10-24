package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;

/**
 * Class for {@link PatientAdminService} exception in case of {@link Encounter} has modified subject, defined as
 * unchecked exception.
 */
public class EncounterSubjectModifiedException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 593483453388343996L;

    /**
     * Instantiates a new SubjectModifiedException runtime exception.
     *
     * @param message
     *            the message
     */
    public EncounterSubjectModifiedException(String message) {
        super(message);
    }

    /**
     * Instantiates a new SubjectModifiedException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public EncounterSubjectModifiedException(String message, Throwable reason) {
        super(message, reason);
    }
}