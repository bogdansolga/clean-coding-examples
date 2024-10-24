package com.great.project.patientadmin.core.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.great.project.patientadmin.core.application.eventproducer.AugeroPatientDeletedEventProducer;
import com.great.project.patientadmin.core.application.exception.EncounterSubjectModifiedException;
import com.great.project.patientadmin.core.application.exception.GoldenIdentifierNotFoundException;
import com.great.project.patientadmin.core.application.mapper.BasicIdentifierMapper;
import com.great.project.patientadmin.core.application.port.secondary.EventPublisherPort;
import com.great.project.patientadmin.core.application.port.secondary.PatientRepositoryPort;
import com.great.project.patientadmin.core.application.port.secondary.PrimarySystemPort;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.great.project.common.events.domain.patientadmin.eventdata.IdentifierEventDataType;
import com.great.project.core.util.MessageTextProviderUtil;
import com.great.project.patientadmin.core.application.eventproducer.AugeroEncounterCancelledEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroEncounterCreatedEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroEncounterDeletedEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroEncounterReplacedEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroEncounterUpdatedEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroPatientCancelledEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroPatientCreatedEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroPatientReplacedEventProducer;
import com.great.project.patientadmin.core.application.eventproducer.AugeroPatientUpdatedEventProducer;
import com.great.project.patientadmin.core.application.exception.ActivePatientException;
import com.great.project.patientadmin.core.application.exception.EncounterExistsException;
import com.great.project.patientadmin.core.application.exception.EncounterMultipleFoundException;
import com.great.project.patientadmin.core.application.exception.EncounterNotFoundException;
import com.great.project.patientadmin.core.application.exception.EncounterReplacementViolationException;
import com.great.project.patientadmin.core.application.exception.GoldenIdentifierMultipleException;
import com.great.project.patientadmin.core.application.exception.PatientContainsEncountersException;
import com.great.project.patientadmin.core.application.exception.PatientCreateFailException;
import com.great.project.patientadmin.core.application.exception.PatientExistsException;
import com.great.project.patientadmin.core.application.exception.PatientMultipleFoundException;
import com.great.project.patientadmin.core.application.exception.PatientNotFoundException;
import com.great.project.patientadmin.core.application.exception.StatusTypeException;
import com.great.project.patientadmin.core.domain.model.Age;
import com.great.project.patientadmin.core.domain.model.Gender;
import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.encounter.BasicEncounter;
import com.great.project.patientadmin.core.domain.model.encounter.Encounter;
import com.great.project.patientadmin.core.domain.model.encounter.EncounterReplaced;
import com.great.project.patientadmin.core.domain.model.encounter.StatusCoding;
import com.great.project.patientadmin.core.domain.model.humanname.HumanName;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;
import com.great.project.patientadmin.core.domain.model.patient.PatientDataWithEncounters;
import com.great.project.patientadmin.core.domain.model.patient.PatientDataWithReplaceLink;
import com.great.project.patientadmin.core.domain.model.patient.PatientQueryResultData;
import com.great.project.patientadmin.core.domain.model.patient.PatientReplaced;
import com.great.project.patientadmin.core.domain.model.patient.PatientWithEncounters;
import com.great.project.patientadmin.core.domain.model.patient.PatientWithReplaceLink;
import com.great.project.patientadmin.core.domain.model.patient.SelectedEncounter;
import com.great.project.patientadmin.core.domain.query.EncounterQuery;
import com.great.project.patientadmin.core.domain.query.PatientQuery;
import com.great.project.patientadmin.core.domain.query.SubjectAndAccountQuery;
import com.great.project.patientadmin.core.domain.services.AgeCalculator;
import com.great.project.patientadmin.core.domain.services.BusinessConfigUtility;
import com.great.project.patientadmin.core.domain.services.GenderSelector;
import com.great.project.patientadmin.core.domain.services.HumanNameSelector;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Logic implementation of Patient related operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientAdminService {

    private static final String UPDATE = "update";
    private static final String PATIENT_ALREADY_EXISTS = "exception.patient.alreadyExists";
    private static final String MULTIPLE_PATIENTS_FOUND = "exception.patient.event.multipleFound";
    private static final String PATIENT_NOT_FOUND = "exception.patient.notFound";
    private static final String IDENTIFIER_NOT_FOUND = "exception.patient.identifier.notFound";
    private static final String ENCOUNTER_IDENTIFIER_NOT_FOUND = "exception.encounter.identifier.notFound";
    private static final String ENCOUNTER_MULTIPLE_FOUND = "exception.encounter.multipleFound";
    private static final String PATIENT_MULTIPLE_FOUND = "exception.patient.multipleFound";
    private static final String ENCOUNTER_NOT_FOUND = "exception.encounter.notFound";
    private static final String OPERATION_CALL_INVALID = "exception.operation.call.invalid";
    private static final String MSG_TOPIC = "augeroPatientAdminService";
    private static final String PARAMETER_ID = "Id";
    private static final String PARAMETER_RESOURCE_PATIENT = "Patient";
    private static final String PARAMETER_RESOURCE_ENCOUNTER = "Encounter";
    private static final String IDENTIFIER_MULTIPLE_FOUND = "exception.identifier.multipleFound";
    private static final String GOLDEN_IDENTIFIER_NOT_FOUND = "exception.identifier.goldenIdentifier.notFound";
    private static final String ENCOUNTER_SUBJECT_NOT_FOUND = "exception.encounter.subject.notFound";
    private static final String IDENTIFIER_USE_CODE_GOLDEN_IDENTIFIER = "official";
    private static final String PATIENT_CREATE_FAIL = "exception.patient.createFail";
    private static final String CANCELLED = "cancelled";
    private static final String GOLDEN_IDENTIFIER_USE = "official";
    private static final String WRONG_STATUS_TYPE = "exception.resource.status.type.wrongValue";
    private static final String CANCELLED_PATIENT_CANNOT_BE_ACTIVE = "exception.patient.active.wrongValue";
    private static final String MULTIPLE_ENCOUNTERS_FOUND = "exception.encounter.multipleFound";
    private static final String ENCOUNTER_ALREADY_EXISTS = "exception.encounter.alreadyExists";
    private static final String ENCOUNTER_SUBJECT_MODIFIED = "exception.encounter.subject.modified";
    private static final String ENCOUNTER_REPLACEMENT_VIOLATION = "exception.encounter.replacement.violation";
    private static final String PATIENT_CONTAINS_ENCOUNTERS = "exception.patient.containsEncounters";

    private static final Comparator<PatientDataWithEncounters> ENCOUNTER_START_DATE_COMPARATOR = Comparator
            .comparing(p -> p.getEncounterList().stream().map(SelectedEncounter::getStartDate).findFirst()
                    .orElse(LocalDateTime.now()));
    private static final Comparator<PatientDataWithEncounters> PATIENT_NAME_COMPARATOR = Comparator
            .comparing(PatientDataWithEncounters::getName);

    private final PatientRepositoryPort patientRepositoryPort;
    private final AgeCalculator ageCalculator;
    private final MessageTextProviderUtil messageTextProvider;
    private final HumanNameSelector humanNameSelector;
    private final GenderSelector genderSelector;
    private final BusinessConfigUtility businessConfigUtility;
    private final PrimarySystemPort primarySystemPort;
    private final EventPublisherPort eventPublisherPort;

    /**
     * Method implementation to find a patient by a given ID.
     * 
     * @param id
     *            the id of the Patient object
     * @return {@link Patient}
     */
    public Patient readPatientById(UUID id) {
        // First check the correctness of the method call
        String messageOperationCallInvalid = messageTextProvider.getTextFor(MSG_TOPIC, OPERATION_CALL_INVALID,
                PARAMETER_ID);
        Assert.notNull(id, messageOperationCallInvalid);
        Assert.hasText(id.toString(), messageOperationCallInvalid);

        // Next check the existence of the returned patient entity
        Patient patient = patientRepositoryPort.readPatientById(id)
                .orElseThrow(() -> createPatientNotFoundException(PATIENT_NOT_FOUND));

        businessConfigUtility.setContextLanguage();
        ageCalculator.setPatientAge(patient);

        Gender gender = genderSelector.getPatientGender(patient);
        patient.setGender(gender);

        HumanName uniqueHumanName = getHumanName(patient);

        patient.setDisplayName(uniqueHumanName.getText());

        return patient;
    }

    /**
     * Method implementation to save a {@link Patient} object.
     * 
     * @param patient
     *            - the patient to save
     * @return the stored patient from db
     */
    public Patient savePatient(Patient patient) {
        setDisplayableName(patient);
        return patientRepositoryPort.savePatient(patient).stream().peek(ageCalculator::setPatientAge).findFirst()
                .orElseThrow(() -> new PatientCreateFailException(
                        messageTextProvider.getTextFor(MSG_TOPIC, PATIENT_CREATE_FAIL)));
    }

    /**
     * Method implementation to read Patient internal Id by the given identifier parameters. If the identifier is a
     * golden type and the patient is not found internally, an additional search is made by calling the
     * {@link PrimarySystemPort}
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Patient Id found with the given identifier parameters.
     * @throws PatientMultipleFoundException
     *             Multiple patients found
     */
    public UUID readPatientIdByIdentifierWithIngestion(String use, String value, String system) {
        Optional<UUID> optionalId = getPatientList(use, value, system).stream().findFirst();
        if (IDENTIFIER_USE_CODE_GOLDEN_IDENTIFIER.equalsIgnoreCase(use)) {
            return optionalId.orElseGet(() -> ingestAndReturnPatientId(use, value, system));
        } else {
            return optionalId.orElseThrow(() -> createPatientNotFoundException(IDENTIFIER_NOT_FOUND));
        }
    }

    /**
     * Method implementation to read Patient internal Id by the given identifier parameters.
     *
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Patient Id found with the given identifier parameters.
     * @throws PatientMultipleFoundException
     *             Multiple patients found
     */

    public UUID readPatientIdByIdentifier(String use, String value, String system) {
        return getPatientList(use, value, system).stream().findFirst()
                .orElseThrow(() -> createPatientNotFoundException(IDENTIFIER_NOT_FOUND));
    }

    /**
     * Method implementation to save an {@link Encounter} object.
     * 
     * @param encounter
     *            - the encounter to save
     * @return the stored encounter from the database
     */
    public Encounter saveEncounter(Encounter encounter) {
        return Optional.ofNullable(encounter).filter(e -> e.getSubject() != null)
                .map(this::saveEncounterWithReferencedPatient).orElseThrow(() -> new IllegalArgumentException(
                        messageTextProvider.getTextFor(MSG_TOPIC, ENCOUNTER_SUBJECT_NOT_FOUND)));

    }

    /**
     * Method implementation to find the Golden Identifier of a given patient by the internal id.
     * 
     * @param id
     *            the id of the Patient object
     * @return {@link Identifier}
     */
    public Identifier readPatientGoldenIdentifierById(UUID id) {
        String messageNotFound = messageTextProvider.getTextFor(MSG_TOPIC, GOLDEN_IDENTIFIER_NOT_FOUND,
                PARAMETER_RESOURCE_PATIENT);
        String messageMultipleFound = messageTextProvider.getTextFor(MSG_TOPIC, IDENTIFIER_MULTIPLE_FOUND);

        List<Identifier> patientIdentifiers = Optional.ofNullable(readPatientById(id).getIdentifier())
                .orElseThrow(() -> new GoldenIdentifierNotFoundException(messageNotFound));
        List<Identifier> matchingGoldenIdentifiers = patientIdentifiers.stream()
                .filter(identifier -> IDENTIFIER_USE_CODE_GOLDEN_IDENTIFIER.equals(identifier.getUse().getCode()))
                .collect(Collectors.toList());
        if (matchingGoldenIdentifiers.size() > 1) {
            throw new GoldenIdentifierMultipleException(messageMultipleFound);
        }
        return matchingGoldenIdentifiers.stream().findFirst()
                .orElseThrow(() -> new GoldenIdentifierNotFoundException(messageNotFound));
    }

    /**
     * Method implementation to read Encounter internal id by the given identifier values.
     * 
     * @param use
     *            - the use of the Identifier
     * @param value
     *            - the value of the Identifier
     * @param system
     *            - the system of the Identifier
     * @return {@link UUID} - the internal Encounter Id having the given identifier parameters.
     * @throws EncounterMultipleFoundException
     *             Multiple encounters found with given identifiers
     */
    public UUID readEncounterIdByIdentifier(String use, String value, String system) {
        return Optional.ofNullable(getEncounterList(use, value, system)).filter(list -> list.size() <= 1)
                .map(idList -> idList.stream().findFirst().get())
                .orElseThrow(() -> createEncounterMultipleFoundException(MSG_TOPIC, ENCOUNTER_MULTIPLE_FOUND));
    }

    /**
     * Method implementation to find an encounter by a given encounter ID.
     * 
     * @param id
     *            the id of the Encounter object
     * @return {@link Encounter}
     * @throws EncounterNotFoundException
     *             No encounter found with given id
     */
    public Encounter readEncounterById(UUID id) {
        String messageOperationCallInvalid = messageTextProvider.getTextFor(MSG_TOPIC, OPERATION_CALL_INVALID,
                PARAMETER_ID);
        Assert.notNull(id, messageOperationCallInvalid);
        Assert.hasText(id.toString(), messageOperationCallInvalid);

        return patientRepositoryPort.readEncounterById(id)
                .orElseThrow(() -> createEncounterNotFoundException(MSG_TOPIC, ENCOUNTER_NOT_FOUND));
    }

    /**
     * Method implementation to find the Golden Identifier of a given Encounter by the internal id.
     * 
     * @param id
     *            the id of the Encounter object
     * @return {@link Identifier}
     */
    public Identifier readEncounterGoldenIdentifierById(UUID id) {
        String messageNotFound = messageTextProvider.getTextFor(MSG_TOPIC, GOLDEN_IDENTIFIER_NOT_FOUND,
                PARAMETER_RESOURCE_ENCOUNTER);
        String messageMultipleFound = messageTextProvider.getTextFor(MSG_TOPIC, IDENTIFIER_MULTIPLE_FOUND);

        List<Identifier> encounterIdentifiers = Optional.ofNullable(readEncounterById(id).getIdentifier())
                .orElseThrow(() -> new GoldenIdentifierNotFoundException(messageNotFound));
        List<Identifier> matchingGoldenIdentifiers = encounterIdentifiers.stream()
                .filter(identifier -> IDENTIFIER_USE_CODE_GOLDEN_IDENTIFIER.equals(identifier.getUse().getCode()))
                .collect(Collectors.toList());
        if (matchingGoldenIdentifiers.size() > 1) {
            throw new GoldenIdentifierMultipleException(messageMultipleFound);
        }
        return matchingGoldenIdentifiers.stream().findFirst()
                .orElseThrow(() -> new GoldenIdentifierNotFoundException(messageNotFound));
    }

    /**
     * Method implementation for creating a Patient.
     *
     * @param patient
     *            the processed patient
     */
    public void createPatient(Patient patient) {
        try {
            var savedPatient = savePatient(patient);
            publishPatientCreatedEvent(savedPatient);
        } catch (DataIntegrityViolationException e) {
            throw new PatientExistsException(messageTextProvider.getTextFor(MSG_TOPIC, PATIENT_ALREADY_EXISTS));
        }
    }

    /**
     * Method implementation for updating a Patient.
     *
     * @param patientUpdated
     *            the processed patient
     */
    public void updatePatient(Patient patientUpdated) {

        Optional<Identifier> goldenIdentifier = getPatientGoldenIdentifier(patientUpdated);

        goldenIdentifier.ifPresent(identifier -> {
            try {
                var patient = getPatientByIdentifier(identifier);
                updateExistingPatient(patientUpdated, patient);
                savePatient(patient);
                publishPatientUpdatedEvent(patient);
            } catch (PatientNotFoundException e) {
                savePatient(patientUpdated);
            } catch (PatientMultipleFoundException e) {
                throw createPatientMultipleFoundException(MULTIPLE_PATIENTS_FOUND, UPDATE);
            }
        });
    }

    /**
     * Method implementation for cancelling a Patient.
     *
     * @param patient
     *            the processed patient
     */
    public void cancelPatient(Patient patient) {
        checkIfPatientIsActive(patient);

        Optional<Identifier> goldenIdentifier = patient.getIdentifier().stream()
                .filter(identifier -> GOLDEN_IDENTIFIER_USE.equals(identifier.getUse().getCode())).findFirst();

        goldenIdentifier.ifPresent(identifier -> {
            patient.setId(readPatientIdByIdentifier(identifier.getUse().getCode(), identifier.getValue(),
                    identifier.getSystem()));
            markPatientAsCancelled(patient);
            savePatient(patient);
            publishPatientCancelledEvent(patient);
        });
    }

    private void checkIfPatientIsActive(Patient patient) {

        if (patientIsActive(patient)) {
            throw new ActivePatientException(
                    messageTextProvider.getTextFor(MSG_TOPIC, CANCELLED_PATIENT_CANNOT_BE_ACTIVE));
        }
    }

    /**
     * Method implementation for creating an Encounter.
     *
     * @param encounter
     *            the processed encounter
     * @param subjectIdentifiers
     *            list of identifiers belonging to the subject
     */
    public void createEncounter(Encounter encounter, List<IdentifierEventDataType> subjectIdentifiers) {
        checkStatusIsNotCancelled(encounter);
        verifyEncounterDoesNotExist(encounter);
        setPatientIdInEncounterSubjectWithIngestion(subjectIdentifiers, encounter);
        try {
            var savedEncounter = saveEncounter(encounter);
            publishEncounterCreatedEvent(savedEncounter);
        } catch (DataIntegrityViolationException e) {
            throw new EncounterExistsException(messageTextProvider.getTextFor(MSG_TOPIC, ENCOUNTER_ALREADY_EXISTS));
        }

    }

    /**
     * Method implementation for updating an Encounter.
     *
     * @param encounterUpdated
     *            the processed encounter
     * @param subjectIdentifiers
     *            list of identifiers belonging to the subject
     */
    public void updateEncounter(Encounter encounterUpdated, List<IdentifierEventDataType> subjectIdentifiers) {
        Optional<Identifier> goldenIdentifier = getEncounterGoldenIdentifier(encounterUpdated);

        goldenIdentifier.ifPresent(identifier -> {
            setPatientIdInEncounterSubject(subjectIdentifiers, encounterUpdated);
            try {
                var encounter = getEncounterByIdentifier(identifier);
                checkCancelledStatusIsNotModified(encounter, encounterUpdated);
                checkSubjectIsNotModified(encounter, encounterUpdated);
                BeanUtils.copyProperties(encounterUpdated, encounter, "id", "replaces");
                saveEncounter(encounter);
                publishEncounterUpdatedEvent(encounter);
            } catch (EncounterNotFoundException e) {
                checkStatusIsNotCancelled(encounterUpdated);
                saveEncounter(encounterUpdated);
            } catch (EncounterMultipleFoundException e) {
                throw createEncounterMultipleFoundException(MSG_TOPIC, MULTIPLE_ENCOUNTERS_FOUND);
            }
        });
    }

    /**
     * Method implementation for creating a Cancelled Encounter.
     * 
     * @param encounterCancelled
     *            the processed encounter
     * @param subjectIdentifiers
     *            list of identifiers belonging to the subject
     */
    public void createCancelledEncounter(Encounter encounterCancelled,
            List<IdentifierEventDataType> subjectIdentifiers) {
        checkForEncounterType(encounterCancelled);
        Optional<Identifier> goldenIdentifier = getEncounterGoldenIdentifier(encounterCancelled);

        goldenIdentifier.ifPresent(identifier -> {
            setPatientIdInEncounterSubject(subjectIdentifiers, encounterCancelled);
            try {
                var encounter = getEncounterByIdentifier(identifier);
                checkSubjectIsNotModified(encounter, encounterCancelled);
                BeanUtils.copyProperties(encounterCancelled, encounter, "id", "replaces");
                saveEncounter(encounter);
                publishEncounterCancelledEvent(encounter);
            } catch (EncounterNotFoundException e) {
                throw createEncounterNotFoundException(MSG_TOPIC, ENCOUNTER_NOT_FOUND);
            } catch (EncounterMultipleFoundException e) {
                throw new EncounterMultipleFoundException(
                        messageTextProvider.getTextFor(MSG_TOPIC, MULTIPLE_ENCOUNTERS_FOUND));
            }
        });

    }

    /**
     * Method implementation to return a list of BasicEncounters based on given parameters.
     *
     * @param encounterQuery
     *            - contains all the parameters needed for the query
     * @return a list of {@link BasicEncounter}
     */
    public List<BasicEncounter> findByOrgAssignmentAndTimePeriod(EncounterQuery encounterQuery) {
        return patientRepositoryPort.findByOrgAssignmentAndTimePeriod(encounterQuery);
    }

    /**
     * Return a list of PatientWithEncounters based on the given query criteria
     *
     * @param query
     *            - contains all the parameters needed for the query
     * @return {@link List} of {@link PatientDataWithEncounters}
     */
    public List<PatientDataWithEncounters> findByOrgPatientAndAccountIdentifier(SubjectAndAccountQuery query) {
        return patientRepositoryPort.findByOrgPatientAndAccountIdentifier(query).stream()
                .peek(this::setAgeOnPatientWithEncounters).peek(this::setGenderOnPatientWithEncounters)
                .map(this::mapToPatientDataWithEncounters).collect(Collectors.toList());
    }

    /**
     * Return a PatientQueryResultData based on the given query criteria
     *
     * @param patientQuery
     *            - contains all the parameters needed for the query
     * @param offset
     *            - the starting position for the dataset
     * @param pageSize
     *            - the size of the page for the dataset
     * @return {@link PatientQueryResultData}
     */
    public List<PatientDataWithReplaceLink> findPatientData(PatientQuery patientQuery, Integer offset,
            Integer pageSize) {
        return patientRepositoryPort.findPatientData(patientQuery, offset, pageSize).stream()
                .peek(this::setAgeOnPatientWithEncounters).peek(this::setGenderOnPatientWithEncounters)
                .map(this::mapToPatientDataWithReplaceLink)
                .sorted(ENCOUNTER_START_DATE_COMPARATOR.reversed().thenComparing(PATIENT_NAME_COMPARATOR))
                .collect(Collectors.toList());

    }

    public void checkForEncounterType(Encounter encounterCancelled) {
        if (!encounterCancelled.getStatus().getCode().contentEquals(CANCELLED)) {
            throw new StatusTypeException(messageTextProvider.getTextFor(MSG_TOPIC, WRONG_STATUS_TYPE));
        }
    }

    public void checkSubjectIsNotModified(Encounter encounter, Encounter updatedEncounter) {
        if (!encounter.getSubject().equals(updatedEncounter.getSubject())) {
            throw new EncounterSubjectModifiedException(
                    messageTextProvider.getTextFor(MSG_TOPIC, ENCOUNTER_SUBJECT_MODIFIED));
        }
    }

    /**
     * Method implementation to replace a {@link Encounter} object with a replacing object.
     *
     * @param replacedEncounter
     *            - the source encounter to be replaced
     * @param replacingEncounter
     *            - the target encounter to replace the source encounter
     */
    public void replaceEncounter(Encounter replacedEncounter, Encounter replacingEncounter) {
        List<UUID> replacedEncounterIds = findEncounterIds(replacedEncounter);
        List<UUID> replacingEncounterIds = findEncounterIds(replacingEncounter);
        validateEncounterReplacementConditions(replacedEncounterIds, replacingEncounterIds);
        Optional<EncounterReplaced> encounterReplacedOptional = patientRepositoryPort
                .replaceEncounter(replacedEncounter, replacingEncounter);

        encounterReplacedOptional.ifPresent(this::publishEncounterReplacedEvent);
    }

    /**
     * Method implementation to delete an encounter by golden identifier.
     * 
     * @param use
     *            - the use of the identifier
     * @param value
     *            - the value of the identifier
     * @param system
     *            - the system of the identifier
     */
    public void deleteEncounterByIdentifier(String use, String value, String system) {
        var encounterId = readEncounterIdByIdentifier(use, value, system);
        var encounterToBeDeleted = readEncounterById(encounterId);

        patientRepositoryPort.deleteEncounterById(encounterId);
        publishEncounterDeletedEvent(encounterToBeDeleted);
    }

    /**
     * Method implementation to delete a patient by golden identifier.
     * 
     * @param use
     *            - the use of the identifier
     * @param value
     *            - the value of the identifier
     * @param system
     *            - the system of the identifier
     */
    public void deletePatientByIdentifier(String use, String value, String system) {
        var patientId = readPatientIdByIdentifier(use, value, system);
        var patientToBeDeleted = readPatientById(patientId);

        if (!patientRepositoryPort.deletePatientById(patientId)) {
            throw createPatientContainsEncountersException(MSG_TOPIC, PATIENT_CONTAINS_ENCOUNTERS);
        }

        publishPatientDeletedEvent(patientToBeDeleted);
    }

    private void validateEncounterReplacementConditions(List<UUID> replacedEncounterIds,
            List<UUID> replacingEncounterIds) {
        if ((CollectionUtils.isEmpty(replacedEncounterIds) && !CollectionUtils.isEmpty(replacingEncounterIds))
                || (!CollectionUtils.isEmpty(replacedEncounterIds)
                        && !CollectionUtils.isEmpty(replacingEncounterIds))) {
            throw new EncounterReplacementViolationException(
                    messageTextProvider.getTextFor(MSG_TOPIC, ENCOUNTER_REPLACEMENT_VIOLATION));
        }
    }

    private List<UUID> findEncounterIds(Encounter encounter) {
        var identifier = encounter.getIdentifier();
        if (CollectionUtils.isEmpty(identifier)) {
            return new ArrayList<>();
        }
        var firstIdentifier = identifier.get(0);
        return patientRepositoryPort.readEncounterIdByIdentifier(firstIdentifier.getUse().getCode(),
                firstIdentifier.getValue(), firstIdentifier.getSystem());
    }

    private void publishEncounterCreatedEvent(Encounter encounter) {
        eventPublisherPort.publishEvent(AugeroEncounterCreatedEventProducer.produce(encounter));
    }

    private void publishEncounterUpdatedEvent(Encounter encounter) {
        eventPublisherPort.publishEvent(AugeroEncounterUpdatedEventProducer.produce(encounter));
    }

    private void publishEncounterCancelledEvent(Encounter encounter) {
        eventPublisherPort.publishEvent(AugeroEncounterCancelledEventProducer.produce(encounter));
    }

    private void publishEncounterReplacedEvent(EncounterReplaced encounterReplaced) {
        eventPublisherPort.publishEvent(AugeroEncounterReplacedEventProducer.produce(encounterReplaced));
    }

    private void publishEncounterDeletedEvent(Encounter encounter) {
        eventPublisherPort.publishEvent(AugeroEncounterDeletedEventProducer.produce(encounter));
    }

    private void publishPatientCancelledEvent(Patient patient) {
        eventPublisherPort.publishEvent(AugeroPatientCancelledEventProducer.produce(patient));
    }

    private void publishPatientUpdatedEvent(Patient patient) {
        eventPublisherPort.publishEvent(AugeroPatientUpdatedEventProducer.produce(patient));
    }

    private void publishPatientCreatedEvent(Patient patient) {
        eventPublisherPort.publishEvent(AugeroPatientCreatedEventProducer.produce(patient));
    }

    private void publishPatientDeletedEvent(Patient patient) {
        eventPublisherPort.publishEvent(AugeroPatientDeletedEventProducer.produce(patient));
    }

    private HumanName getHumanName(Patient patient) {
        return patient.getName().stream().filter(HumanName::isDisplayed).findFirst().get();
    }

    private Patient getPatientByIdentifier(Identifier identifier) {
        UUID id = readPatientIdByIdentifier(identifier.getUse().getCode(), identifier.getValue(),
                identifier.getSystem());
        return readPatientById(id);
    }

    private Encounter getEncounterByIdentifier(Identifier identifier) {
        UUID id = readEncounterIdByIdentifier(identifier.getUse().getCode(), identifier.getValue(),
                identifier.getSystem());
        return readEncounterById(id);
    }

    private void checkStatusIsNotCancelled(Encounter encounter) {
        if (CANCELLED.equals(encounter.getStatus().getCode())) {
            throw new StatusTypeException(messageTextProvider.getTextFor(MSG_TOPIC, WRONG_STATUS_TYPE));
        }
    }

    private void checkCancelledStatusIsNotModified(Encounter encounter, Encounter encounterUpdated) {
        String existingStatus = encounter.getStatus().getCode();
        String newStatus = encounterUpdated.getStatus().getCode();

        if ((CANCELLED.equals(existingStatus) && !CANCELLED.equals(newStatus))
                || (!CANCELLED.equals(existingStatus) && CANCELLED.equals(newStatus))) {
            throw new StatusTypeException(messageTextProvider.getTextFor(MSG_TOPIC, WRONG_STATUS_TYPE));
        }
    }

    private void verifyEncounterDoesNotExist(Encounter encounter) {
        getEncounterGoldenIdentifier(encounter).ifPresent(identifier -> {
            if (checkEncounterExists(identifier)) {
                throw new EncounterExistsException(messageTextProvider.getTextFor(MSG_TOPIC, ENCOUNTER_ALREADY_EXISTS));
            }
        });
    }

    private boolean checkEncounterExists(Identifier identifier) {
        try {
            readEncounterIdByIdentifier(identifier.getUse().getCode(), identifier.getValue(), identifier.getSystem());
            return true;
        } catch (EncounterNotFoundException | EncounterMultipleFoundException e) {
            return false;
        }
    }

    private Optional<Identifier> getEncounterGoldenIdentifier(Encounter encounter) {
        return encounter.getIdentifier().stream()
                .filter(identifier -> GOLDEN_IDENTIFIER_USE.equals(identifier.getUse().getCode())).findFirst();
    }

    private Optional<Identifier> getPatientGoldenIdentifier(Patient patient) {
        return patient.getIdentifier().stream()
                .filter(identifier -> GOLDEN_IDENTIFIER_USE.equals(identifier.getUse().getCode())).findFirst();
    }

    private void markPatientAsCancelled(Patient patient) {
        StatusCoding status = new StatusCoding();
        status.setCode(CANCELLED);
        patient.setStatus(status);
    }

    private boolean patientIsActive(Patient patientCancelled) {
        return patientCancelled.getActive();
    }

    private void setPatientIdInEncounterSubject(List<IdentifierEventDataType> patientEventDataIdentifiers,
            Encounter encounter) {
        patientEventDataIdentifiers.forEach(patientIdentifier -> {
            UUID patientId = readPatientIdByIdentifier(patientIdentifier.getUse().getCode(),
                    patientIdentifier.getValue(), patientIdentifier.getSystem());

            encounter.setSubject(patientId);
        });
    }

    private void setPatientIdInEncounterSubjectWithIngestion(List<IdentifierEventDataType> patientEventDataIdentifiers,
            Encounter encounter) {
        patientEventDataIdentifiers.forEach(patientIdentifier -> {
            UUID patientId = readPatientIdByIdentifierWithIngestion(patientIdentifier.getUse().getCode(),
                    patientIdentifier.getValue(), patientIdentifier.getSystem());

            encounter.setSubject(patientId);
        });
    }

    private Encounter saveEncounterWithReferencedPatient(Encounter encounter) {
        return patientRepositoryPort.readPatientById(encounter.getSubject())
                .map(patient -> patientRepositoryPort.saveEncounter(encounter))
                .orElseThrow(() -> createPatientNotFoundException(PATIENT_NOT_FOUND));
    }

    private List<UUID> getEncounterList(String use, String value, String system) {
        return Optional.ofNullable(patientRepositoryPort.readEncounterIdByIdentifier(use, value, system))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> createEncounterNotFoundException(MSG_TOPIC, ENCOUNTER_IDENTIFIER_NOT_FOUND));
    }

    private List<UUID> getPatientList(String use, String value, String system) {
        var patientIds = patientRepositoryPort.readPatientIdByIdentifier(use, value, system);
        if (patientIds.size() > 1) {
            throw createPatientMultipleFoundException(MSG_TOPIC, PATIENT_MULTIPLE_FOUND);
        }
        return patientIds;
    }

    private UUID ingestAndReturnPatientId(String use, String value, String system) {
        return primarySystemPort.findPatientByGoldenIdentifier(use, value, system).map(this::savePatient)
                .map(Patient::getId).orElseThrow(() -> createPatientNotFoundException(IDENTIFIER_NOT_FOUND));
    }

    private void setAgeOnPatientWithEncounters(PatientWithEncounters patientWithEncounters) {
        businessConfigUtility.setContextLanguage();
        ageCalculator.setPatientAge(patientWithEncounters.getPatient());
    }

    private void setGenderOnPatientWithEncounters(PatientWithEncounters patientWithEncounters) {
        var gender = genderSelector.getPatientGender(patientWithEncounters.getPatient());
        patientWithEncounters.getPatient().setGender(gender);
    }

    private PatientDataWithEncounters mapToPatientDataWithEncounters(PatientWithEncounters patientWithEncounters) {
        var patient = patientWithEncounters.getPatient();
        var patientGoldenIdentifier = patient.getIdentifier().stream()
                                             .filter(identifier -> identifier.getUse().getCode().equalsIgnoreCase(GOLDEN_IDENTIFIER_USE)).findAny()
                                             .map(BasicIdentifierMapper.INSTANCE::mapToBasicIdentifier).orElseThrow();
        var age = Optional.ofNullable(patient.getAge()).map(Age::toString).orElse(null);
        var gender = Optional.ofNullable(patient.getGender()).map(Gender::toString).orElse(null);
        return PatientDataWithEncounters.builder().id(patient.getId()).name(getHumanName(patient).getText()).age(age)
                .active(patient.getActive()).birthDate(patient.getBirthDate())
                .birthDateAbsent(patient.getBirthDateAbsent()).birthDateUnknown(patient.getBirthDateUnknown())
                .deceased(patient.getDeceasedBoolean()).genderDisplayText(gender)
                .inexactDateOfBirth(patient.getInexactDoB()).status(patient.getStatus())
                .goldenIdentifier(patientGoldenIdentifier).encounterList(patientWithEncounters.getEncounterList())
                .build();
    }

    private PatientDataWithReplaceLink mapToPatientDataWithReplaceLink(PatientWithReplaceLink patientWithReplaceLink) {
        var patientDataWithEncounters = mapToPatientDataWithEncounters(patientWithReplaceLink);
        return PatientDataWithReplaceLink.buildFrom(patientDataWithEncounters,
                patientWithReplaceLink.getReplacedPatientIds(), patientWithReplaceLink.getReplacedPatientNames());
    }

    private PatientNotFoundException createPatientNotFoundException(String key) {
        var message = messageTextProvider.getTextFor(MSG_TOPIC, key);
        log.error(message);
        return new PatientNotFoundException(message);
    }

    private EncounterNotFoundException createEncounterNotFoundException(String topic, String key) {
        return new EncounterNotFoundException(messageTextProvider.getTextFor(topic, key));
    }

    private PatientMultipleFoundException createPatientMultipleFoundException(String topic, String key) {
        return new PatientMultipleFoundException(messageTextProvider.getTextFor(topic, key));
    }

    private EncounterMultipleFoundException createEncounterMultipleFoundException(String topic, String key) {
        return new EncounterMultipleFoundException(messageTextProvider.getTextFor(topic, key));
    }

    private PatientContainsEncountersException createPatientContainsEncountersException(String topic, String key) {
        return new PatientContainsEncountersException(messageTextProvider.getTextFor(topic, key));
    }

    /**
     * Method implementation to replace a {@link Patient} object with a replacing object.
     *
     * @param replacedPatient
     *            - the source patient to be replaced
     * @param replacingPatient
     *            - the target patient to replace the source patient
     */
    public void mergePatient(Patient replacedPatient, Patient replacingPatient) {

        checkIfPatientIsActive(replacedPatient);

        markPatientAsCancelled(replacedPatient);

        replacedPatient.setActive(false);

        setDisplayableName(replacedPatient);
        setDisplayableName(replacingPatient);

        Optional<PatientReplaced> patientReplaced = patientRepositoryPort.replacePatient(replacedPatient,
                replacingPatient);
        patientReplaced.ifPresent(this::publishPatientReplacedEvent);
    }

    private void publishPatientReplacedEvent(PatientReplaced patientReplaced) {
        eventPublisherPort.publishEvent(AugeroPatientReplacedEventProducer.produce(patientReplaced));
    }

    private void setDisplayableName(Patient patient) {
        HumanName chosenName = humanNameSelector.getUniquePatientName(patient);
        if (chosenName != null) {
            chosenName.setDisplayed(true);
        }

    }

    private void updateExistingPatient(Patient patientUpdated, Patient existingPatient) {
        if (isPatientCancelled(existingPatient)) {
            BeanUtils.copyProperties(patientUpdated, existingPatient, "status", "id");
        } else {
            BeanUtils.copyProperties(patientUpdated, existingPatient, "id");
        }
    }

    private boolean isPatientCancelled(Patient patient) {
        return null != patient.getStatus() && CANCELLED.equals(patient.getStatus().getCode());
    }
}
