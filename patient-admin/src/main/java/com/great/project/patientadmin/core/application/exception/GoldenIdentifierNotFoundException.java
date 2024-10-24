package com.great.project.patientadmin.core.application.exception;

import com.great.project.core.exception.AugeroRuntimeException;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

/**
 * Class for {@link PatientAdminService} exception in case of Golden {@link Identifier} entity was not found , defined
 * as unchecked exception.
 */
public class GoldenIdentifierNotFoundException extends AugeroRuntimeException {

    /**
     * Generated Serial Version ID
     */
    private static final long serialVersionUID = -7829156197166831013L;

    /**
     * Instantiates a new Patient Admin Hexagon core service {@link GoldenIdentifierNotFoundException} runtime
     * exception.
     *
     * @param message
     *            the message
     */
    public GoldenIdentifierNotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Patient Admin Hexagon core service {@link GoldenIdentifierNotFoundException} runtime
     * exception.
     *
     * @param message
     *            the message
     * @param reason
     *            the reason
     */
    public GoldenIdentifierNotFoundException(String message, Throwable reason) {
        super(message, reason);
    }

}
