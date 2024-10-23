package com.great.project.patientadmin.core.domain.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.great.project.patientadmin.core.domain.model.contact.Contact;
import com.great.project.patientadmin.core.domain.model.contactpoint.ContactPoint;
import com.great.project.patientadmin.core.domain.model.humanname.HumanName;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

    private UUID id;
    private List<Identifier> identifier;
    private Boolean active;
    private List<HumanName> name;
    private String displayName;
    private List<ContactPoint> telecom;
    private GenderAdministrativeCoding genderAdministrative;
    private GenderSupplementalCoding genderSupplemental;
    private LocalDate birthDate;
    private BirthDateAbsentCoding birthDateAbsent;
    private PersonBirthDateUnknown birthDateUnknown;
    private LocalDateTime birthTime;
    private String birthTimeUnknown;
    private Boolean deceasedBoolean;
    private LocalDateTime deceasedDateTime;
    private List<Contact> contact;
    private List<Reference> employer;
    private List<Communication> communication;
    private List<PersonLink> link;

    public void setLink(List<PersonLink> link) {
        if (link == null || link.isEmpty()) {
            this.link = null;
        } else {
            this.link = link;
        }
    }
}
