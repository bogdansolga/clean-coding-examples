package com.great.project.patientadmin.core.application.port.primary.impl;

import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.application.PatientAdminService;
import com.great.project.patientadmin.core.application.port.primary.PatientAdminPort;
import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.encounter.BasicEncounter;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;
import com.great.project.patientadmin.core.domain.model.patient.PatientDataWithEncounters;
import com.great.project.patientadmin.core.domain.model.patient.PatientDataWithReplaceLink;
import com.great.project.patientadmin.core.domain.model.patient.PatientQueryResultData;
import com.great.project.patientadmin.core.domain.query.EncounterQuery;
import com.great.project.patientadmin.core.domain.query.PatientQuery;
import com.great.project.patientadmin.core.domain.query.SubjectAndAccountQuery;

/**
 * Implementation of {@link PatientAdminPort}
 */
public class PatientAdminPortImpl implements PatientAdminPort {

    private final PatientAdminService patientAdminService;

    public PatientAdminPortImpl(PatientAdminService patientAdminService) {
        this.patientAdminService = patientAdminService;
    }

    /**
     * Returns a patient given the logical id.
     * 
     * @param id
     *            of patient
     * @return a {@link Patient} corresponding to the patient with the given patient id, as represented through the
     *         information model of the patient.
     */
    @Override
    public Patient readPatientById(UUID id) {
        // Return the possible patient object found and let exception be propagated to the primary adapter caller
        return patientAdminService.readPatientById(id);
    }

    /**
     * Returns the Patient internal Id found with the given the identifier parameters.
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Patient Id found with the given identifier parameters.
     */
    @Override
    public UUID readPatientIdByIdentifier(String use, String value, String system) {
        return patientAdminService.readPatientIdByIdentifierWithIngestion(use, value, system);
    }

    /**
     * Returns the Golden Identifier of a patient given the logical id.
     * 
     * @param id
     *            of patient
     * @return the Golden {@link Identifier} corresponding to the patient with the given patient id, as represented
     *         through the information model of the patient.
     */
    @Override
    public Identifier readPatientGoldenIdentifierById(UUID id) {
        return patientAdminService.readPatientGoldenIdentifierById(id);
    }

    /**
     * Returns the internal Encounter id given the identifier parameters.
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Encounter Id having the given identifier parameters.
     */
    @Override
    public UUID readEncounterIdByIdentifier(String use, String value, String system) {
        return patientAdminService.readEncounterIdByIdentifier(use, value, system);
    }

    /**
     * Returns an encounter data with the given id.
     * 
     * @param id
     *            of encounter
     * @return a {@link Encounter} corresponding to encounter data with the given encounter id.
     */
    @Override
    public Encounter readEncounterById(UUID id) {
        return patientAdminService.readEncounterById(id);
    }

    /**
     * Returns the Golden Identifier of an Encounter given the logical id.
     * 
     * @param id
     *            of Encounter
     * @return the Golden {@link Identifier} corresponding to the Encounter with the given Encounter id, as represented
     *         through the information model of the Encounter.
     */
    @Override
    public Identifier readEncounterGoldenIdentifierById(UUID id) {
        return patientAdminService.readEncounterGoldenIdentifierById(id);
    }

    /**
     * Returns a list of BasicEncounters based on given parameters.
     * 
     * @param encounterQuery
     *            - contains all the parameters needed for the query
     * @return a list of {@link BasicEncounter}
     */
    @Override
    public List<BasicEncounter> findByOrgAssignmentAndTimePeriod(EncounterQuery encounterQuery) {
        return patientAdminService.findByOrgAssignmentAndTimePeriod(encounterQuery);
    }

    /**
     * Returns a list of PatientDataWithEncounters based on the given query
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @return {@link List} of {@link PatientDataWithEncounters}
     */
    @Override
    public List<PatientDataWithEncounters> findByOrgPatientAndAccountIdentifier(SubjectAndAccountQuery query) {
        return patientAdminService.findByOrgPatientAndAccountIdentifier(query);
    }

    /**
     * Returns a list of PatientDataWithReplaced based on the given query
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @param pageSize
     * @return {@link PatientQueryResultData}
     */
    @Override
    public List<PatientDataWithReplaceLink> findPatientData(PatientQuery query, Integer offset, Integer pageSize) {
        return patientAdminService.findPatientData(query, offset, pageSize);
    }

}
