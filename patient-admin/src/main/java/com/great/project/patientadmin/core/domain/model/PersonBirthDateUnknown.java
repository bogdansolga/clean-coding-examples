package com.great.project.patientadmin.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PersonBirthDateUnknown {

    DAY_UNKNOWN("DayUnknown"), DAY_AND_MONTH_UNKNOWN("DayAndMonthUnknown");

    private final String value;

    public static PersonBirthDateUnknown fromString(String text) {
        for (PersonBirthDateUnknown birthDateUnknown : PersonBirthDateUnknown.values()) {
            if (birthDateUnknown.value.equalsIgnoreCase(text)) {
                return birthDateUnknown;
            }
        }
        return null;
    }

}