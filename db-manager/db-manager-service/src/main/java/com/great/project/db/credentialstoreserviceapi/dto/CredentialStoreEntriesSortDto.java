package com.great.project.db.credentialstoreserviceapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CredentialStoreEntriesSortDto {
    private boolean sorted;
    private boolean unsorted;
    private boolean empty;
}
