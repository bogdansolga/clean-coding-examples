package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Link implements Serializable {
    private Coding type;
}
