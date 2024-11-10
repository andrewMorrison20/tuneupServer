package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.profiles.dtos.ProfileType;
import com.tuneup.tuneup.users.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
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



}
