package com.great.project.patientadmin.core.application.eventhandler;

import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.domain.model.Patient;
import org.springframework.stereotype.Component;

import com.great.project.common.events.EventCodes;
import com.great.project.common.events.domain.patientadmin.patient.PatientReplacedECCDomainEventV1;

/**
 * Implementation of {@link EventHandler} that handles the merge of a patient.
 */
@Component
public class PatientReplacedEventHandler implements EventHandler<PatientReplacedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public PatientReplacedEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    @Override
    public void processEvent(PatientReplacedECCDomainEventV1 event) {
        Patient sourcePatientEvent = PatientEventMapper.mapPatientEventDataToPatient(event.getEventData().getSource());
        Patient targetPatientEvent = PatientEventMapper.mapPatientEventDataToPatient(event.getEventData().getTarget());

        patientAdminService.mergePatient(sourcePatientEvent, targetPatientEvent);

    }

    @Override
    public String eventCodeSupported() {
        return EventCodes.PATIENT_REPLACED.getMessage();
    }

}
