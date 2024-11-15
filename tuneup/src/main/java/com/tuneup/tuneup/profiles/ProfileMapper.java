package com.tuneup.tuneup.profiles;


import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface ProfileMapper {

    ProfileDto toProfileDto(Profile profile);

    Profile toProfile(ProfileDto profileDto);
}
