package com.tuneup.tuneup.profiles.mappers;


import com.tuneup.tuneup.Instruments.InstrumentMapper;
import com.tuneup.tuneup.genres.GenreMapper;
import com.tuneup.tuneup.images.ImageMapper;
import com.tuneup.tuneup.pricing.PriceMapper;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.regions.RegionMapper;
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
