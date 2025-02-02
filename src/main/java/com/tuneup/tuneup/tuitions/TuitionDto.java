package com.tuneup.tuneup.tuitions;

import java.time.LocalDate;

public class TuitionDto {

    private Long id;
    private Long tutorProfileId;
    private Long studentProfileId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean activeTuition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTutorProfileId() {
        return tutorProfileId;
    }

    public void setTutorProfileId(Long tutorProfileId) {
        this.tutorProfileId = tutorProfileId;
    }

    public Long getStudentProfileId() {
        return studentProfileId;
    }

    public void setStudentProfileId(Long studentProfileId) {
        this.studentProfileId = studentProfileId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActiveTuition() {
        return activeTuition;
    }

    public void setActiveTuition(boolean activeTuition) {
        this.activeTuition = activeTuition;
    }
}
