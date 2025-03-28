package com.tuneup.tuneup.availability.dtos;

import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.profiles.enums.LessonType;

public class LessonSummaryDto {

    private Long id;
    private Long tuitionId;
    private String displayName;

    public LessonSummaryDto(){

    }

    public LessonSummaryDto(Long id, Long tuitionId, String studentName, Long studentProfileId,
                            LessonStatus lessonStatus, LessonType lessonType) {
        this.id = id;
        this.tuitionId = tuitionId;
        this.displayName = studentName;
        this.studentProfileId = studentProfileId;
        this.lessonStatus = lessonStatus;
        this.lessonType = lessonType;
    }

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getStudentProfileId() {
        return studentProfileId;
    }

    public void setStudentProfileId(Long studentProfileId) {
        this.studentProfileId = studentProfileId;
    }

    public Long getTutorProfileId() {
        return tutorProfileId;
    }

    public void setTutorProfileId(Long tutorProfileId) {
        this.tutorProfileId = tutorProfileId;
    }

    public LessonStatus getLessonStatus() {
        return lessonStatus;
    }

    public void setLessonStatus(LessonStatus lessonStatus) {
        this.lessonStatus = lessonStatus;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    private Long studentProfileId;
    private Long tutorProfileId;
    private LessonStatus lessonStatus;
    private LessonType lessonType;


}
