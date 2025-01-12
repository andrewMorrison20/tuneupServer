package com.tuneup.tuneup.qualifications;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;

@Entity
public class ProfileInstrumentQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "instrument_id")
    private Instrument instrument;

    @ManyToOne
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    // Getters and Setters
}