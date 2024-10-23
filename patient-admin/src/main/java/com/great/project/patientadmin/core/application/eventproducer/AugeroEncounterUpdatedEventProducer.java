package com.great.project.patientadmin.core.application.eventproducer;

import java.net.URI;
import java.util.UUID;

import com.cerner.augero.common.events.domain.AugeroEventDataV1;
import com.cerner.augero.common.events.domain.patientadmin.encounter.EncounterUpdatedAugeroDomainEventV1;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class used to produce a {@link EncounterUpdatedAugeroDomainEventV1}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AugeroEncounterUpdatedEventProducer {

    private static final String GET_ENCOUNTER_BY_ID_URL = "/patient-admin/v1/encounter/";
    private static final String PATIENT_ADMIN_SOURCE = "https://cerner.com/augero/domain-events/patient-admin";

    public static EncounterUpdatedAugeroDomainEventV1 produce(Encounter encounter) {
        AugeroEventDataV1 eventData = new AugeroEventDataV1(encounter.getId(), creteEndpointUri(encounter.getId()),
                encounter.getSubject(), encounter.getId());
        return new EncounterUpdatedAugeroDomainEventV1(eventData, PATIENT_ADMIN_SOURCE);
    }

    private static URI creteEndpointUri(UUID id) {
        return URI.create(GET_ENCOUNTER_BY_ID_URL.concat(id.toString()));
    }

}
