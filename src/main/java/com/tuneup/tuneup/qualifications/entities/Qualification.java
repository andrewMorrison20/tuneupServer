package com.tuneup.tuneup.qualifications.entities;

import com.tuneup.tuneup.qualifications.enums.QualificationName;
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QualificationName name;

    @OneToMany(mappedBy = "qualification")
    private Set<ProfileInstrumentQualification> userProfileInstrumentQualifications;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QualificationName getName() {
        return name;
    }

    public void setName(QualificationName name) {
        this.name = name;
    }

    public Set<ProfileInstrumentQualification> getUserProfileInstrumentQualifications() {
        return userProfileInstrumentQualifications;
    }

    public void setUserProfileInstrumentQualifications(Set<ProfileInstrumentQualification> userProfileInstrumentQualifications) {
        this.userProfileInstrumentQualifications = userProfileInstrumentQualifications;
    }
}
