package com.tuneup.tuneup.profiles.dtos;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.profiles.ProfileType;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProfileDto {

    private int id;
    private String displayName;
    private String bio;
    private boolean onlineLessons;
    private ProfileType profileType;
    private Set<Instrument> instruments;
    private long appUserId;


}
