package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Tuition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Profile student;

    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    private Profile tutor;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean activeTuition;

    public Long getId() { return id; }
    public Profile getStudent() { return student; }
    public Profile getTutor() { return tutor; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActiveTuition() { return activeTuition; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setActiveTuition(boolean activeTuition) { this.activeTuition = activeTuition; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudent(Profile student) {
        this.student = student;
    }

    public void setTutor(Profile tutor) {
        this.tutor = tutor;
    }

}

