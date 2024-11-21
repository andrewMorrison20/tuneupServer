package com.tuneup.tuneup;

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

    @GetMapping("/{id}")
    public ResponseEntity<Set<ReviewDto>> getProfileReviews(@PathVariable Long id) {
                Set<ReviewDto> reviewDtos = reviewService.getAll(id);
                return ResponseEntity.ok(reviewDtos);
    }

    @PostMapping()
    public ResponseEntity<ReviewDto> createReview(@RequestBody ReviewDto reviewDto) {
        ReviewDto createReview = reviewService.createReview(reviewDto);
        return ResponseEntity.ok(createReview);
    }

}
