package com.great.project.patientadmin.core.application.eventproducer;

import java.net.URI;
import java.util.UUID;

import com.great.project.common.events.domain.AugeroEventDataV1;
import com.great.project.common.events.domain.patientadmin.patient.PatientUpdatedAugeroDomainEventV1;
import com.great.project.common.events.domain.patientadmin.patient.PatientUpdatedECCDomainEventV1;
import com.great.project.patientadmin.core.domain.model.Patient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class used to produce a {@link PatientUpdatedECCDomainEventV1}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public abstract class AugeroPatientUpdatedEventProducer {

    private static final String GET_PATIENT_BY_ID_URL = "/patient-admin/v1/patient/";
    private static final String PATIENT_ADMIN_SOURCE = "https://cerner.com/augero/domain-events/patient-admin";

    public static PatientUpdatedAugeroDomainEventV1 produce(Patient patient) {

        AugeroEventDataV1 eventData = new AugeroEventDataV1(patient.getId(), creteEndpointUri(patient.getId()),
                patient.getId(), null);

        return new PatientUpdatedAugeroDomainEventV1(eventData, PATIENT_ADMIN_SOURCE);
    }

    private static URI creteEndpointUri(UUID id) {
        return URI.create(GET_PATIENT_BY_ID_URL.concat(id.toString()));
    }
}