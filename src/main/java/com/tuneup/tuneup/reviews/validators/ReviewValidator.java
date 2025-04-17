package com.tuneup.tuneup.reviews.validators;

import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class ReviewValidator {

    private final ProfileRepository profileRepository;


    public ReviewValidator(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public void  validateReviewDto(ReviewDto reviewDto){
        validateReviewProfile(reviewDto.getProfileId());
        validateReviewProfile(reviewDto.getReviewerProfileId());

    }

    private void validateReviewProfile(long profileId) {
         if(!profileRepository.existsById(profileId)){
             throw new ValidationException("Profile does not exist");
         }
    }
}
