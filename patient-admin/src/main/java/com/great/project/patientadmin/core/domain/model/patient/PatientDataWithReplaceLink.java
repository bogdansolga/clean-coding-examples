package com.great.project.patientadmin.core.domain.model.patient;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientDataWithReplaceLink extends PatientDataWithEncounters {
    private List<UUID> replacedPatientIds;
    private List<String> replacedPatientNames;

    public static PatientDataWithReplaceLink buildFrom(PatientDataWithEncounters patientDataWithEncounters,
            List<UUID> replacedPatientIds, List<String> replacedPatientNames) {
        var patientDataWithReplaceLink = new PatientDataWithReplaceLink();
        patientDataWithReplaceLink.setId(patientDataWithEncounters.getId());
        patientDataWithReplaceLink.setName(patientDataWithEncounters.getName());
        patientDataWithReplaceLink.setAge(patientDataWithEncounters.getAge());
        patientDataWithReplaceLink.setActive(patientDataWithEncounters.getActive());
        patientDataWithReplaceLink.setBirthDate(patientDataWithEncounters.getBirthDate());
        patientDataWithReplaceLink.setBirthDateAbsent(patientDataWithEncounters.getBirthDateAbsent());
        patientDataWithReplaceLink.setBirthDateUnknown(patientDataWithEncounters.getBirthDateUnknown());
        patientDataWithReplaceLink.setDeceased(patientDataWithEncounters.getDeceased());
        patientDataWithReplaceLink.setGenderDisplayText(patientDataWithEncounters.getGenderDisplayText());
        patientDataWithReplaceLink.setInexactDateOfBirth(patientDataWithEncounters.getInexactDateOfBirth());
        patientDataWithReplaceLink.setStatus(patientDataWithEncounters.getStatus());
        patientDataWithReplaceLink.setGoldenIdentifier(patientDataWithEncounters.getGoldenIdentifier());
        patientDataWithReplaceLink.setEncounterList(patientDataWithEncounters.getEncounterList());
        patientDataWithReplaceLink.setReplacedPatientIds(replacedPatientIds);
        patientDataWithReplaceLink.setReplacedPatientNames(replacedPatientNames);
        return patientDataWithReplaceLink;
    }
}
