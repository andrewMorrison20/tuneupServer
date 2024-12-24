package com.tuneup.tuneup.images;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto toImageDto(Image image);

    Image toImage(ImageDto imageDto);
}
