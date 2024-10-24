package com.great.project.patientadmin.core.application.eventhandler;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.encounter.EncounterUpdatedECCDomainEventV1;
import com.great.project.common.events.domain.patientadmin.eventdata.encounter.EncounterECCEventDataV1;
import com.great.project.patientadmin.core.application.PatientAdminService;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EventHandler} that handles the update of an Encounter.
 */
@Component
public class EncounterUpdatedEventHandler implements EventHandler<EncounterUpdatedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public EncounterUpdatedEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    @Override
    public void processEvent(EncounterUpdatedECCDomainEventV1 event) {
        EncounterECCEventDataV1 encounterEventData = event.getEventData();
        var encounterUpdated = EncounterEventMapper.mapEncounterEventDataToEncounter(encounterEventData);
        patientAdminService.updateEncounter(encounterUpdated, encounterEventData.getSubject().getIdentifier());
    }


    @Override
    public String eventCodeSupported() {
        return EventCodes.ENCOUNTER_UPDATED.getMessage();
    }
}