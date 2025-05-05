package com.tuneup.tuneup.Instruments.entities;

import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.qualifications.entities.ProfileInstrumentQualification;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "instrument")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "instruments")
    private Set<Profile> profiles;

    @OneToMany(mappedBy = "instrument")
    private Set<ProfileInstrumentQualification> userProfileInstrumentQualifications;


    public Instrument() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instrument(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;

    }
}
