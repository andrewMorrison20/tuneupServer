package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.users.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String displayName;
    private String bio;
    private Boolean onlineLessons;
    @Enumerated(EnumType.STRING)
    private ProfileType profileType;

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @ManyToMany
    @JoinTable(
            name = "profile_instrument",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id")
    )
    private Set<Instrument> instruments;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getOnlineLessons() {
        return onlineLessons;
    }

    public Set<Instrument> getInstruments() {
        return instruments;
    }

    public String getBio() {
        return bio;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setOnlineLessons(Boolean onlineLessons) {
        this.onlineLessons = onlineLessons;
    }
    public ProfileType getProfileType() {
        return profileType;
    }
    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }
    public AppUser getAppUser() {
        return appUser;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setInstruments(Set<Instrument> instruments) {
        this.instruments = instruments;
    }

}
