package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

/**
 * Class for {@link PatientAdminService} exception in case multiple Golden {@link Identifier} entities were found ,
 * defined as unchecked exception.
 */
public class GoldenIdentifierMultipleException extends AugeroRuntimeException {

    /**
     * Generated Serial Version ID
     */
    private static final long serialVersionUID = -4783237067036246977L;

    /**
     * Instantiates a new Patient Admin Hexagon core service {@link GoldenIdentifierMultipleException} runtime
     * exception.
     *
     * @param message
     *            the message
     */
    public GoldenIdentifierMultipleException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Patient Admin Hexagon core service {@link GoldenIdentifierMultipleException} runtime
     * exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public GoldenIdentifierMultipleException(String message, Throwable reason) {
        super(message, reason);
    }

}
