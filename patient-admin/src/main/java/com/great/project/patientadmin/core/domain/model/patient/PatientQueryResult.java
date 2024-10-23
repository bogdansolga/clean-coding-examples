package com.great.project.patientadmin.core.domain.model.patient;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PatientQueryResult {

    private boolean limitExceeded = false;
    private List<PatientWithReplaceLink> patients;
}
