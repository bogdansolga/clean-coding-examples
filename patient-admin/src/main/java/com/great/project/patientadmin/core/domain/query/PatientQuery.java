package com.great.project.patientadmin.core.domain.query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.great.project.patientadmin.core.domain.model.encounter.ClassificationCoding;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.great.project.patientadmin.core.domain.model.identifier.BasicIdentifier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientQuery implements IshQuery {
    private int limit = 50;
    private BasicIdentifier serviceProvider;
    private List<BasicIdentifier> departmentOrganizationIdentifiers = new ArrayList<>();
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private List<StatusCoding> encounterStatuses = new ArrayList<>();
    private boolean excludeEncounterStatus;
    private List<StatusCoding> encounterStatusesFilter = new ArrayList<>();
    private boolean excludeEncounterStatusFilter;
    private List<ClassificationCoding> encounterClassifications = new ArrayList<>();
    private boolean excludeEncounterClassification;
    private List<ClassificationCoding> encounterClassificationsFilter = new ArrayList<>();
    private boolean excludeEncounterClassificationFilter;
    private String patientName;
    private String dateOfBirth;
    private boolean dateOfBirthAbsent;
    private String patientGoldenIdentifier;
    private List<StatusCoding> patientStatuses = new ArrayList<>();
    private boolean excludeDeceased;
}
