package com.great.project.patientadmin.core.application.eventhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.great.project.common.events.domain.patientadmin.eventdata.IdentifierEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.ReferenceEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.encounter.EncounterECCEventDataV1;
import com.great.project.patientadmin.core.domain.model.Reference;
import com.great.project.patientadmin.core.domain.model.encounter.ClassificationCoding;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;
import com.great.project.patientadmin.core.domain.model.identifier.IdentifierCodingUse;

/**
 * Utility class for mapping Encounter Event Data to internal Encounter model.
 */
public class EncounterEventMapper {

    private EncounterEventMapper() {
    }

    /**
     * Method to map the Encounter Event Data to internal Encounter model.
     * 
     * @param encounterEventData
     *            - the Encounter Event Data attached to the event
     * @return {@link Encounter}
     */
    public static Encounter mapEncounterEventDataToEncounter(EncounterECCEventDataV1 encounterEventData) {

        Encounter encounter = new Encounter();

        // identifier
        encounter.setIdentifier(mapToIdentifier(encounterEventData));

        // classification
        if (encounterEventData.getClassification() != null) {
            ClassificationCoding classification = new ClassificationCoding(null,
                    encounterEventData.getClassification().getSystem(),
                    encounterEventData.getClassification().getVersion(),
                    encounterEventData.getClassification().getCode(),
                    encounterEventData.getClassification().getDisplay(),
                    encounterEventData.getClassification().getUserSelected());
            encounter.setClassification(classification);
        }

        // account
        encounter.setAccount(mapToAccount(encounterEventData));

        // departmentOrganization
        encounter.setDepartmentOrganization(mapToReferenceDataType(encounterEventData.getDepartmentOrganization()));

        // nursingOrganization
        encounter.setNursingOrganization(mapToReferenceDataType(encounterEventData.getNursingOrganization()));

        // periodStart
        encounter.setStartDate(encounterEventData.getPeriodStart());

        // periodEnd
        encounter.setEndDate(encounterEventData.getPeriodEnd());

        // serviceProvider
        encounter.setServiceProvider(mapToReferenceDataType(encounterEventData.getServiceProvider()));

        // status
        if (encounterEventData.getStatus() != null) {
            StatusCoding status = new StatusCoding(null, encounterEventData.getStatus().getSystem(),
                    encounterEventData.getStatus().getVersion(), encounterEventData.getStatus().getCode(),
                    encounterEventData.getStatus().getDisplay(), encounterEventData.getStatus().getUserSelected());
            encounter.setStatus(status);
        }

        // subject
        // It's set in the EncounterCreatedEventHandler after query for Patient Id

        return encounter;
    }

    private static Reference mapToReferenceDataType(ReferenceEventDataType referenceEventDataType) {
        if (referenceEventDataType != null) {
            String link = referenceEventDataType.getLink() == null ? null : referenceEventDataType.getLink();
            String type = referenceEventDataType.getType() == null ? null : referenceEventDataType.getType();
            return new Reference(null, link, type, mapToIdentifierFromReference(referenceEventDataType));
        }

        return null;
    }

    private static List<Reference> mapToAccount(EncounterECCEventDataV1 encounterEventData) {
        return encounterEventData.getAccount() != null ? encounterEventData.getAccount().stream()
                .map(o -> new Reference(null, o.getLink(), o.getType(), mapToIdentifierFromReference(o)))
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    private static List<Identifier> mapToIdentifierFromReference(ReferenceEventDataType referenceEventData) {
        return referenceEventData.getIdentifier() != null
                ? referenceEventData.getIdentifier().stream().map(id -> new Identifier(null, getIdentifierUse(id), null,
                        id.getSystem(), id.getValue(), null, null, null)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<Identifier> mapToIdentifier(EncounterECCEventDataV1 encounterEventData) {
        return encounterEventData.getIdentifier() != null
                ? encounterEventData.getIdentifier().stream().map(id -> new Identifier(null, getIdentifierUse(id), null,
                        id.getSystem(), id.getValue(), null, null, null)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static IdentifierCodingUse getIdentifierUse(IdentifierEventDataType id) {
        return id.getUse() != null ? new IdentifierCodingUse(null, id.getUse().getSystem(), id.getUse().getVersion(),
                id.getUse().getCode(), id.getUse().getDisplay(), id.getUse().getUserSelected()) : null;
    }
}