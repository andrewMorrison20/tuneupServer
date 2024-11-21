package com.tuneup.tuneup;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface ReviewMapper {

    Review toReview(ReviewDto reviewDto);

    ReviewDto toReviewDto(Review review);
}
