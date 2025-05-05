package com.tuneup.tuneup.availability.entities;

import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.profiles.enums.LessonType;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import jakarta.persistence.*;

@Entity
@Table(name = "lesson", uniqueConstraints = @UniqueConstraint(columnNames = "availability_id"))
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tuition_id", nullable = false)
    private Tuition tuition;

    @OneToOne
    @JoinColumn(name = "availability_id", nullable = false, unique = true)
    private Availability availability;

    @Enumerated(EnumType.STRING)
    private LessonStatus lessonStatus;

    @Enumerated(EnumType.STRING)
    private LessonType lessonType;

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
}
