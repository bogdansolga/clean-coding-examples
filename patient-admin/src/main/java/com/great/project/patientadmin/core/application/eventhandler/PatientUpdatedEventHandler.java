package com.great.project.patientadmin.core.application.eventhandler;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.patient.PatientUpdatedECCDomainEventV1;
import com.great.project.patientadmin.core.application.PatientAdminService;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EventHandler} that handles the update of a patient.
 */
@Component
public class PatientUpdatedEventHandler implements EventHandler<PatientUpdatedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public PatientUpdatedEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    @Override
    public void processEvent(PatientUpdatedECCDomainEventV1 event) {
        var patientUpdated = PatientEventMapper.mapPatientEventDataToPatient(event.getEventData());
        patientAdminService.updatePatient(patientUpdated);
    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.PATIENT_UPDATED.getMessage();
    }

}
