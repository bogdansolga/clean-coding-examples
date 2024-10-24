// **************************************************************************************************************************
//
//
//
//
//
// This file contains a risk mitigation
//
//
//
//
//
// **************************************************************************************************************************

package com.great.project.patientadmin.core.application.eventhandler;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.encounter.EncounterCreatedECCDomainEventV1;
import com.great.project.common.events.domain.patientadmin.eventdata.encounter.EncounterECCEventDataV1;
import com.great.project.patientadmin.core.application.PatientAdminService;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EventHandler} that handles the creation of an encounter.
 */
@Component
public class EncounterCreatedEventHandler implements EventHandler<EncounterCreatedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public EncounterCreatedEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    // **************************************************************************************************************************
    // Start risk mitigation for risk item: 1.001.003.001
    // **************************************************************************************************************************

    @Override
    public void processEvent(EncounterCreatedECCDomainEventV1 event) {
        EncounterECCEventDataV1 encounterEventData = event.getEventData();
        var encounter = EncounterEventMapper.mapEncounterEventDataToEncounter(encounterEventData);
        patientAdminService.createEncounter(encounter, encounterEventData.getSubject().getIdentifier());
    }

    // **************************************************************************************************************************
    // End risk mitigation
    // **************************************************************************************************************************

    @Override
    public String eventCodeSupported() {
        return EventCodes.ENCOUNTER_CREATED.getMessage();
    }
}