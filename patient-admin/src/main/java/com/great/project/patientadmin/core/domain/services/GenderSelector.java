package com.great.project.patientadmin.core.domain.services;

import java.util.Optional;

import com.great.project.core.i18n.I18nService;
import com.great.project.patientadmin.core.domain.model.Gender;
import com.great.project.patientadmin.core.domain.model.Patient;

/**
 * Service class which provide business logic to get the Patient gender.
 */

public class GenderSelector {

    private static final String PATIENT_GENDER_DYNAMIC = "patient.gender.%s";
    private static final String PATIENT_GENDER_DYNAMIC_SHORT = "patient.gender.short.%s";
    private static final String PATIENT_GENDER_OTHER = "patient.gender.other";
    private static final String PATIENT_GENDER_DIVERSE = "patient.gender.diverse";
    private static final String PATIENT_GENDER_NOTDETERMINED = "patient.gender.notdetermined";
    private static final String PATIENT_GENDER_OTHER_SHORT = "patient.gender.short.other";
    private static final String PATIENT_GENDER_DIVERSE_SHORT = "patient.gender.short.diverse";
    private static final String PATIENT_GENDER_NOTDETERMINED_SHORT = "patient.gender.short.notdetermined";
    private static final String EMPTY_TEXT = "";

    private I18nService i18nService;

    public GenderSelector(final I18nService i18nService) {
        this.i18nService = i18nService;
    }

    /**
     * Gets the gender of a patient based on his attributes genderAdministrative and getGenderSupplemental.
     * 
     * @param patient
     *            - the patient to get gender
     */
    public Gender getPatientGender(Patient patient) {
        return Optional.ofNullable(patient.getGenderAdministrative())
                .map(g -> getSelectedGender(patient))
                .orElse(composeGenderAttributes(EMPTY_TEXT, EMPTY_TEXT));
    }

    private Gender getSelectedGender(Patient patient) {
        return Optional.ofNullable(patient.getGenderAdministrative().getCode())
                .map(gc -> getGenderIfAdministrativeCodeIsOther(patient, gc))
                .orElse(composeGenderAttributes(EMPTY_TEXT, EMPTY_TEXT));

    }

    private Gender getGenderIfAdministrativeCodeIsOther(Patient patient, String genderAdministrativeCode) {
        if (genderAdministrativeCode.equalsIgnoreCase("other")) {
            return getGenderSupplementalIfCodeExists(patient);
        }
        return getGenderAdministrative(genderAdministrativeCode);
    }

    private Gender getGenderSupplementalIfCodeExists(Patient patient) {
        if (patient.getGenderSupplemental() == null) {
            return composeGenderAttributes(EMPTY_TEXT, EMPTY_TEXT);
        }
        String genderSupplementalCode = patient.getGenderSupplemental().getCode();
        if (genderSupplementalCode != null) {
            if (genderSupplementalCode.equalsIgnoreCase("X")) {
                return composeGenderAttributes(i18nService.getMessageText(PATIENT_GENDER_NOTDETERMINED),
                        i18nService.getMessageText(PATIENT_GENDER_NOTDETERMINED_SHORT));
            } else if (genderSupplementalCode.equalsIgnoreCase("D")) {
                return composeGenderAttributes(i18nService.getMessageText(PATIENT_GENDER_DIVERSE),
                        i18nService.getMessageText(PATIENT_GENDER_DIVERSE_SHORT));
            } else {
                return composeGenderAttributes(i18nService.getMessageText(PATIENT_GENDER_OTHER),
                        i18nService.getMessageText(PATIENT_GENDER_OTHER_SHORT));
            }
        }
        return composeGenderAttributes(EMPTY_TEXT, EMPTY_TEXT);
    }

    private Gender getGenderAdministrative(String code) {
        String lowerCaseCode = code.toLowerCase();
        String acceptedGenders = "\\b(?:male|female|unknown)\\b";
        if (lowerCaseCode.matches(acceptedGenders)) {
            return composeGenderAttributes(
                    i18nService.getMessageText(String.format(PATIENT_GENDER_DYNAMIC, code.toLowerCase())),
                    i18nService.getMessageText(String.format(PATIENT_GENDER_DYNAMIC_SHORT, code.toLowerCase())));
        }
        return composeGenderAttributes(EMPTY_TEXT, EMPTY_TEXT);
    }

    private Gender composeGenderAttributes(String messageKey, String messageKeyShort) {
        Gender gender = new Gender();
        gender.setDisplayLongText(messageKey);
        gender.setDisplayShortText(messageKeyShort);
        return gender;
    }
}
