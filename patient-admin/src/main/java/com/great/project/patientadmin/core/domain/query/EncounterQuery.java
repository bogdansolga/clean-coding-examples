package com.great.project.patientadmin.core.domain.query;

import java.util.ArrayList;
import java.util.List;

import com.great.project.patientadmin.core.domain.model.Period;
import com.great.project.patientadmin.core.domain.model.encounter.ClassificationCoding;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.great.project.patientadmin.core.domain.model.identifier.BasicIdentifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncounterQuery implements IshQuery {

    private List<BasicIdentifier> serviceProviderIdentifiers = new ArrayList<>();
    private List<BasicIdentifier> nursingOrganizationIdentifiers = new ArrayList<>();
    private Period periodBegin;
    private Period periodEnd;
    private boolean excludeEncounterStatus;
    private List<StatusCoding> encounterStatuses = new ArrayList<>();
    private boolean excludeEncounterClassification;
    private List<ClassificationCoding> encounterClassifications = new ArrayList<>();

}
