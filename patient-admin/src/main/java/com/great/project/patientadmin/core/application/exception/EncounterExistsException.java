package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.eventhandler.EncounterCreatedEventHandler;

/**
 * Class for {@link EncounterCreatedEventHandler} exception in case the encounter with the given identifier already
 * exists in the database, defined as unchecked exception.
 * 
 * @author Ardeleanu Dragos
 */
public class EncounterExistsException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */
    private static final long serialVersionUID = -1567006527442094889L;

    /**
     * Instantiates a new EncounterExistsException runtime exception.
     *
     * @param message
     *            the message
     */
    public EncounterExistsException(String message) {
        super(message);
    }

    /**
     * Instantiates a new EncounterExistsException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public EncounterExistsException(String message, Throwable reason) {
        super(message, reason);
    }

}
