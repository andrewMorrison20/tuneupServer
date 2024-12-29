package com.tuneup.tuneup.pricing;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Period period;

    private String description;

    @ManyToMany(mappedBy = "prices")
    private Set<Profile> profiles = new HashSet<>();

    private Double rate;

    public void setId(long id) {
        this.id = id;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfile(Set<Profile> profiles) {
        this.profiles = profiles;
    }


    public long getId() {
        return id;
    }

    public Period getPeriod() {
        return period;
    }

    public String getDescription() {
        return description;
    }

    public Double getRate() {
        return rate;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }
}
