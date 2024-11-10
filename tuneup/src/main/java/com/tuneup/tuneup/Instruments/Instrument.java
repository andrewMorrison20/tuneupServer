package com.tuneup.tuneup.Instruments;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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

    // Constructors, getters, and setters
    public Instrument() {}

    public Instrument(String name) {
        this.name = name;
    }


}
