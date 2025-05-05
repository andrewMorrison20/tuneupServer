package com.tuneup.tuneup.profiles.dtos;

import com.tuneup.tuneup.profiles.enums.LessonType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class ProfileSearchCriteriaDto {
    private String profileType;
    private String country;
    private Long regionId;
    private Long rating;
    private List<Double> priceRange;
    private Set<Long> qualifications;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Set<LessonType> lessonType;
    private String keyword;
    private Set<Long> instrument;
    private Set<Long> genre;

    public Set<LessonType> getLessonType() {
        return lessonType;
    }

    public void setLessonType(Set<LessonType> lessonType) {
        this.lessonType = lessonType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Double> getPriceRange() {
        return priceRange;
    }

    public Set<Long> getQualifications() {
        return qualifications;
    }

    public void setQualifications(Set<Long> qualificationIds) {
        this.qualifications = qualificationIds;
    }

    public void setPriceRange(List<Double> priceRange) {
        this.priceRange = priceRange;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Long> getInstruments() {
        return instrument;
    }

    public void setInstruments(Set<Long> instruments) {
        this.instrument = instruments;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public Set<Long> getGenre() {
        return genre;
    }

    public void setGenres(Set<Long> genre) {
        this.genre = genre;
    }

}
