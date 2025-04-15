package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.mappers.ReviewMapper;
import com.tuneup.tuneup.reviews.model.Review;
import com.tuneup.tuneup.reviews.repositories.ReviewRepository;
import com.tuneup.tuneup.reviews.services.ReviewService;
import com.tuneup.tuneup.reviews.validators.ReviewValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewValidator reviewValidator;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {

        long profileId = 1L;

        Review review1 = new Review();
        review1.setId(1L);
        review1.setComment("Great service!");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setComment("Excellent tutor!");

        Set<Review> reviews = new HashSet<>();
        reviews.add(review1);
        reviews.add(review2);

        when(reviewRepository.findAllByProfileId(profileId)).thenReturn(reviews);

        ReviewDto reviewDto1 = new ReviewDto();
        reviewDto1.setId(1L);
        reviewDto1.setComment("Great service!");

        ReviewDto reviewDto2 = new ReviewDto();
        reviewDto2.setId(2L);
        reviewDto2.setComment("Excellent tutor!");

        when(reviewMapper.toReviewDto(review1)).thenReturn(reviewDto1);
        when(reviewMapper.toReviewDto(review2)).thenReturn(reviewDto2);

        Set<ReviewDto> result = reviewService.getAll(profileId);

        assertEquals(2, result.size());
        verify(reviewRepository).findAllByProfileId(profileId);
        verify(reviewMapper, times(2)).toReviewDto(any(Review.class));
    }

    @Test
    void testCreateReview() {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setComment("Outstanding tutor!");
        reviewDto.setProfileId(10L);
        reviewDto.setReviewerProfileId(20L);

        Profile targetProfile = new Profile();
        targetProfile.setId(10L);
        Profile reviewerProfile = new Profile();
        reviewerProfile.setId(20L);

        Review review = new Review();
        review.setComment("Outstanding tutor!");
        review.setProfile(targetProfile);
        review.setReviewerProfile(reviewerProfile);

        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setComment("Outstanding tutor!");
        savedReview.setProfile(targetProfile);
        savedReview.setReviewerProfile(reviewerProfile);
        savedReview.setRating(5L);

        ReviewDto savedReviewDto = new ReviewDto();
        savedReviewDto.setId(1L);
        savedReviewDto.setComment("Outstanding tutor!");

        doNothing().when(reviewValidator).validateReviewDto(reviewDto);
        when(reviewMapper.toReview(reviewDto)).thenReturn(review);
        when(profileService.fetchProfileEntityInternal(10L)).thenReturn(targetProfile);
        when(profileService.fetchProfileEntityInternal(20L)).thenReturn(reviewerProfile);
        when(reviewRepository.save(review)).thenReturn(savedReview);
        when(reviewRepository.findAllByProfileId(10L)).thenReturn(Set.of(savedReview));
        when(profileRepository.save(targetProfile)).thenReturn(targetProfile);
        when(reviewMapper.toReviewDto(savedReview)).thenReturn(savedReviewDto);

        ReviewDto result = reviewService.createReview(reviewDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Outstanding tutor!", result.getComment());
        assertEquals(5.0, targetProfile.getAverageRating());

        verify(reviewValidator).validateReviewDto(reviewDto);
        verify(reviewMapper).toReview(reviewDto);
        verify(profileService).fetchProfileEntityInternal(10L);
        verify(profileService).fetchProfileEntityInternal(20L);
        verify(reviewRepository).save(review);
        verify(reviewRepository).findAllByProfileId(10L);
        verify(profileRepository).save(targetProfile);
        verify(reviewMapper).toReviewDto(savedReview);
    }

}


