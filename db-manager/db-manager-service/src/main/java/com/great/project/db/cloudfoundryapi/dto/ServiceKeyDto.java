package com.great.project.db.cloudfoundryapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ServiceKeyDto {
    private String guid;
    private String credentials;
}
