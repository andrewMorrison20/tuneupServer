package com.tuneup.tuneup.availability.mappers;

import com.tuneup.tuneup.availability.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson toEntity(LessonDto lessonDto);

    LessonDto toDto(Lesson lesson);

}
