package com.tuneup.tuneup.availability.mappers;

import com.tuneup.tuneup.availability.entities.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { AvailabilityMapper.class} )
public interface LessonMapper {

    @Mapping(target = "tuition", ignore = true)
    @Mapping(source = "availabilityDto", target = "availability")
    Lesson toEntity(LessonDto lessonDto);

    @Mapping(source = "availability", target = "availabilityDto")
    @Mapping(source = "tuition.id", target = "tuitionId")
    LessonDto toDto(Lesson lesson);

}
