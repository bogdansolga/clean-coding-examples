package com.great.project.patientadmin.core.domain.model.encounter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.Reference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicEncounter {
    private UUID id;
    private UUID subject;
    private Reference serviceProvider;
    private Reference nursingOrganization;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private StatusCoding status;
    private ClassificationCoding classification;
}
