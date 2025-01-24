package com.tuneup.tuneup.availability;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LessonRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Profile student;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    private Profile tutor;

    private LocalDateTime requestedTime;

    @Enumerated(EnumType.STRING)
    private LessonRequestStatus status;

    public LessonRequest() {}

    public LessonRequest(Long id, Profile student, Profile tutor, LocalDateTime requestedTime, LessonRequestStatus status) {
        this.id = id;
        this.student = student;
        this.tutor = tutor;
        this.requestedTime = requestedTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profile getStudent() {
        return student;
    }

    public void setStudent(Profile student) {
        this.student = student;
    }

    public Profile getTutor() {
        return tutor;
    }

    public void setTutor(Profile tutor) {
        this.tutor = tutor;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public LessonRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LessonRequestStatus status) {
        this.status = status;
    }
}
