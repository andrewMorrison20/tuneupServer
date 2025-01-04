package com.tuneup.tuneup.reviews.repositories;

import com.tuneup.tuneup.reviews.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Set<Review> findAllByProfileId(long profileId);

}
