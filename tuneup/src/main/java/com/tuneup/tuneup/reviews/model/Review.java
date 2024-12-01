package com.tuneup.tuneup.reviews.model;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String comment;
    @Column(nullable = false)
    private long rating;
    @Column(nullable = false)
    private long reviewerUserID;
    @Column(nullable = false)
    private long profileId;
    private String reviewerName;


    public String getComment() {
        return comment;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
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

    public long getReviewerUserID() {
        return reviewerUserID;
    }

    public void setReviewerUserID(long reviewerUserID) {
        this.reviewerUserID = reviewerUserID;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
