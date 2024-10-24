package com.great.project.patientadmin.core.application.eventproducer;

import java.net.URI;
import java.util.UUID;

import com.great.project.common.events.domain.AugeroEventDataV1;
import com.great.project.common.events.domain.patientadmin.patient.PatientCancelledAugeroDomainEventV1;
import com.great.project.patientadmin.core.domain.model.Patient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class used to produce a {@link PatientCancelledAugeroDomainEventV1}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AugeroPatientCancelledEventProducer {

    private static final String GET_PATIENT_BY_ID_URL = "/patient-admin/v1/patient/";
    private static final String PATIENT_ADMIN_SOURCE = "https://cerner.com/augero/domain-events/patient-admin";

    public static PatientCancelledAugeroDomainEventV1 produce(Patient patient) {
        AugeroEventDataV1 eventData = new AugeroEventDataV1(patient.getId(), creteEndpointUri(patient.getId()),
                patient.getId(), null);
        return new PatientCancelledAugeroDomainEventV1(eventData, PATIENT_ADMIN_SOURCE);
    }

    private static URI creteEndpointUri(UUID id) {
        return URI.create(GET_PATIENT_BY_ID_URL.concat(id.toString()));
    }
}
