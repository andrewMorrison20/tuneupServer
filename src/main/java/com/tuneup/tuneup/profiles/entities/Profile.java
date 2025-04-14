package com.tuneup.tuneup.profiles.entities;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.genres.Genre;
import com.tuneup.tuneup.images.Image;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.enums.LessonType;
import com.tuneup.tuneup.qualifications.ProfileInstrumentQualification;
import com.tuneup.tuneup.regions.entities.Region;
import com.tuneup.tuneup.users.model.AppUser;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Profile {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String displayName;
    private String bio;
    private double averageRating;

    @Enumerated(EnumType.STRING)
    private ProfileType profileType;

    @Enumerated(EnumType.STRING)
    private LessonType lessonType;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image profilePicture;

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

    @ManyToMany
    @JoinTable(
            name = "profile_price",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "price_id")
    )
    private Set<Price> prices;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "profile_genre",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tuition_region_id")
    private Region tuitionRegion;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfileInstrumentQualification> profileInstrumentQualifications;

    private LocalDateTime deletedAt;

    // Getters and Setters

    public long getId() {
        return id;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deletedAt != null;
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

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Image profilePicture) {
        this.profilePicture = profilePicture;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Set<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<Instrument> instruments) {
        this.instruments = instruments;
    }

    public Set<Price> getPrices() {
        return prices;
    }

    public void setPrices(Set<Price> prices) {
        this.prices = prices;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Region getTuitionRegion() {
        return tuitionRegion;
    }

    public void setTuitionRegion(Region tuitionRegion) {
        this.tuitionRegion = tuitionRegion;
    }

    public Set<ProfileInstrumentQualification> getProfileInstrumentQualifications() {
        return profileInstrumentQualifications;
    }

    public void setProfileInstrumentQualifications(Set<ProfileInstrumentQualification> profileInstrumentQualifications) {
        if (this.profileInstrumentQualifications != null) {
            this.profileInstrumentQualifications.clear(); // Ensures orphan removal
            if (profileInstrumentQualifications != null) {
                this.profileInstrumentQualifications.addAll(profileInstrumentQualifications);
            }
        } else {
            this.profileInstrumentQualifications = profileInstrumentQualifications;
        }
    }
}
