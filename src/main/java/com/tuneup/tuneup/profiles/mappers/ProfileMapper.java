package com.tuneup.tuneup.profiles.mappers;


import com.tuneup.tuneup.Instruments.mappers.InstrumentMapper;
import com.tuneup.tuneup.genres.mappers.GenreMapper;
import com.tuneup.tuneup.images.mappers.ImageMapper;
import com.tuneup.tuneup.pricing.mappers.PriceMapper;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.regions.mappers.RegionMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = {RegionMapper.class, GenreMapper.class, PriceMapper.class, InstrumentMapper.class, ImageMapper.class} )
public interface ProfileMapper {

    @Mapping(source = "appUser.id", target = "appUserId")
    ProfileDto toProfileDto(Profile profile);

    Profile toProfile(ProfileDto profileDto);
}
