package com.tuneup.tuneup.availability.dtos;

import java.time.LocalDateTime;

public class LessonRequestDto {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentProfileId() {
        return studentProfileId;
    }

    public void setStudentProfileId(Long studentProfileId) {
        this.studentProfileId = studentProfileId;
    }

    public LocalDateTime getRequestedStartTime() {
        return requestedStartTime;
    }

    public void setRequestedStartTime(LocalDateTime requestedStartTime) {
        this.requestedStartTime = requestedStartTime;
    }

    public Long getTutorProfileId() {
        return tutorProfileId;
    }

    public void setTutorProfileId(Long tutorProfileId) {
        this.tutorProfileId = tutorProfileId;
    }

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public LocalDateTime getRequestedEndTime() {
        return requestedEndTime;
    }

    public void setRequestedEndTime(LocalDateTime requestedEndTime) {
        this.requestedEndTime = requestedEndTime;
    }

    private Long id;
    private Long studentProfileId;
    private Long tutorProfileId;
    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;
    private Long availabilityId;
}
