package com.tuneup.tuneup.reviews.services;

import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.mappers.ReviewMapper;
import com.tuneup.tuneup.reviews.model.Review;
import com.tuneup.tuneup.reviews.repositories.ReviewRepository;
import com.tuneup.tuneup.reviews.validators.ReviewValidator;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewValidator reviewValidator;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper, ReviewValidator reviewValidator) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.reviewValidator = reviewValidator;
    }

    public Set<ReviewDto> getAll(long profileId) {

        Set<Review> reviews = reviewRepository.findAllByProfileId(profileId);
        return reviews.stream()
                .map(reviewMapper::toReviewDto)
                .collect(Collectors.toSet());
    }

    public ReviewDto createReview(ReviewDto reviewDto){
        reviewValidator.validateReviewDto(reviewDto);
        Review review = reviewMapper.toReview(reviewDto);
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDto(savedReview);
    }
}
