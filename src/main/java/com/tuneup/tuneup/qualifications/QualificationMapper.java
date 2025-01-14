package com.tuneup.tuneup.qualifications;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring" )
public interface QualificationMapper {

    QualificationDto toQualificationDto(Qualification qualification);


    Qualification toQualification(QualificationDto qualificationDto);
}
