package com.great.project.patientadmin.core.application.eventhandler.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import com.great.project.common.events.domain.patientadmin.eventdata.PersonBirthDateUnknownEventDataType;

public class BirthDateParser {

    private BirthDateParser() {
    }

    public static LocalDate parseBirthdate(String birthDate) {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("[[yyyy]-[MM]-[dd]][[yyyy]-[MM]][[yyyy]]").optionalStart()
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1).parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                .optionalEnd().toFormatter();

        if (!birthDate.isEmpty())
            return LocalDate.parse(birthDate, formatter);

        return null;
    }

    public static PersonBirthDateUnknownEventDataType getBirthDateUnknownForPatient(String birthDate) {

        switch (birthDate.length()) {
        case 4:
            return PersonBirthDateUnknownEventDataType.DAY_AND_MONTH_UNKNOWN;
        case 7:
            return PersonBirthDateUnknownEventDataType.DAY_UNKNOWN;
        default:
            return null;
        }
    }

}
