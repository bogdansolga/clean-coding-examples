package com.great.project.patientadmin.core.domain.model.patient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.Reference;
import com.great.project.patientadmin.core.domain.model.encounter.ClassificationCoding;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SelectedEncounter {
    private UUID id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private StatusCoding status;
    private ClassificationCoding classification;
    private Reference serviceProvider;
    private Reference departmentOrganization;
    private List<Reference> account;
}
