package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.genres.Genre;
import com.tuneup.tuneup.images.Image;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.regions.Region;
import com.tuneup.tuneup.users.model.AppUser;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String displayName;

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    private String bio;
    private Boolean onlineLessons;
    @Enumerated(EnumType.STRING)
    private ProfileType profileType;
    private double averageRating;

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

    public Image getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Image image) {
        this.profilePicture = image;
    }

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

    public Set<Genre> getGenres() {
        return this.genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public long getId() {
        return id;
    }

    public Set<Price> getPrices() {
        return prices;
    }

    public void setPrices(Set<Price> prices) {
        this.prices = prices;
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

    public Region getTuitionRegion() {
        return tuitionRegion;
    }

    public void setTuitionRegion(Region tuitionRegion) {
        this.tuitionRegion = tuitionRegion;
    }
}
