package com.great.project.patientadmin.core.application.port.secondary;

import java.util.Optional;

import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

/**
 * Secondary port that specifies FHIR Primary System (ECC) operations.
 *
 * @author Catalin Matache
 */
public interface PrimarySystemPort {
    /**
     * Search for a {@link Patient} using a golden {@link Identifier}
     * 
     * @param use
     *            the use of the golden {@link Identifier}
     * @param value
     *            the value of the golden {@link Identifier}
     * @param system
     *            the system of the golden {@link Identifier}
     * @return the {@link Patient} wrapped by an {@link Optional}
     */
    Optional<Patient> findPatientByGoldenIdentifier(String use, String value, String system);
}
