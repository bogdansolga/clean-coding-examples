package com.great.project.patientadmin.core.application.eventhandler;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.patient.PatientCancelledECCDomainEventV1;
import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EventHandler} that handles the cancel of a Patient.
 */
@Component
public class PatientCancelledEventHandler implements EventHandler<PatientCancelledECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public PatientCancelledEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    @Override
    public void processEvent(PatientCancelledECCDomainEventV1 event) {
        Patient patient = PatientEventMapper.mapPatientEventDataToPatient(event.getEventData());
        patientAdminService.cancelPatient(patient);

    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.PATIENT_CANCELLED.getMessage();
    }
}
