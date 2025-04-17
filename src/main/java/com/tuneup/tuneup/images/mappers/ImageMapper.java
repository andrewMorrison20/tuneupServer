package com.tuneup.tuneup.images.mappers;


import com.tuneup.tuneup.images.entities.Image;
import com.tuneup.tuneup.images.dtos.ImageDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageDto toImageDto(Image image);

    Image toImage(ImageDto imageDto);
}
