package com.great.project.patientadmin.core.domain.model.humanname;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class HumanName implements Serializable {
    private UUID id;
    private HumanNameCodingUse use;
    private String text;
    private String family;
    private List<HumanNameGiven> given;
    private List<HumanNamePrefix> prefix;
    private List<HumanNameSuffix> suffix;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isDisplayed;
}
