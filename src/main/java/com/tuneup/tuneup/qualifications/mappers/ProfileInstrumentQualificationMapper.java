package com.tuneup.tuneup.qualifications.mappers;

import com.tuneup.tuneup.qualifications.ProfileInstrumentQualification;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface ProfileInstrumentQualificationMapper {

    @Mapping(target = "instrumentName", source = "instrument.name")
    @Mapping(target = "instrumentId", source = "instrument.id")
    @Mapping(target = "qualificationName", source = "qualification.name")
    @Mapping(target = "qualificationId", source = "qualification.id")
    ProfileInstrumentQualificationDto toDto(ProfileInstrumentQualification profileInstrumentQualification);

    ProfileInstrumentQualification toEntity(ProfileInstrumentQualificationDto profileInstrumentQualificationDto );
}
