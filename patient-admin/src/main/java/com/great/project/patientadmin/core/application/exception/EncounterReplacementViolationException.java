package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;

/**
 * Exception class in case of rule violations for Encounter Replacement
 */
public class EncounterReplacementViolationException extends AugeroRuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2507211311867318919L;

    /**
     * Instantiates a new EncounterReplacementViolationException runtime exception.
     *
     * @param message
     *            the message
     */
    public EncounterReplacementViolationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new EncounterReplacementViolationException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public EncounterReplacementViolationException(String message, Throwable reason) {
        super(message, reason);
    }
}
