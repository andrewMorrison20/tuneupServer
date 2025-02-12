package com.tuneup.tuneup.availability.dtos;

public class UpdateLessonRequestStatusDto {
    private String status;
    private Boolean autoDeclineConflicts;

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getAutoDeclineConflicts() {
        return autoDeclineConflicts;
    }

    public void setAutoDeclineConflicts(Boolean autoDeclineConflicts) {
        this.autoDeclineConflicts = autoDeclineConflicts;
    }
}
