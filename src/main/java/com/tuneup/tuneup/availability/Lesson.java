package com.tuneup.tuneup.availability;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Profile student;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    private Profile tutor;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private LessonStatus status; // CONFIRMED, COMPLETED, CANCELED

    private String cancellationReason;

    public Lesson() {}

    public Lesson(Long id, Profile student, Profile tutor, LocalDateTime startTime, LocalDateTime endTime, LessonStatus status, String cancellationReason) {
        this.id = id;
        this.student = student;
        this.tutor = tutor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.cancellationReason = cancellationReason;
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

    public LessonStatus getStatus() {
        return status;
    }

    public void setStatus(LessonStatus status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
