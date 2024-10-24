package com.great.project.patientadmin.core.domain.services;

import static com.great.project.patientadmin.core.domain.model.PersonBirthDateUnknown.DAY_AND_MONTH_UNKNOWN;
import static com.great.project.patientadmin.core.domain.model.PersonBirthDateUnknown.DAY_UNKNOWN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.great.project.core.i18n.I18nService;
import com.great.project.patientadmin.core.domain.model.Age;
import com.great.project.patientadmin.core.domain.model.Patient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Service used to calculate the age of a Patient.
 */
public class AgeCalculator {

    private final I18nService i18nService;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class DateRange {
        private LocalDate startDate;
        private LocalDate endDate;
        private Optional<LocalDateTime> startTime;
        private LocalDateTime endTime;
    }

    public AgeCalculator(final I18nService i18nService) {
        this.i18nService = i18nService;
    }

    /**
     * Sets a calculated age for a patient.
     * The patient should have a birthdate and should not be deceased (
     * if it's deceased then it should have a deceased date time in order to calculate its age).
     *
     * @param patient - the current patient
     */
    public void setPatientAge(Patient patient) {
        // returns a DateRange object with the start - end interval set. If the patient doesn't have
        // the necessary data set, the age won't be calculated.
        DateRange ageRange = getAgeRange(patient);
        if (ageRange.getStartDate() == null) {
            patient.setInexactDoB(false);
        } else if (ageRange.getEndTime() == null || ageRange.getEndDate() == null) {
            patient.setInexactDoB(hasInexactDateOfBirth(patient));
        } else if (hasInexactDateOfBirth(patient)) {
            if (patient.getBirthDateUnknown() == DAY_UNKNOWN) {
                setAgeWithMissingDay(patient, ageRange);
            } else if (patient.getBirthDateUnknown() == DAY_AND_MONTH_UNKNOWN) {
                setAgeWithDayAndMonthUnknown(patient, ageRange);
            }
            patient.setInexactDoB(true);
        } else {
            setCalculatedAge(patient, ageRange);
            patient.setInexactDoB(false);
        }
    }

    private boolean hasInexactDateOfBirth(Patient patient) {
        return patient.getBirthDateUnknown() == DAY_UNKNOWN || patient.getBirthDateUnknown() == DAY_AND_MONTH_UNKNOWN;
    }

    /**
     * Calculates and sets the age of the patient when the Day of birth are unknown.
     * <p>
     * The age is expressed in <b>value</b> and <b>unit</b> based on the following rules:<br>
     * <blockquote> &gt;= 2 years; years <br>
     * &gt;= 2 months &lt;= 2 years; months<br>
     * </blockquote>
     * 
     * @param patient
     *            - the patient whose Age is calculated
     */
    private void setAgeWithMissingDay(Patient patient, DateRange dateRange) {
        Period ageAsPeriodForMissingDay = Period.between(dateRange.getStartDate(), dateRange.getEndDate());
        if (ageAsPeriodForMissingDay.getYears() >= 2) {
            calculateAgeWithUnitWhenDateOfBirthIsInexact(patient, dateRange, ChronoUnit.YEARS);
        } else if (ageAsPeriodForMissingDay.getMonths() >= 2 || ageAsPeriodForMissingDay.getYears() == 1) {
            calculateAgeWithUnitWhenDateOfBirthIsInexact(patient, dateRange, ChronoUnit.MONTHS);
        }
    }

    /**
     * Calculates and sets the age of the patient when the Day and Month of birth are unknown.
     * <p>
     * The age is expressed in <b>value</b> and <b>unit</b> based on the following rules:<br>
     * <blockquote> &gt;= 2 years; years <br>
     * </blockquote>
     * 
     * @param patient
     *            - the patient whose Age is calculated
     */
    private void setAgeWithDayAndMonthUnknown(Patient patient, DateRange dateRange) {
        Period ageAsPeriodForMissingDayAndMonth = Period.between(dateRange.getStartDate(), dateRange.getEndDate());
        if (ageAsPeriodForMissingDayAndMonth.getYears() >= 2) {
            calculateAgeWithUnitWhenDateOfBirthIsInexact(patient, dateRange, ChronoUnit.YEARS);
        }
    }

    private void calculateAgeWithUnitWhenDateOfBirthIsInexact(Patient patient, DateRange dateRange, ChronoUnit unit) {
        patient.setAge(calculateAgeWithUnit(dateRange, unit));
    }

    /**
     * Calculates and sets the age of the patient associated with this AgeCalculator instance.
     * <p>
     * The age is expressed in <b>value</b> and <b>unit</b> based on the following rules:<br>
     * <blockquote> &lt;48 hours - &gt; hours<br>
     * 2 days &lt;= 2 months - &gt; days<br>
     * 2 months &lt;= 2 years - &gt; months<br>
     * &gt;= 2 years -&gt; years </blockquote>
     * 
     * @param patient
     *            - the patient whose Age is calculated
     */
    private void setCalculatedAge(Patient patient, DateRange ageRange) {

        Period ageAsPeriod = Period.between(ageRange.getStartDate(), ageRange.getEndDate());

        if (ageAsPeriod.getYears() >= 2) {
            patient.setAge(calculateAgeWithUnit(ageRange, ChronoUnit.YEARS));

        } else if (ageAsPeriod.getMonths() >= 2 || ageAsPeriod.getYears() == 1) {
            patient.setAge(calculateAgeWithUnit(ageRange, ChronoUnit.MONTHS));

        } else if (ageAsPeriod.getDays() >= 2 || ageAsPeriod.getMonths() == 1) {
            patient.setAge(calculateAgeWithUnit(ageRange, ChronoUnit.DAYS));
        } else if (!ageRange.getStartTime().isPresent()) {
            patient.setAge(calculateAgeWithUnit(ageRange, ChronoUnit.DAYS));
        } else {
            patient.setAge(calculateAgeWithUnit(ageRange, ChronoUnit.HOURS));
        }
    }

    private DateRange getAgeRange(Patient patient) {

        Optional<LocalDateTime> birthTime = Optional.ofNullable(patient.getBirthTime());

        DateRange ageRange = new DateRange(patient.getBirthDate(), LocalDate.now(), birthTime,
                convertToUTC(LocalDateTime.now()));

        if (Boolean.TRUE.equals(patient.getDeceasedBoolean())) {
            if (patient.getDeceasedDateTime() != null) {
                ageRange.setEndDate(patient.getDeceasedDateTime().toLocalDate());
                ageRange.setEndTime(patient.getDeceasedDateTime());
            } else {
                ageRange.setEndDate(null);
                ageRange.setEndTime(null);
            }
        }
        return ageRange;

    }

    private Age calculateAgeWithUnit(DateRange ageRange, ChronoUnit unit) {

        Long betweenAmountOfTime = (ageRange.getStartTime().isPresent())
                ? unit.between(ageRange.getStartTime().get(), ageRange.getEndTime())
                : unit.between(ageRange.getStartDate(), ageRange.getEndDate());
        Integer betweenAmountOfTimeInteger = betweenAmountOfTime.intValue();

        Age age = new Age();

        age.setValue(
                Optional.ofNullable(betweenAmountOfTimeInteger).map(value -> value < 1 ? Integer.valueOf(1) : value));
        String unitUncapitalized = StringUtils.uncapitalize(unit.toString());
        age.setUnit(unit.toString());
        age.setDisplayLongText(i18nService.getMessageText(String.format("patient.age.%s", unitUncapitalized)));
        age.setDisplayShortText(i18nService.getMessageText(String.format("patient.age.short.%s", unitUncapitalized)));

        return age;

    }

    private LocalDateTime convertToUTC(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }
}