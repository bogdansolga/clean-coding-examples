package com.great.project.patientadmin.core.application.port.primary;

import java.util.List;
import java.util.UUID;

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
 * Primary port - the main API of the application. This is called by the primary adapters that allow you to change
 * objects, attributes, and relations in the core logic.
 */
public interface PatientAdminPort {
    /**
     * Returns a patient given the logical id.
     * 
     * @param id
     *            of patient
     * @return a {@link Patient} corresponding to the patient with the given patient id, as represented through the
     *         information model of the patient.
     */
    Patient readPatientById(UUID id);

    /**
     * Read Patient internal Id by the given identifier parameters.
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Patient Id found with the given identifier parameters.
     */
    UUID readPatientIdByIdentifier(String use, String value, String system);

    /**
     * Returns the Golden Identifier of a patient given the logical id.
     * 
     * @param id
     *            of patient
     * @return the Golden {@link Identifier} corresponding to the patient with the given patient id, as represented
     *         through the information model of the patient.
     */
    Identifier readPatientGoldenIdentifierById(UUID id);

    /**
     * Read Encounter internal id by the given identifier parameters.
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Encounter Id having the given identifier parameters.
     */
    UUID readEncounterIdByIdentifier(String use, String value, String system);

    /**
     * Returns an encounter.
     * 
     * @param id
     *            of encounter
     * @return a {@link Encounter} corresponding to encounter data with the given encounter id.
     */
    Encounter readEncounterById(UUID id);

    /**
     * Returns the Golden Identifier of an Encounter given the logical id.
     * 
     * @param id
     *            of Encounter
     * @return the Golden {@link Identifier} corresponding to the Encounter with the given Encounter id, as represented
     *         through the information model of the Encounter.
     */
    Identifier readEncounterGoldenIdentifierById(UUID id);

    /**
     * Returns a list of BasicEncounters based on given parameters.
     * 
     * @param encounterQuery
     *            - contains all the parameters needed for the query
     * @return a list of {@link BasicEncounter}
     */
    List<BasicEncounter> findByOrgAssignmentAndTimePeriod(EncounterQuery encounterQuery);

    /**
     * Returns a list of {@link PatientDataWithEncounters} based on the given query.
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @return a {@link List} of {@link PatientDataWithEncounters}
     */
    List<PatientDataWithEncounters> findByOrgPatientAndAccountIdentifier(SubjectAndAccountQuery query);

    /**
     * Returns {@link PatientQueryResultData} based on the given query.
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @param pageSize
     * @return {@link PatientQueryResultData}
     */
    List<PatientDataWithReplaceLink> findPatientData(PatientQuery query, Integer offset, Integer pageSize);
}
