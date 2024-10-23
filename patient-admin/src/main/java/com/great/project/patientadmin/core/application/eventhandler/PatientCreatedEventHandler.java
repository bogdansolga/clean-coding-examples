// **************************************************************************************************************************
//
//
//
//
//
// This file contains a risk mitigation.
//
//
//
//
//
// **************************************************************************************************************************

package com.great.project.patientadmin.core.application.eventhandler;

import com.cerner.augero.common.events.EventCodes;
import com.cerner.augero.common.events.domain.patientadmin.patient.PatientCreatedECCDomainEventV1;
import com.great.project.patientadmin.core.application.PatientAdminService;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link EventHandler} that handles the creation of a patient.
 * 
 * @author Gabriela Maciac, Andrei Maneasa
 */
@Component
public class PatientCreatedEventHandler implements EventHandler<PatientCreatedECCDomainEventV1> {

    private final PatientAdminService patientAdminService;

    public PatientCreatedEventHandler(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    // **************************************************************************************************************************
    // Start risk mitigation for risk item: 1.001.003.001
    // **************************************************************************************************************************

    @Override
    public void processEvent(PatientCreatedECCDomainEventV1 event) {
        var patient = PatientEventMapper.mapPatientEventDataToPatient(event.getEventData());
        patientAdminService.createPatient(patient);
    }
    // **************************************************************************************************************************
    // End risk mitigation
    // **************************************************************************************************************************

    @Override
    public String eventCodeSupported() {
        return EventCodes.PATIENT_CREATED.getMessage();
    }
}
