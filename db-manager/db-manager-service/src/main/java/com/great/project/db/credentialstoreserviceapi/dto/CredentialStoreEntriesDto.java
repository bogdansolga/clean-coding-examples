package com.great.project.db.credentialstoreserviceapi.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CredentialStoreEntriesDto {

    private boolean last;
    private long totalPages;
    private long totalElements;
    private boolean first;
    private long numberOfElements;
    private long size;
    private long number;
    private boolean empty;
    private List<CredentialStoreEntriesEntryDto> content;
    private CredentialStoreEntriesSortDto sort;

}
