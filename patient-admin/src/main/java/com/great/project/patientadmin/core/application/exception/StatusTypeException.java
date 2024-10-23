package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;

/**
 * Class for {@link PatientAdminService} exception in case of status type of the {@link Encounter} it's not proper for
 * the action implemented (eg. create, update, cancel), defined as unchecked exception.
 * 
 * @author Lung Alexandru
 */
public class StatusTypeException extends AugeroRuntimeException {

    /**
     * Generated serial version ID
     */

    private static final long serialVersionUID = 4478045841274052313L;

    /**
     * Instantiates a new StatusTypeException runtime exception.
     *
     * @param message
     *            the message
     */
    public StatusTypeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new StatusTypeException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public StatusTypeException(String message, Throwable reason) {
        super(message, reason);
    }

}