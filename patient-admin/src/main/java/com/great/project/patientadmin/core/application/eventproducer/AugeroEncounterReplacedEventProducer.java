package com.great.project.patientadmin.core.application.eventproducer;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import com.great.project.common.events.domain.patientadmin.encounter.EncounterReplacedAugeroDomainEventV1;
import com.great.project.common.events.domain.patientadmin.eventdata.encounter.EncounterReplacedAugeroEventDataV1;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.encounter.EncounterReplaced;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class used to produce a {@link EncounterReplaced}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AugeroEncounterReplacedEventProducer {

    private static final String GET_ENCOUNTER_BY_ID_URL = "/patient-admin/v1/encounter/";
    private static final String PATIENT_ADMIN_SOURCE = "https://cerner.com/augero/domain-events/patient-admin";

    public static EncounterReplacedAugeroDomainEventV1 produce(EncounterReplaced encounterReplaced) {
        return new EncounterReplacedAugeroDomainEventV1(createEventData(encounterReplaced), PATIENT_ADMIN_SOURCE);
    }

    private static EncounterReplacedAugeroEventDataV1 createEventData(EncounterReplaced encounterReplaced) {
        UUID replacedEncounterId = Optional.ofNullable(encounterReplaced.getReplacedEncounter()).map(Encounter::getId)
                .orElse(null);

        UUID replacingEncounterId = encounterReplaced.getReplacingEncounter().getId();
        return new EncounterReplacedAugeroEventDataV1(replacedEncounterId, createURI(replacedEncounterId),
                replacingEncounterId, createURI(replacingEncounterId), null, replacingEncounterId);
    }

    private static URI createURI(UUID id) {
        return Optional.ofNullable(id).map(i -> URI.create(GET_ENCOUNTER_BY_ID_URL.concat(i.toString()))).orElse(null);

    }

}
