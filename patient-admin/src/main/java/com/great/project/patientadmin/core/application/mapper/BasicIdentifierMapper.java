package com.great.project.patientadmin.core.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.great.project.patientadmin.core.domain.model.identifier.BasicIdentifier;
import com.great.project.patientadmin.core.domain.model.identifier.Identifier;

/**
 * Mapper class for Identifier and BasicIdentifier
 */
@Mapper
public interface BasicIdentifierMapper {

    BasicIdentifierMapper INSTANCE = Mappers.getMapper(BasicIdentifierMapper.class);

    @Mapping(target = "use", source = "use.code")
    BasicIdentifier mapToBasicIdentifier(Identifier identifier);

}
