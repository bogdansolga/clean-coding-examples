package com.great.project.patientadmin.core.application.eventhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.great.project.common.events.domain.patientadmin.eventdata.CodeableConceptEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.CommunicationEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.ContactEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.ContactPointEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.HumanNameEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.IdentifierEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.PatientECCEventDataV1;
import com.great.project.common.events.domain.patientadmin.eventdata.PersonBirthDateUnknownEventDataType;
import com.great.project.common.events.domain.patientadmin.eventdata.PersonEventData;
import com.great.project.common.events.domain.patientadmin.eventdata.ReferenceEventDataType;
import com.great.project.patientadmin.core.domain.model.BirthDateAbsentCoding;
import com.great.project.patientadmin.core.domain.model.CodeableConcept;
import com.great.project.patientadmin.core.domain.model.Coding;
import com.great.project.patientadmin.core.domain.model.Communication;
import com.great.project.patientadmin.core.domain.model.GenderAdministrativeCoding;
import com.great.project.patientadmin.core.domain.model.GenderSupplementalCoding;
import com.great.project.patientadmin.core.domain.model.Patient;
import com.great.project.patientadmin.core.domain.model.PersonBirthDateUnknown;
import com.great.project.patientadmin.core.domain.model.Reference;
import com.great.project.patientadmin.core.domain.model.contact.Contact;
import com.great.project.patientadmin.core.domain.model.contactpoint.ContactPoint;
import com.great.project.patientadmin.core.domain.model.contactpoint.ContactPointCodingSystem;
import com.great.project.patientadmin.core.domain.model.contactpoint.ContactPointCodingUse;
import com.great.project.patientadmin.core.domain.model.humanname.HumanName;
import com.great.project.patientadmin.core.domain.model.humanname.HumanNameCodingUse;
import com.great.project.patientadmin.core.domain.model.humanname.HumanNameGiven;
import com.great.project.patientadmin.core.domain.model.humanname.HumanNamePrefix;
import com.great.project.patientadmin.core.domain.model.humanname.HumanNameSuffix;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;
import com.great.project.patientadmin.core.domain.model.identifier.IdentifierCodingUse;

/**
 * Utility class for mapping Patient Event Data to internal Patient model.
 */
public class PatientEventMapper {

    private PatientEventMapper() {
    }

    /**
     * Method to map the Patient Event Data to internal Patient model.
     * 
     * @param patientEventData
     *            - the Patient Event Data attached to the event
     * @return {@link Patient}
     */
    public static Patient mapPatientEventDataToPatient(PatientECCEventDataV1 patientEventData) {

        Patient patient = new Patient();

        // identifier
        patient.setIdentifier(mapToIdentifier(patientEventData));

        // active
        patient.setActive(patientEventData.getActive());

        // humanName
        patient.setName(mapToHumanName(patientEventData));

        // telecom
        patient.setTelecom(mapToContactPoint(patientEventData));

        // genderAdministrative
        if (patientEventData.getGenderAdministrative() != null) {
            GenderAdministrativeCoding genderAdministrative = new GenderAdministrativeCoding(null,
                    patientEventData.getGenderAdministrative().getSystem(),
                    patientEventData.getGenderAdministrative().getVersion(),
                    patientEventData.getGenderAdministrative().getCode(),
                    patientEventData.getGenderAdministrative().getDisplay(),
                    patientEventData.getGenderAdministrative().getUserSelected());
            patient.setGenderAdministrative(genderAdministrative);
        }

        // genderSupplemental
        if (patientEventData.getGenderSupplemental() != null) {
            GenderSupplementalCoding genderSupplemental = new GenderSupplementalCoding(null,
                    patientEventData.getGenderSupplemental().getSystem(),
                    patientEventData.getGenderSupplemental().getVersion(),
                    patientEventData.getGenderSupplemental().getCode(),
                    patientEventData.getGenderSupplemental().getDisplay(),
                    patientEventData.getGenderSupplemental().getUserSelected());
            patient.setGenderSupplemental(genderSupplemental);
        }

        // birthDate
        patient.setBirthDate(patientEventData.getBirthDate());

        // birthDateUnknown
        PersonBirthDateUnknownEventDataType birthDateUnknown = patientEventData.getBirthDateUnknown();
        if (birthDateUnknown != null) {
            patient.setBirthDateUnknown(PersonBirthDateUnknown.fromString(birthDateUnknown.getValue()));
        }

        // birthDateAbsent
        if (patientEventData.getBirthDateAbsent() != null) {
            BirthDateAbsentCoding birthDateAbsent = new BirthDateAbsentCoding(null,
                    patientEventData.getBirthDateAbsent().getSystem(),
                    patientEventData.getBirthDateAbsent().getVersion(), patientEventData.getBirthDateAbsent().getCode(),
                    patientEventData.getBirthDateAbsent().getDisplay(),
                    patientEventData.getBirthDateAbsent().getUserSelected());
            patient.setBirthDateAbsent(birthDateAbsent);
        }

        // deceasedBoolean
        patient.setDeceasedBoolean(patientEventData.getDeceasedBoolean());

        // deceasedDateTime
        patient.setDeceasedDateTime(patientEventData.getDeceasedDateTime());

        // maritalStatus
        if (patientEventData.getMaritalStatus() != null) {
            CodeableConcept maritalStatus = new CodeableConcept(null,
                    patientEventData.getMaritalStatus().getCoding() != null
                            ? patientEventData.getMaritalStatus().getCoding().stream()
                                    .map(ms -> new Coding(null, ms.getSystem(), ms.getVersion(), ms.getCode(),
                                            ms.getDisplay(), ms.getUserSelected()))
                                    .collect(Collectors.toList())
                            : new ArrayList<>(),
                    patientEventData.getMaritalStatus().getText());
            patient.setMaritalStatus(maritalStatus);
        }

        // employer
        patient.setEmployer(mapToEmployer(patientEventData));

        // organization
        patient.setManagingOrganization(mapToOrganization(patientEventData));

        // communication
        patient.setCommunication(mapToCommunication(patientEventData));

        // contact
        patient.setContact(mapToContact(patientEventData));

        return patient;
    }

    private static List<Communication> mapToCommunication(PersonEventData patientEventData) {
        return patientEventData.getCommunication() != null ? patientEventData.getCommunication().stream()
                .map(c -> new Communication(null, getCodeableConcept(c), c.getPreferred())).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static CodeableConcept getCodeableConcept(CommunicationEventDataType c) {
        return c.getLanguage() != null ? new CodeableConcept(null, getCoding(c), c.getLanguage().getText()) : null;
    }

    private static List<Coding> getCoding(CommunicationEventDataType c) {
        return c.getLanguage().getCoding() != null ? c.getLanguage().getCoding().stream()
                .map(co -> new Coding(null, co.getSystem(), co.getVersion(), co.getCode(), co.getDisplay(), null))
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    private static List<Reference> mapToEmployer(PersonEventData patientEventData) {
        return patientEventData.getEmployer() != null ? patientEventData.getEmployer().stream()
                .map(o -> new Reference(null, o.getLink(), o.getType(), mapToIdentifierFromReference(o)))
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    private static Reference mapToOrganization(PatientECCEventDataV1 patientEventData) {
        return patientEventData.getManagingOrganization() != null
                ? new Reference(null, patientEventData.getManagingOrganization().getLink(),
                        patientEventData.getManagingOrganization().getType(),
                        mapToIdentifierFromReference(patientEventData.getManagingOrganization()))
                : null;
    }

    private static List<Identifier> mapToIdentifierFromReference(ReferenceEventDataType referenceEventData) {
        return referenceEventData.getIdentifier() != null
                ? referenceEventData.getIdentifier().stream().map(id -> new Identifier(null, getIdentifierUse(id), null,
                        id.getSystem(), id.getValue(), null, null, null)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<ContactPoint> mapToContactPoint(PersonEventData patientEventData) {
        return patientEventData.getTelecom() != null
                ? patientEventData.getTelecom().stream()
                        .map(t -> new ContactPoint(null, getContactPointSystem(t), t.getValue(), getContactPointUse(t),
                                t.getRank(), t.getPeriodStart(), t.getPeriodEnd()))
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static ContactPointCodingSystem getContactPointSystem(ContactPointEventDataType t) {
        return t.getUse() != null
                ? new ContactPointCodingSystem(null, t.getSystem().getSystem(), t.getSystem().getVersion(),
                        t.getSystem().getCode(), t.getSystem().getDisplay(), null)
                : null;
    }

    private static ContactPointCodingUse getContactPointUse(ContactPointEventDataType t) {
        return t.getUse() != null
                ? new ContactPointCodingUse(null, t.getUse().getSystem(), t.getUse().getVersion(), t.getUse().getCode(),
                        t.getUse().getDisplay(), null)
                : null;
    }

    private static List<HumanName> mapToHumanName(PersonEventData patientEventData) {
        return patientEventData.getName() != null ? patientEventData.getName().stream()
                .map(hn -> new HumanName(null, getHumanNameUse(hn), hn.getText(), hn.getFamily(), getHumanNameGiven(hn),
                        getHumanNamePrefix(hn), getHumanNameSuffix(hn), null, null, false))
                .collect(Collectors.toList()) : new ArrayList<>();
    }

    private static HumanNameCodingUse getHumanNameUse(HumanNameEventDataType hn) {
        return hn.getUse() != null ? new HumanNameCodingUse(null, hn.getUse().getSystem(), hn.getUse().getVersion(),
                hn.getUse().getCode(), hn.getUse().getDisplay(), null) : null;
    }

    private static List<HumanNameGiven> getHumanNameGiven(HumanNameEventDataType hn) {
        return hn.getGiven() != null
                ? hn.getGiven().stream().map(hng -> new HumanNameGiven(null, hng)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<HumanNamePrefix> getHumanNamePrefix(HumanNameEventDataType hn) {
        return hn.getPrefix() != null
                ? hn.getPrefix().stream().map(hnp -> new HumanNamePrefix(null, hnp)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<HumanNameSuffix> getHumanNameSuffix(HumanNameEventDataType hn) {
        return hn.getSuffix() != null
                ? hn.getSuffix().stream().map(hns -> new HumanNameSuffix(null, hns)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<Contact> mapToContact(PatientECCEventDataV1 patientEventData) {
        if (patientEventData.getContact() != null) {
            return mapContactFields(patientEventData);
        }
        return new ArrayList<>();
    }

    private static List<Contact> mapContactFields(PatientECCEventDataV1 patientEventData) {
        List<Contact> contactList = new ArrayList<>();
        for (ContactEventDataType contactEventDataType : patientEventData.getContact()) {
            Contact contact = new Contact();
            // identifier
            contact.setIdentifier(mapToIdentifier(contactEventDataType));

            // active
            contact.setActive(patientEventData.getActive());

            // humanName
            contact.setName(mapToHumanName(contactEventDataType));

            // telecom
            contact.setTelecom(mapToContactPoint(contactEventDataType));

            // genderAdministrative
            if (contactEventDataType.getGenderAdministrative() != null) {
                GenderAdministrativeCoding genderAdministrative = new GenderAdministrativeCoding(null,
                        contactEventDataType.getGenderAdministrative().getSystem(),
                        contactEventDataType.getGenderAdministrative().getVersion(),
                        contactEventDataType.getGenderAdministrative().getCode(),
                        contactEventDataType.getGenderAdministrative().getDisplay(),
                        contactEventDataType.getGenderAdministrative().getUserSelected());
                contact.setGenderAdministrative(genderAdministrative);
            }

            // genderSupplemental
            if (contactEventDataType.getGenderSupplemental() != null) {
                GenderSupplementalCoding genderSupplemental = new GenderSupplementalCoding(null,
                        contactEventDataType.getGenderSupplemental().getSystem(),
                        contactEventDataType.getGenderSupplemental().getVersion(),
                        contactEventDataType.getGenderSupplemental().getCode(),
                        contactEventDataType.getGenderSupplemental().getDisplay(),
                        contactEventDataType.getGenderSupplemental().getUserSelected());
                contact.setGenderSupplemental(genderSupplemental);
            }

            // birthDate
            contact.setBirthDate(contactEventDataType.getBirthDate());

            // birthDateAbsent
            if (contactEventDataType.getBirthDateAbsent() != null) {
                BirthDateAbsentCoding birthDateAbsent = new BirthDateAbsentCoding(null,
                        contactEventDataType.getBirthDateAbsent().getSystem(),
                        contactEventDataType.getBirthDateAbsent().getVersion(),
                        contactEventDataType.getBirthDateAbsent().getCode(),
                        contactEventDataType.getBirthDateAbsent().getDisplay(),
                        contactEventDataType.getBirthDateAbsent().getUserSelected());
                contact.setBirthDateAbsent(birthDateAbsent);
            }

            // deceasedBoolean
            contact.setDeceasedBoolean(contactEventDataType.getDeceasedBoolean());

            // deceasedDateTime
            contact.setDeceasedDateTime(contactEventDataType.getDeceasedDateTime());

            // employer
            contact.setEmployer(mapToEmployer(contactEventDataType));

            // communication
            contact.setCommunication(mapToCommunication(contactEventDataType));

            // contact
            contact.setContact(new ArrayList<>());

            // relationship
            contact.setRelationship(getContactRelationship(contactEventDataType));

            // startDate
            contact.setStartDate(contactEventDataType.getPeriodStart());

            // endDate
            contact.setEndDate(contactEventDataType.getPeriodEnd());

            contactList.add(contact);
        }
        return contactList;
    }

    private static List<CodeableConcept> getContactRelationship(ContactEventDataType c) {
        return c.getRelationship() != null
                ? c.getRelationship().stream().map(rel -> new CodeableConcept(null, mapToCoding(rel), rel.getText()))
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<Coding> mapToCoding(CodeableConceptEventDataType c) {
        return c.getCoding() != null ? c.getCoding().stream().map(cc -> new Coding(null, cc.getSystem(),
                cc.getVersion(), cc.getCode(), cc.getDisplay(), cc.getUserSelected())).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static List<Identifier> mapToIdentifier(PersonEventData patientEventData) {
        return patientEventData.getIdentifier() != null
                ? patientEventData.getIdentifier().stream().map(id -> new Identifier(null, getIdentifierUse(id), null,
                        id.getSystem(), id.getValue(), null, null, null)).collect(Collectors.toList())
                : new ArrayList<>();
    }

    private static IdentifierCodingUse getIdentifierUse(IdentifierEventDataType id) {
        return id.getUse() != null ? new IdentifierCodingUse(null, id.getUse().getSystem(), id.getUse().getVersion(),
                id.getUse().getCode(), id.getUse().getDisplay(), null) : null;
    }

}
