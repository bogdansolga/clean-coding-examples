package com.great.project.patientadmin.core.application.port.secondary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.encounter.BasicEncounter;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.encounter.EncounterReplaced;
import com.great.project.patientadmin.core.domain.model.patient.PatientReplaced;
import com.great.project.patientadmin.core.domain.model.patient.PatientWithEncounters;
import com.great.project.patientadmin.core.domain.model.patient.PatientWithReplaceLink;
import com.great.project.patientadmin.core.domain.query.EncounterQuery;
import com.great.project.patientadmin.core.domain.query.PatientQuery;
import com.great.project.patientadmin.core.domain.query.SubjectAndAccountQuery;

/**
 * Secondary port that specifies operations with PatientEntity - the business object.
 */
public interface PatientRepositoryPort {

    /**
     * Returns an {@link Optional} of {@link Patient} given the patient id.
     * 
     * @param id
     *            - the id of the patient
     * @return returns an {@link Optional} of {@link Patient} if a patient with the given id exists, or an empty
     *         {@link Optional} if no patient with that id exists.
     */
    Optional<Patient> readPatientById(UUID id);

    /**
     * Saves a {@link Patient} object.
     * 
     * @param patient
     *            - the patient to save
     * @return {@link Optional} of the stored {@link Patient} from db
     */
    Optional<Patient> savePatient(Patient patient);

    /**
     * Read Patient internal id(s) in the repository by the given identifier values.
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link List} of {@link UUID} containing the internal Patient id(s) having the given identifier
     *         parameters.
     */
    List<UUID> readPatientIdByIdentifier(String use, String value, String system);

    /**
     * Saves a {@link Encounter} object.
     * 
     * @param encounter
     *            - the encounter to save
     * @return the stored encounter from db
     */
    Encounter saveEncounter(Encounter encounter);

    /**
     * Read Encounter Id(s) in the repository by the given identifier use, value and system.
     * 
     * @param use
     *            - the use of the external identifier.
     * @param value
     *            - the value of the external identifier
     * @param system
     *            - the system of the external identifier
     * @return a {@link UUID} - the internal Id of the Encounter found with the given identifier parameters.
     */

    List<UUID> readEncounterIdByIdentifier(String use, String value, String system);

    /**
     * Find an encounter data in the repository by a given id.
     * 
     * @param id
     *            the id of the EncounterEntity object
     * @return returns an {@link Optional} of {@link Encounter} if an encounter data with the given id exists, or it
     *         throws an exception if no encounter data with that id exists.
     */
    Optional<Encounter> readEncounterById(UUID id);

    /**
     * Return a list of BasicEncounters based on given parameters.
     * 
     * @param encounterQuery
     *            - contains all the parameters needed for the query
     * @return a list of {@link BasicEncounter}
     */
    List<BasicEncounter> findByOrgAssignmentAndTimePeriod(EncounterQuery encounterQuery);

    /**
     * Return a list of PatientWithEncounters based on the given query criteria
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @return {@link List} of {@link PatientWithEncounters}
     */
    List<PatientWithEncounters> findByOrgPatientAndAccountIdentifier(SubjectAndAccountQuery query);

    /**
     * Returns a list of {@link PatientWithReplaceLink} based on the given query.
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @param offset
     *            - the starting position for the dataset
     * @param pageSize
     *            - the size of the page for the dataset
     * @return a list of {@link PatientWithReplaceLink}
     */
    List<PatientWithReplaceLink> findPatientData(PatientQuery query, Integer offset, Integer pageSize);

    /**
     * Replace patient with another patient
     *
     * @param replacedPatient
     *            - the source patient
     * @param replacingPatient
     *            - the target patient
     * @return the affected patient from db
     */
    Optional<PatientReplaced> replacePatient(Patient replacedPatient, Patient replacingPatient);

    /**
     * Replace encounter with another encounter
     *
     * @param replacedEncounter
     *            - the source encounter
     * @param replacingEncounter
     *            - the target encounter
     * @return the affected encounter from db
     */
    Optional<EncounterReplaced> replaceEncounter(Encounter replacedEncounter, Encounter replacingEncounter);

    /**
     * Deletes an encounter by its id
     * 
     * @param encounterId
     *            - the id of the encounter to be deleted
     */
    void deleteEncounterById(UUID encounterId);

    /**
     * Deletes a patient by its id if patient has no encounters
     * 
     * @param patientId
     *            - the id of the patient to be deleted
     */
    boolean deletePatientById(UUID patientId);

}
