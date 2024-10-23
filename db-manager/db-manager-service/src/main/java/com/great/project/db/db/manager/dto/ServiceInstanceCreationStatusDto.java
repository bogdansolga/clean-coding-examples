package com.great.project.db.db.manager.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ServiceInstanceCreationStatusDto {
    private String lastOperationType;
    private String lastOperationState;
}
