package com.great.project.patientadmin.core.domain.model.encounter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.Reference;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Encounter implements Serializable {
    private UUID id;
    private List<Identifier> identifier;
    private StatusCoding status;
    private ClassificationCoding classification;
    private UUID subject;
    private Reference departmentOrganization;
    private Reference nursingOrganization;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Reference> account;
    private Reference serviceProvider;
    private UUID replaces;
}
