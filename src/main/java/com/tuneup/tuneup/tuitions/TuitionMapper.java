package com.tuneup.tuneup.tuitions;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface TuitionMapper {

    @Mapping(source = "tutor.id", target = "tutorProfileId")
    @Mapping(source = "student.id", target = "studentProfileId")
    TuitionDto toDto(Tuition tuition);

    //ignore since we will fetch and assign manaully
    @Mapping(target = "tutor", ignore = true)
    @Mapping(target = "student", ignore = true)
    Tuition toEntity(TuitionDto tuitionDto);
}
