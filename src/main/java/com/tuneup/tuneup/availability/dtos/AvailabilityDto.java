package com.tuneup.tuneup.availability.dtos;

import com.tuneup.tuneup.availability.enums.AvailabilityStatus;

import java.time.LocalDateTime;

public class AvailabilityDto {

    private Long profileId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AvailabilityStatus status;

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

