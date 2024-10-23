package com.great.project.patientadmin.core.domain.model.contactpoint;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class ContactPoint implements Serializable {
    private UUID id;
    private ContactPointCodingSystem system;
    private String value;
    private ContactPointCodingUse use;
    private Integer rank;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
