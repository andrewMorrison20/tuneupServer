package com.tuneup.tuneup.availability.mappers;

import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonRequestMapper {

    @Mapping(target = "tutor", ignore = true)
    @Mapping(target = "student", ignore = true)
    LessonRequest toLessonRequest(LessonRequestDto lessonRequestDto);


    @Mapping(source = "student.id", target = "studentProfileId")
    @Mapping(source = "tutor.id", target = "tutorProfileId")
    @Mapping(source = "availability.id", target = "availabilityId")
    LessonRequestDto toDto(LessonRequest lessonRequest);
}
