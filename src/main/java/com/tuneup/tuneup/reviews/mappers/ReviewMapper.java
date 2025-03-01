package com.tuneup.tuneup.reviews.mappers;

import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.reviews.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface ReviewMapper {

    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "reviewerProfile", ignore = true)
    Review toReview(ReviewDto reviewDto);

    ReviewDto toReviewDto(Review review);
}
