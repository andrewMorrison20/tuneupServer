package com.tuneup.tuneup.profiles.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.genres.GenreDto;
import com.tuneup.tuneup.images.ImageDto;
import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.enums.LessonType;
import com.tuneup.tuneup.regions.RegionDto;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProfileDto {

    private long id;
    private String displayName;
    private String bio;
    private ProfileType profileType;
    private Set<InstrumentDto> instruments;
    private long appUserId;
    private Double averageRating;
    private LessonType lessonType;

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<PriceDto> prices;
    private Set<GenreDto> genres;

    private ImageDto profilePicture;

    public ImageDto getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ImageDto profilePicture) {
        this.profilePicture = profilePicture;
    }

    public RegionDto getTuitionRegion() {
        return tuitionRegion;
    }

    public void setTuitionRegion(RegionDto tuitionRegion) {
        this.tuitionRegion = tuitionRegion;
    }

    private RegionDto tuitionRegion;

    public void setId(long id) {
        this.id = id;
    }

    public Set<GenreDto> getGenres() {
        return genres;
    }

    public void setGenres(Set<GenreDto> genres) {
        this.genres = genres;
    }

    public Set<PriceDto> getPrices() {
        return prices;
    }
    public void setPrices(Set<PriceDto> prices) {
        this.prices = prices;
    }

    public long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public Set<InstrumentDto> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<InstrumentDto> instruments) {
        this.instruments = instruments;
    }

    public long getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(long appUserId) {
        this.appUserId = appUserId;
    }

}
