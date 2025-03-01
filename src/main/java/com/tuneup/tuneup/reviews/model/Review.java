package com.tuneup.tuneup.reviews.model;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String comment;

    @Column(nullable = false)
    private long rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_profile_id", nullable = false)
    private Profile reviewerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    private String reviewerName;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public Profile getReviewerProfile() {
        return reviewerProfile;
    }

    public void setReviewerProfile(Profile reviewerProfile) {
        this.reviewerProfile = reviewerProfile;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}
