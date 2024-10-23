package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;
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
public abstract class BaseCoding implements Serializable {
    private UUID id;
    private String system;
    private String version;
    private String code;
    private String display;
    private Boolean userSelected;
}
