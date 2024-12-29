package com.tuneup.tuneup.reviews.dtos;

import org.springframework.stereotype.Component;

@Component
public class ReviewDto {

    private long id;
    private String comment;
    private long rating;
    private long reviewerUserID;
    private String reviewerName;
    private long profileId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }
}
