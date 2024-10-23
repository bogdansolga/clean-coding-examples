package com.great.project.patientadmin.core.domain.model.patient;

import java.util.List;

import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PatientReplaced {
    private final Patient replacingPatient;
    private final Patient replacedPatient;
    private final List<Encounter> updatedEncounters;
}
