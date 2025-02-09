package com.tuneup.tuneup.availability.dtos;

public class LessonDto {
    private Long id;
    private Long tuitionId;
    private Long availabilityId;

    public Long getId() {
        return id;
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

    public Long getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }
}
