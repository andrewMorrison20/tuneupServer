package com.tuneup.tuneup.qualifications.mappers;

import com.tuneup.tuneup.qualifications.entities.Qualification;
import com.tuneup.tuneup.qualifications.dtos.QualificationDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring" )
public interface QualificationMapper {

    QualificationDto toQualificationDto(Qualification qualification);


    Qualification toQualification(QualificationDto qualificationDto);
}
