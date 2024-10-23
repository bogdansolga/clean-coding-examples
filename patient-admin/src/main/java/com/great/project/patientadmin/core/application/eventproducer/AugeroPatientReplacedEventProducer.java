package com.great.project.patientadmin.core.application.eventproducer;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cerner.augero.common.events.domain.patientadmin.eventdata.PatientReplacedAugeroEventDataV1;
import com.cerner.augero.common.events.domain.patientadmin.eventdata.encounter.ChangedEncounterDataType;
import com.cerner.augero.common.events.domain.patientadmin.patient.PatientReplacedAugeroDomainEventV1;
import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.patient.PatientReplaced;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class used to produce a {@link PatientReplacedAugeroDomainEventV1}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class AugeroPatientReplacedEventProducer {

    private static final String GET_PATIENT_BY_ID_URL = "/patient-admin/v1/patient/";
    private static final String GET_ENCOUNTER_BY_ID_URL = "/patient-admin/v1/encounter/";
    private static final String PATIENT_ADMIN_SOURCE = "https://cerner.com/augero/domain-events/patient-admin";

    public static PatientReplacedAugeroDomainEventV1 produce(PatientReplaced patientReplaced) {
        return new PatientReplacedAugeroDomainEventV1(createEventData(patientReplaced), PATIENT_ADMIN_SOURCE);
    }

    private static PatientReplacedAugeroEventDataV1 createEventData(PatientReplaced patientReplaced) {
        Patient patient = patientReplaced.getReplacingPatient();
        return new PatientReplacedAugeroEventDataV1(patientReplaced.getReplacedPatient().getId(),
                createURI(GET_PATIENT_BY_ID_URL, patientReplaced.getReplacedPatient().getId()),
                createChangedEncounters(patientReplaced.getUpdatedEncounters()),
                patient.getId(),  createURI(GET_PATIENT_BY_ID_URL, patient.getId()),
                patient.getId(), null);
    }

   private static URI createURI(String url, UUID id) {
       return URI.create(url.concat(id.toString()));
   }

   private static List<ChangedEncounterDataType> createChangedEncounters(List<Encounter> ids) {
        return ids.stream()
                .map(encounter -> new ChangedEncounterDataType(encounter.getId(), createURI(GET_ENCOUNTER_BY_ID_URL, encounter.getId())))
                .collect(Collectors.toList());
   }
}
