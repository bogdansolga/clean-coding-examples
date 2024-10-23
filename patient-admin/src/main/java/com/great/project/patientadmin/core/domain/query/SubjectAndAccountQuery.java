package com.great.project.patientadmin.core.domain.query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.encounter.ClassificationCoding;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.great.project.patientadmin.core.domain.model.identifier.BasicIdentifier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectAndAccountQuery implements IshQuery {
    private BasicIdentifier patientIdentifier;
    private BasicIdentifier accountIdentifier;
    private UUID patientInternalId;
    private BasicIdentifier serviceProvider;
    private List<BasicIdentifier> departmentOrganizationIdentifiers = new ArrayList<>();
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private List<StatusCoding> encounterStatuses = new ArrayList<>();
    private boolean excludeEncounterStatus;
    private List<ClassificationCoding> encounterClassifications = new ArrayList<>();
    private boolean excludeEncounterClassification;
}
