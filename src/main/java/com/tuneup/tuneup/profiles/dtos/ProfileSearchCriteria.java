package com.tuneup.tuneup.profiles.dtos;

import java.util.Set;

public class ProfileSearchCriteria {
    private String profileType; // Tutor type (e.g., Tutor, Student, etc.)
    private String country;     // Country name

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Long> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<Long> instruments) {
        this.instruments = instruments;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private Set<Long> instruments; // List of instrument IDs
    private String keyword;
}
