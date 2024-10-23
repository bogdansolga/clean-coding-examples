package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;

/**
 * Class for {@link PatientAdminService} exception in case of active flag of the {@link Patient} is not proper set for
 * the action implemented (eg. create, update, cancel), defined as unchecked exception.
 *
 * @author Madalina Lupu
 */
public class ActivePatientException extends AugeroRuntimeException {

    private static final long serialVersionUID = 4478045841274052313L;

    /**
     * Instantiates a new ActivePatientException runtime exception.
     *
     * @param message
     *            the message
     */
    public ActivePatientException(String message) {
        super(message);
    }

    /**
     * Instantiates a new ActivePatientException runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public ActivePatientException(String message, Throwable reason) {
        super(message, reason);
    }

}
