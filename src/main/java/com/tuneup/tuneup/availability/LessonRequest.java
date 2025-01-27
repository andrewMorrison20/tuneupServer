package com.tuneup.tuneup.availability;

import com.tuneup.tuneup.availability.enums.LessonRequestStatus;
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

    @Transient
    private LocalDateTime requestedStartTime;

    @Transient
    private LocalDateTime requestedEndTime;

    @Enumerated(EnumType.STRING)
    private LessonRequestStatus status;

    @OneToOne
    @JoinColumn(name = "availability_id")
    private Availability availability;


    @Transient
    public LocalDateTime getRequestedStartTime() {
        return availability != null ? availability.getStartTime() : null;
    }

    @Transient
    public LocalDateTime getRequestedEndTime() {
        return availability != null ? availability.getEndTime() : null;
    }
    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public LessonRequest() {}

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


    public LessonRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LessonRequestStatus status) {
        this.status = status;
    }

}
