package com.tuneup.tuneup.reviews.validators;

import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.repositories.ReviewRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidator {

    private final ProfileRepository profileRepository;
    private final ReviewRepository reviewRepository;

    public ReviewValidator(ProfileRepository profileRepository, ReviewRepository reviewRepository) {
        this.profileRepository = profileRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Ensure valid data passed in the dto prior to creation
     * @param reviewDto review to validate
     */
    public void  validateReviewDto(ReviewDto reviewDto){
        validateReviewProfile(reviewDto.getProfileId());
        validateReviewProfile(reviewDto.getReviewerProfileId());
        validateDuplicates(reviewDto.getProfileId(),reviewDto.getReviewerProfileId());
    }

    /**
     * Ensure review submitted by a valid profile
     * @param profileId
     */
    private void validateReviewProfile(long profileId) {
         if(!profileRepository.existsById(profileId)){
             throw new ValidationException("Profile does not exist");
         }
    }

    /**
     * Checks for existing review prior to creation
     * @param profileId the id of the profile being reviewed
     * @param reviewerProfileId the review id
     */
    private void validateDuplicates(long profileId, long reviewerProfileId) {
        if(reviewRepository.existsByProfileIdAndReviewerProfileId(profileId,reviewerProfileId)){
            throw new ValidationException("Profile has already left a review for this tutor");
        }
    }
}
