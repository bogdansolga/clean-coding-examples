package com.great.project.patientadmin.core.domain.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.humanname.HumanName;
import com.great.project.patientadmin.core.domain.model.humanname.HumanNameCodingUse;
import com.great.project.patientadmin.core.domain.model.humanname.HumanNameGiven;

/**
 * Service class to provide business logic for setting unique patient name.
 */
public class HumanNameSelector {

    public HumanNameSelector() {
        // Do nothing
    }

    /**
     * Get unique patient name in the Patient object based on business logic use cases.
     * 
     * @param patient
     *            - {@link Patient} object
     * @return {@link HumanName} - the unique patient name
     */
    public HumanName getUniquePatientName(Patient patient) {
        ArrayList<HumanName> usualHumanNameList = new ArrayList<>();
        ArrayList<HumanName> officialHumanNameList = new ArrayList<>();
        ArrayList<HumanName> oldHumanNameList = new ArrayList<>();
        ArrayList<HumanName> maidenHumanNameList = new ArrayList<>();
        ArrayList<HumanName> tempHumanNameList = new ArrayList<>();
        ArrayList<HumanName> nickNameHumanNameList = new ArrayList<>();
        ArrayList<HumanName> anonymousHumanNameList = new ArrayList<>();
        ArrayList<HumanName> otherHumanNameList = new ArrayList<>();

        Optional.ofNullable(patient.getName()).orElse(new ArrayList<>()).stream().forEach(humanName -> Optional
                .of(humanName).map(HumanName::getUse).map(HumanNameCodingUse::getCode).ifPresent(code -> {
                    if (code.equals("USUAL")) {
                        usualHumanNameList.add(humanName);
                    } else if (code.equals("OFFICIAL")) {
                        officialHumanNameList.add(humanName);
                    } else if (code.equals("OLD")) {
                        oldHumanNameList.add(humanName);
                    } else if (code.equals("MAIDEN")) {
                        maidenHumanNameList.add(humanName);
                    } else if (code.equals("TEMP")) {
                        tempHumanNameList.add(humanName);
                    } else if (code.equals("NICKNAME")) {
                        nickNameHumanNameList.add(humanName);
                    } else if (code.equals("ANONYMOUS")) {
                        anonymousHumanNameList.add(humanName);
                    } else {
                        otherHumanNameList.add(humanName);
                    }
                }));

        Collections.sort(usualHumanNameList,
                Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));
        Collections.sort(officialHumanNameList,
                Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));
        Collections.sort(oldHumanNameList, Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));
        Collections.sort(maidenHumanNameList,
                Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));
        Collections.sort(tempHumanNameList,
                Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));
        Collections.sort(nickNameHumanNameList,
                Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));
        Collections.sort(anonymousHumanNameList,
                Comparator.comparing(HumanName::getFamily).thenComparing(compareGivenNames));

        return usualHumanNameList.stream().findFirst().orElseGet(() -> officialHumanNameList.stream().findFirst()
                .orElseGet(() -> oldHumanNameList.stream().findFirst().orElseGet(() -> maidenHumanNameList.stream()
                        .findFirst()
                        .orElseGet(() -> tempHumanNameList.stream().findFirst().orElseGet(() -> nickNameHumanNameList
                                .stream().findFirst()
                                .orElseGet(() -> anonymousHumanNameList.stream().findFirst().orElseGet(
                                        () -> otherHumanNameList.stream().findFirst().orElse(new HumanName()))))))));

    }

    /**
     * Compare given names introduced as input.
     * 
     * @return {@link HumanName} - alphabetically sorted given name.
     */
    Comparator<HumanName> compareGivenNames = (HumanName patientName, HumanName nextPatientName) -> {

        List<HumanNameGiven> givenNames = patientName.getGiven();
        List<HumanNameGiven> nextGivenNames = nextPatientName.getGiven();

        Collections.sort(givenNames, Comparator.comparing(HumanNameGiven::getValue));
        Collections.sort(nextGivenNames, Comparator.comparing(HumanNameGiven::getValue));

        int givenNamesSize = givenNames.size() <= nextGivenNames.size() ? givenNames.size() : nextGivenNames.size();
        int i = 0;

        while (i < givenNamesSize) {
            int result = givenNames.get(i).getValue().compareTo(nextGivenNames.get(i).getValue());
            if (result == 0)
                i++;
            else if (result < 0)
                return -1;
            else if (result > 0)
                return 1;
        }

        if (givenNames.size() < nextGivenNames.size())
            return -1;
        else if (givenNames.size() > nextGivenNames.size())
            return 1;
        else
            return 0;
    };
}