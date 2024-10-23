package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Communication implements Serializable {
    private UUID id;
    private CodeableConcept language;
    private Boolean preferred;
}
