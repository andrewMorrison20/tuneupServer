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

    public void setId(Long id) {
        this.id = id;
    }

    public Instrument(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public long getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;

    }

}
