package com.great.project.patientadmin.core.application.exception;

import com.cerner.augero.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

/**
 * Class for {@link PatientAdminService} exception in case of mismatch Golden {@link Identifier} from Patient, defined
 * as unchecked exception.
 *
 * @author Catalin Matache
 */
public class GoldenIdentifierMismatchException extends AugeroRuntimeException {

    /**
     * Instantiates a new {@link GoldenIdentifierMismatchException} runtime exception.
     *
     * @param message
     *            the message
     */
    public GoldenIdentifierMismatchException(String message) {
        super(message);
    }

    /**
     * Instantiates a new {@link GoldenIdentifierMismatchException} runtime exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public GoldenIdentifierMismatchException(String message, Throwable reason) {
        super(message, reason);
    }
}
