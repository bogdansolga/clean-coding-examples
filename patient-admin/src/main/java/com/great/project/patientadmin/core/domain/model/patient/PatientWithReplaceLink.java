package com.great.project.patientadmin.core.domain.model.patient;

import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.Patient;

import lombok.Getter;

@Getter
public class PatientWithReplaceLink extends PatientWithEncounters {

    private final List<UUID> replacedPatientIds;
    private final List<String> replacedPatientNames;

    public PatientWithReplaceLink(Patient patient,
            List<UUID> replacedPatientIds, List<String> replacedPatientNames) {
        super(patient);
        this.replacedPatientIds = replacedPatientIds;
        this.replacedPatientNames = replacedPatientNames;
    }
}
