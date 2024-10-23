package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reference implements Serializable {
    private UUID id;
    private String link;
    private String type;
    private List<Identifier> identifier;
}
