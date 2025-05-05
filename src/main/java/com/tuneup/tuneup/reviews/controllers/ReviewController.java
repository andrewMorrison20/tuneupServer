package com.tuneup.tuneup.reviews.controllers;

import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Retrieve all reviews for a given profile
     * @param id id of the profile to retrieve reviews for
     * @return Set ReviewDto the existing reviews
     */
    @GetMapping("/{id}")
    public ResponseEntity<Set<ReviewDto>> getProfileReviews(@PathVariable Long id) {
                Set<ReviewDto> reviewDtos = reviewService.getAll(id);
                return ResponseEntity.ok(reviewDtos);
    }

    /**
     * Create a new Review
     * @param reviewDto review to created
     * @return newly created review
     */
    @PostMapping()
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        ReviewDto createReview = reviewService.createReview(reviewDto);
        return ResponseEntity.ok(createReview);
    }
}
