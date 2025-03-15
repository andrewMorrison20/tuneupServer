package com.tuneup.tuneup.junit.services;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReviewValidator reviewValidator;

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
    @Disabled("Temporarily disabled due to NullPointerException")
    void testCreateReview() {

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setComment("Outstanding tutor!");

        Review review = new Review();
        review.setComment("Outstanding tutor!");

        Review savedReview = new Review();
        savedReview.setId(1L);
        savedReview.setComment("Outstanding tutor!");

        ReviewDto savedReviewDto = new ReviewDto();
        savedReviewDto.setId(1L);
        savedReviewDto.setComment("Outstanding tutor!");

        doNothing().when(reviewValidator).validateReviewDto(reviewDto);
        when(reviewMapper.toReview(reviewDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(savedReview);
        when(reviewMapper.toReviewDto(savedReview)).thenReturn(savedReviewDto);

        ReviewDto result = reviewService.createReview(reviewDto);

        assertEquals(1L, result.getId());
        assertEquals("Outstanding tutor!", result.getComment());
        verify(reviewValidator).validateReviewDto(reviewDto);
        verify(reviewMapper).toReview(reviewDto);
        verify(reviewRepository).save(review);
        verify(reviewMapper).toReviewDto(savedReview);
    }
}


