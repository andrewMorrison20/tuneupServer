package com.tuneup.tuneup.reviews.dtos;

import org.springframework.stereotype.Component;

@Component
public class ReviewDto {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private long id;
    private String comment;
    private String title;
    private long rating;
    private long reviewerProfileId;
    private String reviewerName;

    public long getReviewerProfileId() {
        return reviewerProfileId;
    }

    public void setReviewerProfileId(long reviewerProfileId) {
        this.reviewerProfileId = reviewerProfileId;
    }

    public Long getTuitionId() {
        return tuitionId;
    }

    public void setTuitionId(Long tuitionId) {
        this.tuitionId = tuitionId;
    }

    private Long tuitionId;
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
