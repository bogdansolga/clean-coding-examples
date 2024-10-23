package com.great.project.patientadmin.core.domain.model.contact;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.great.project.patientadmin.core.domain.model.CodeableConcept;
import com.great.project.patientadmin.core.domain.model.Person;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends Person implements Serializable {
    private List<CodeableConcept> relationship;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
