package com.great.project.patientadmin.core.application.eventhandler;

import java.util.Optional;

import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;
import org.springframework.stereotype.Component;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.encounter.EncounterDeletedECCDomainEventV1;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link EventHandler} that handles the deletion of an encounter.
 */
@Component
@RequiredArgsConstructor
public class EncounterDeletedEventHandler implements EventHandler<EncounterDeletedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    @Override
    public void processEvent(EncounterDeletedECCDomainEventV1 event) {
        event.getEventData().getEncounters().stream().map(EncounterEventMapper::mapEncounterEventDataToEncounter)
                .forEach(this::deleteEncountersWithGoldenIdentifier);
    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.ENCOUNTER_DELETED.getMessage();
    }

    private void deleteEncountersWithGoldenIdentifier(Encounter encounter) {
        findGoldenIdentifier(encounter)
                .ifPresent(identifier -> patientAdminService.deleteEncounterByIdentifier(identifier.getUse().getCode(),
                        identifier.getValue(), identifier.getSystem()));
    }

    private Optional<Identifier> findGoldenIdentifier(Encounter encounter) {
        return encounter.getIdentifier().stream().filter(e -> e.getUse().getCode().equalsIgnoreCase("official"))
                .findFirst();
    }
}
