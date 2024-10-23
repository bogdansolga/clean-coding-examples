package com.great.project.patientadmin.core.domain.model.encounter;

import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.BaseCoding;

import lombok.NoArgsConstructor;

@SuppressWarnings("serial")
@NoArgsConstructor
public class StatusCoding extends BaseCoding {
    public StatusCoding(UUID id, String system, String version, String code, String display, Boolean userSelected) {
        super(id, system, version, code, display, userSelected);
    }
}
