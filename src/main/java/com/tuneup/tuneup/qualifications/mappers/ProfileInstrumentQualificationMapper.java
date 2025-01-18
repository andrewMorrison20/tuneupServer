package com.tuneup.tuneup.qualifications.mappers;

import com.tuneup.tuneup.qualifications.ProfileInstrumentQualification;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface ProfileInstrumentQualificationMapper {

    ProfileInstrumentQualificationDto toDto(ProfileInstrumentQualification profileInstrumentQualification);

    ProfileInstrumentQualification toEntity(ProfileInstrumentQualificationDto profileInstrumentQualificationDto );
}
