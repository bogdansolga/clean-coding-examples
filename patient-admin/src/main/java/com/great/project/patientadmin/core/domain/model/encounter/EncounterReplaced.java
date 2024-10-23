package com.great.project.patientadmin.core.domain.model.encounter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EncounterReplaced {
    private final Encounter replacingEncounter;
    private final Encounter replacedEncounter;
}
