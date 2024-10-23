package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;
import java.util.List;
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
public class CodeableConcept implements Serializable {
    private UUID id;
    private List<Coding> coding;
    private String text;
}
