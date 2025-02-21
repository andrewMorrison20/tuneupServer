package com.tuneup.tuneup.availability.validators;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityValidator {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityValidator(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    public void validateAvailabilityDto(AvailabilityDto availabilityDto) {
        if(availabilityRepository.existsByProfileIdAndStartTimeAndEndTime(availabilityDto.getProfileId(),
                availabilityDto.getStartTime(),
                availabilityDto.getEndTime())){
            throw new ValidationException("Availability slot already exists for this profile at this time!");
        }

    }

    public Availability fetchAndValidateById(Long id) {
        return availabilityRepository.findById(id).orElseThrow(() -> new ValidationException("No existing Availbility slot with id : " + id));
    }
}
