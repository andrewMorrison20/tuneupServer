package com.tuneup.tuneup.availability.dtos;

import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.profiles.enums.LessonType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class LessonDto {

    private Long id;
    private Long tuitionId;
    private AvailabilityDto availabilityDto;
    private LessonStatus lessonStatus;

    @Enumerated(EnumType.STRING)
    private LessonType lessonType;


    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public LessonStatus getLessonStatus() {
        return lessonStatus;
    }

    public void setLessonStatus(LessonStatus lessonStatus) {
        this.lessonStatus = lessonStatus;
    }

    public Long getId() {
        return id;
    }

    public AvailabilityDto getAvailabilityDto() {
        return availabilityDto;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTuitionId() {
        return tuitionId;
    }

    public void setTuitionId(Long tuitionId) {
        this.tuitionId = tuitionId;
    }


    public void setAvailabilityDto(AvailabilityDto availabilityDto) {
        this.availabilityDto = availabilityDto;
    }
}
