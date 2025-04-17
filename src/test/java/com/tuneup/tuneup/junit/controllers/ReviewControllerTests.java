package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.reviews.controllers.ReviewController;
import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTests {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewDto dto1;
    private ReviewDto dto2;

    @BeforeEach
    void setUp() {
        dto1 = new ReviewDto();
        dto1.setId(1L);
        dto1.setComment("Good");

        dto2 = new ReviewDto();
        dto2.setId(2L);
        dto2.setComment("Average");
    }

    @Test
    void testGetProfileReviewsReturnsSet() {
        Set<ReviewDto> reviews = new HashSet<>(Set.of(dto1, dto2));
        when(reviewService.getAll(1L)).thenReturn(reviews);

        ResponseEntity<Set<ReviewDto>> response = reviewController.getProfileReviews(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(reviews, response.getBody());
        verify(reviewService, times(1)).getAll(1L);
    }

    @Test
    void testGetProfileReviewsEmptySet() {
        Set<ReviewDto> empty = new HashSet<>();
        when(reviewService.getAll(2L)).thenReturn(empty);

        ResponseEntity<Set<ReviewDto>> response = reviewController.getProfileReviews(2L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(reviewService).getAll(2L);
    }

    @Test
    void testCreateReviewReturnsDto() {
        ReviewDto input = new ReviewDto();
        input.setComment("Excellent");

        ReviewDto saved = new ReviewDto();
        saved.setId(3L);
        saved.setComment("Excellent");

        when(reviewService.createReview(input)).thenReturn(saved);

        ResponseEntity<ReviewDto> response = reviewController.createReview(input);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(saved, response.getBody());
        verify(reviewService).createReview(input);
    }

    @Test
    void testCreateReviewThrowsException() {
        ReviewDto input = new ReviewDto();
        input.setComment("Bad");

        when(reviewService.createReview(input)).thenThrow(new RuntimeException("Error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reviewController.createReview(input));
        assertEquals("Error", ex.getMessage());
        verify(reviewService).createReview(input);
    }
}
