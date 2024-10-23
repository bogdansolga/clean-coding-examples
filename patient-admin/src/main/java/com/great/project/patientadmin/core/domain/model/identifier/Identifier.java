package com.great.project.patientadmin.core.domain.model.identifier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.CodeableConcept;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Identifier implements Serializable {
    private UUID id;
    private IdentifierCodingUse use;
    private CodeableConcept type;
    private String system;
    private String value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String assigner;
}
