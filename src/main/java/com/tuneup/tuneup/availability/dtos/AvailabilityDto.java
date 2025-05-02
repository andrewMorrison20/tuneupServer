package com.tuneup.tuneup.availability.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AvailabilityDto {

    private Long id;
    private Long profileId;

    @JsonAlias({"start","startTime"})
    private LocalDateTime startTime;

    @JsonAlias({"end","endTime"})
    private LocalDateTime endTime;
    private AvailabilityStatus status;

    public AvailabilityStatus getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

