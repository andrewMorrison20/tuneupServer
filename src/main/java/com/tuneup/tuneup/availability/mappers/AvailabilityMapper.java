package com.tuneup.tuneup.availability.mappers;

import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    //full profile not required at front end for this operation
    @Mapping(source = "profile.id", target = "profileId")
    AvailabilityDto toAvailabilityDto(Availability availability);

    //ignore since we will fetch and assign manaully
    @Mapping(target = "profile", ignore = true)
    Availability toAvailability(AvailabilityDto availabilityDto);
}
