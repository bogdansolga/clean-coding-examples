package com.great.project.patientadmin.core.application.eventhandler;

import com.cerner.augero.common.events.EventCodes;
import com.cerner.augero.common.events.domain.patientadmin.encounter.EncounterCancelledECCDomainEventV1;
import com.cerner.augero.common.events.domain.patientadmin.eventdata.encounter.EncounterECCEventDataV1;
import com.great.project.patientadmin.core.application.PatientAdminService;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EventHandler} that handles the cancel of an Encounter.
 * 
 * @author Lung Alexandru
 */

@Component
public class EncounterCancelledEventHandler implements EventHandler<EncounterCancelledECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public EncounterCancelledEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    @Override
    public void processEvent(EncounterCancelledECCDomainEventV1 event) {
        EncounterECCEventDataV1 encounterEventData = event.getEventData();
        var encounterCancelled = EncounterEventMapper.mapEncounterEventDataToEncounter( encounterEventData);
        patientAdminService.createCancelledEncounter(encounterCancelled, encounterEventData.getSubject().getIdentifier());
    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.ENCOUNTER_CANCELLED.getMessage();
    }
}