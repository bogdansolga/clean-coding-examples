package com.great.project.patientadmin.core.domain.model;

import java.util.UUID;

import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
public class BirthDateAbsentCoding extends BaseCoding {

    public BirthDateAbsentCoding(UUID id, String system, String version, String code, String display,
            Boolean userSelected) {
        super(id, system, version, code, display, userSelected);
    }
}
