package com.great.project.patientadmin.core.application.eventhandler;

import java.util.Optional;

import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;
import org.springframework.stereotype.Component;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.patient.PatientDeletedECCDomainEventV1;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link EventHandler} that handles the deletion of a patient.
 */

@Component
@RequiredArgsConstructor
public class PatientDeletedEventHandler implements EventHandler<PatientDeletedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    @Override
    public void processEvent(PatientDeletedECCDomainEventV1 event) {
        var patient = PatientEventMapper.mapPatientEventDataToPatient(event.getEventData());
        findGoldenIdentifier(patient)
                .ifPresent(identifier -> patientAdminService.deletePatientByIdentifier(identifier.getUse().getCode(),
                        identifier.getValue(), identifier.getSystem()));
    }

    private Optional<Identifier> findGoldenIdentifier(Patient patient) {

        return patient.getIdentifier().stream().filter(e -> e.getUse().getCode().equalsIgnoreCase("official"))
                .findFirst();
    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.PATIENT_DELETED.getMessage();
    }

}
