package com.great.project.patientadmin.core.domain.model.patient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.BirthDateAbsentCoding;
import com.great.project.patientadmin.core.domain.model.PersonBirthDateUnknown;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.great.project.patientadmin.core.domain.model.identifier.BasicIdentifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDataWithEncounters {
    private UUID id;
    private String name;
    private String age;
    private String genderDisplayText;
    private LocalDate birthDate;
    private BirthDateAbsentCoding birthDateAbsent;
    private PersonBirthDateUnknown birthDateUnknown;
    private Boolean inexactDateOfBirth;
    private BasicIdentifier goldenIdentifier;
    private StatusCoding status;
    private Boolean active;
    private Boolean deceased;
    private List<SelectedEncounter> encounterList;
}
