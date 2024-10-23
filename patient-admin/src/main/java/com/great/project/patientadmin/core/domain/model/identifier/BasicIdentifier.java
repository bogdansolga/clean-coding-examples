package com.great.project.patientadmin.core.domain.model.identifier;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BasicIdentifier {
    private String system;
    private String value;
    private String use;
}
