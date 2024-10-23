package com.great.project.patientadmin.core.domain.model.patient;

import java.util.ArrayList;
import java.util.List;

import com.great.project.patientadmin.core.domain.model.Patient;

import lombok.Getter;

@Getter
public class PatientWithEncounters {
    private final Patient patient;
    private final List<SelectedEncounter> encounterList = new ArrayList<>();

    public PatientWithEncounters(Patient patient) {
        this(patient, new ArrayList<>());
    }

    public PatientWithEncounters(Patient patient, List<SelectedEncounter> encounterList) {
        this.patient = patient;
        this.encounterList.addAll(encounterList);
    }
}
