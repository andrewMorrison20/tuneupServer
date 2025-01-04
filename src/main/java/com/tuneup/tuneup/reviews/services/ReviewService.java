package com.tuneup.tuneup.reviews.services;

import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.mappers.ReviewMapper;
import com.tuneup.tuneup.reviews.model.Review;
import com.tuneup.tuneup.reviews.repositories.ReviewRepository;
import com.tuneup.tuneup.reviews.validators.ReviewValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewValidator reviewValidator;
    private final ProfileRepository profileRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper, ReviewValidator reviewValidator, ProfileRepository profileRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.reviewValidator = reviewValidator;
        this.profileRepository = profileRepository;
    }

    public Set<ReviewDto> getAll(long profileId) {

        Set<Review> reviews = reviewRepository.findAllByProfileId(profileId);
        return reviews.stream()
                .map(reviewMapper::toReviewDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ReviewDto createReview(ReviewDto reviewDto) {
        reviewValidator.validateReviewDto(reviewDto);
        Review review = reviewMapper.toReview(reviewDto);
        Review savedReview = reviewRepository.save(review);

        updateProfileAverageRating(savedReview.getProfileId());
        return reviewMapper.toReviewDto(savedReview);
    }

    private void updateProfileAverageRating(Long profileId) {

        Set<Review> reviews = reviewRepository.findAllByProfileId(profileId);
        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + profileId));
        profile.setAverageRating(averageRating);

        profileRepository.save(profile);
    }
}