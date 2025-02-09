package com.tuneup.tuneup.availability;

import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.tuitions.Tuition;
import jakarta.persistence.*;

@Entity
@Table(name = "lesson", uniqueConstraints = @UniqueConstraint(columnNames = "availability_id"))
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
    @JoinColumn(name = "availability_id", nullable = false,unique = true)
    private Availability availability;

    public LessonStatus getLessonStatus() {
        return lessonStatus;
    }

    public void setLessonStatus(LessonStatus lessonStatus) {
        this.lessonStatus = lessonStatus;
    }

    @Enumerated(EnumType.STRING)
    private LessonStatus lessonStatus;

}
