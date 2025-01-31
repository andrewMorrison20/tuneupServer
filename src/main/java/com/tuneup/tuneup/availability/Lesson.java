package com.tuneup.tuneup.availability;

import com.tuneup.tuneup.tuitions.Tuition;
import jakarta.persistence.*;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public Tuition getTuition() {
        return tuition;
    }

    public void setTuition(Tuition tuition) {
        this.tuition = tuition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "tuition_id", nullable = false)
    private Tuition tuition;

    @OneToOne
    @JoinColumn(name = "availability_id", nullable = false)
    private Availability availability;

}
