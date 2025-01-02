package com.tuneup.tuneup.profiles.dtos;

public class ProfileSearchCriteria {
    private String profileType;
    private String country;
    private Long regionId;

    public String getProfileType() {

        return profileType;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getCountry(){
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getInstruments() {
        return instrument;
    }

    public void setInstruments(Long instruments) {
        this.instrument = instruments;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    private Long instrument;
    private Long genre;

    public Long getGenre() {
        return genre;
    }

    public void setGenres(Long genre) {
        this.genre = genre;
    }

    private String keyword;
}
