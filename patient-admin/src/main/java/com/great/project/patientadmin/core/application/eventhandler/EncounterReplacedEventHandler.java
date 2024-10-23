package com.great.project.patientadmin.core.application.eventhandler;

import java.util.UUID;

import com.great.project.patientadmin.core.application.PatientAdminService;
import org.springframework.stereotype.Component;

import com.cerner.augero.common.events.EventCodes;
import com.cerner.augero.common.events.domain.patientadmin.encounter.EncounterReplacedECCDomainEventV1;
import com.cerner.augero.common.events.domain.patientadmin.eventdata.IdentifierEventDataType;
import com.cerner.augero.common.events.domain.patientadmin.eventdata.encounter.EncounterECCEventDataV1;

/**
 * Implementation of {@link EventHandler} that handles the merge of an encounter.
 *
 * @author Catalin Matache
 */
@Component
public class EncounterReplacedEventHandler implements EventHandler<EncounterReplacedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public EncounterReplacedEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    @Override
    public void processEvent(EncounterReplacedECCDomainEventV1 event) {
        UUID patientId = findGoldenIdentifier(event.getEventData().getSource());
        var sourceEncounterEvent = EncounterEventMapper
                .mapEncounterEventDataToEncounter(event.getEventData().getSource());
        sourceEncounterEvent.setSubject(patientId);
        var targetEncounterEvent = EncounterEventMapper
                .mapEncounterEventDataToEncounter(event.getEventData().getTarget());
        targetEncounterEvent.setSubject(patientId);
        patientAdminService.replaceEncounter(sourceEncounterEvent, targetEncounterEvent);

    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.ENCOUNTER_REPLACED.getMessage();
    }

    private UUID findGoldenIdentifier(EncounterECCEventDataV1 encounter) {
        var identifier = encounter.getSubject().getIdentifier().stream()
                .filter(e -> e.getUse().getCode().equalsIgnoreCase("official")).findFirst()
                .orElse(new IdentifierEventDataType(null, null, null));
        return patientAdminService.readPatientIdByIdentifier(identifier.getUse().getCode(), identifier.getValue(),
                identifier.getSystem());
    }

}
