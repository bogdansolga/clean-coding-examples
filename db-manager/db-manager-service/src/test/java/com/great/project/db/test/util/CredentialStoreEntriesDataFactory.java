package com.great.project.db.test.util;

import java.util.ArrayList;
import java.util.List;

import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntriesSortDto;
import com.great.project.db.credentialstoreserviceapi.dto.CredentialStoreEntryDto;
import com.great.project.db.credentialstoreserviceapi.dto.NewCredentialStoreEntryDto;

/**
 * Class to create mock DTOs for CredentialStoreEntriesDto, CredentialStoreEntryDto, NewCredentialStoreEntryDto
 */
public class CredentialStoreEntriesDataFactory {

    private CredentialStoreEntriesDataFactory() {
    }

    /**
     * CredentialStoreEntriesDto
     */
    private static final boolean LAST = false;
    private static final long TOTAL_PAGES = 1;
    private static final long TOTAL_ELEMENTS = 2;
    private static final boolean FIRST = true;
    private static final long NUMBER_OF_ELEMENTS = 3;
    private static final long SIZE = 4;
    private static final long NUMBER = 5;
    private static final boolean EMPTY = false;
    private static final String NAME_1 = "credential-store-entries-entry";
    private static final boolean SORTED = true;
    private static final boolean UNSORTED = false;

    /**
     * CredentialStoreEntryDto
     */
    public static final String ID = "unique-id";
    private static final String NAME_2 = "credential-store-entry";
    public static final String MODIFIED_AT = "modified-at";
    public static final String METADATA = "metadata";
    public static final String VALUE = "value";
    public static final String USERNAME = "username";
    public static final String TYPE = "type";

    /**
     * NewCredentialStoreEntryDto
     */
    public static final String NEW_METADATA = "new-metadata";
    public static final String NEW_VALUE = "new-value";
    public static final String NEW_USERNAME = "new-username";
    public static final String NAME_3 = "new-credential-store-entry";

    /**
     * Method to construct a mock CredentialStoreEntriesDto
     * 
     * @return {@link CredentialStoreEntriesDto} - the mock DTO
     */
    public static CredentialStoreEntriesDto createMinimumCredentialStoreEntriesDto() {
        CredentialStoreEntriesEntryDto credentialStoreEntriesEntryDto = new CredentialStoreEntriesEntryDto();
        credentialStoreEntriesEntryDto.setName(NAME_1);
        CredentialStoreEntriesSortDto credentialStoreEntiresSortDto = new CredentialStoreEntriesSortDto();
        credentialStoreEntiresSortDto.setSorted(SORTED);
        credentialStoreEntiresSortDto.setUnsorted(UNSORTED);
        credentialStoreEntiresSortDto.setEmpty(EMPTY);

        List<CredentialStoreEntriesEntryDto> entriesList = new ArrayList<>();
        entriesList.add(credentialStoreEntriesEntryDto);

        CredentialStoreEntriesDto credentialStoreEntriesDto = new CredentialStoreEntriesDto();
        credentialStoreEntriesDto.setLast(LAST);
        credentialStoreEntriesDto.setTotalPages(TOTAL_PAGES);
        credentialStoreEntriesDto.setTotalElements(TOTAL_ELEMENTS);
        credentialStoreEntriesDto.setFirst(FIRST);
        credentialStoreEntriesDto.setNumberOfElements(NUMBER_OF_ELEMENTS);
        credentialStoreEntriesDto.setSize(SIZE);
        credentialStoreEntriesDto.setNumber(NUMBER);
        credentialStoreEntriesDto.setEmpty(EMPTY);
        credentialStoreEntriesDto.setContent(entriesList);
        credentialStoreEntriesDto.setSort(credentialStoreEntiresSortDto);

        return credentialStoreEntriesDto;
    }

    /**
     * Method to construct a mock CredentialStoreEntriesEntryDto list
     * 
     * @return {@link CredentialStoreEntriesEntryDto} - the mock DTO list
     */
    public static List<CredentialStoreEntriesEntryDto> createMinimumCredentialStoreEntriesEntryDtoList() {
        CredentialStoreEntriesEntryDto credentialStoreEntriesEntryDto = new CredentialStoreEntriesEntryDto();
        credentialStoreEntriesEntryDto.setName(NAME_2);
        CredentialStoreEntriesSortDto credentialStoreEntiresSortDto = new CredentialStoreEntriesSortDto();
        credentialStoreEntiresSortDto.setSorted(SORTED);
        credentialStoreEntiresSortDto.setUnsorted(UNSORTED);
        credentialStoreEntiresSortDto.setEmpty(EMPTY);

        List<CredentialStoreEntriesEntryDto> entriesList = new ArrayList<>();
        entriesList.add(credentialStoreEntriesEntryDto);

        return entriesList;
    }

    /**
     * Method to construct a mock CredentialStoreEntryDto
     * 
     * @return {@link CredentialStoreEntryDto} - the mock DTO
     */
    public static CredentialStoreEntryDto createMinimumCredentialStoreEntryDto() {
        CredentialStoreEntryDto credentialStoreEntryDto = new CredentialStoreEntryDto();

        credentialStoreEntryDto.setId(ID);
        credentialStoreEntryDto.setName(NAME_2);
        credentialStoreEntryDto.setModifiedAt(MODIFIED_AT);
        credentialStoreEntryDto.setMetadata(METADATA);
        credentialStoreEntryDto.setValue(VALUE);
        credentialStoreEntryDto.setUsername(USERNAME);
        credentialStoreEntryDto.setType(TYPE);

        return credentialStoreEntryDto;
    }

    /**
     * Method to construct a mock NewCredentialStoreEntryDto
     * 
     * @return {@link NewCredentialStoreEntryDto} - the mock DTO
     */
    public static NewCredentialStoreEntryDto createMinimumNewCredentialStoreDto() {
        NewCredentialStoreEntryDto newCredentialStoreEntryDto = new NewCredentialStoreEntryDto();
        newCredentialStoreEntryDto.setMetadata(NEW_METADATA);
        newCredentialStoreEntryDto.setName(NAME_3);
        newCredentialStoreEntryDto.setUsername(NEW_USERNAME);
        newCredentialStoreEntryDto.setValue(NEW_VALUE);

        return newCredentialStoreEntryDto;
    }
}
