package com.tuneup.tuneup.qualifications;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Qualification {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ProfileInstrumentQualification> getUserProfileInstrumentQualifications() {
        return userProfileInstrumentQualifications;
    }

    public void setUserProfileInstrumentQualifications(Set<ProfileInstrumentQualification> userProfileInstrumentQualifications) {
        this.userProfileInstrumentQualifications = userProfileInstrumentQualifications;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "qualification")
    private Set<ProfileInstrumentQualification> userProfileInstrumentQualifications;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
