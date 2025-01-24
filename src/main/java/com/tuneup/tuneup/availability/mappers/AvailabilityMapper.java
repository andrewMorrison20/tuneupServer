package com.tuneup.tuneup.availability.mappers;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import org.mapstruct.Mapper;

@Mapper
public interface AvailabilityMapper {

    AvailabilityDto toAvailabilityDto(Availability availability);

    Availability toAvailability(AvailabilityDto availabilityDto);
}
