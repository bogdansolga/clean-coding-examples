package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;

/**
 * Class for {@link PatientAdminService} exception in case of {@link Encounter} entity was not found , defined as
 * unchecked exception.
 * 
 * @author Ardeleanu Dragos
 */
public class EncounterNotFoundException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = 250262959332694873L;

    /**
     * Instantiates a new EncounterNotFoundException runtime exception.
     *
     * @param message
     *            the message
     */
    public EncounterNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new EncounterNotFoundException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public EncounterNotFoundException(String message, Throwable reason) {
        super(message, reason);
    }

}
