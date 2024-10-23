package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;

import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends Person implements Serializable {

    private CodeableConcept maritalStatus;
    private StatusCoding status;
    private Reference managingOrganization;
    private Age age;
    private Gender gender;
    private Boolean inexactDoB;
}
