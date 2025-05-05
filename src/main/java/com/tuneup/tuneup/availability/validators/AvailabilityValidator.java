package com.tuneup.tuneup.availability.validators;

import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailabilityValidator {

    private final AvailabilityRepository availabilityRepository;

    public AvailabilityValidator(AvailabilityRepository availabilityRepository) {
        this.availabilityRepository = availabilityRepository;
    }

    /**
     * Validate an incoming availability dto
     * @param availabilityDto the details of the request to validate
     */
    public void validateAvailabilityDto(AvailabilityDto availabilityDto) {
        validateStartAndEndTime(availabilityDto);
        List<Availability> overlappingAvailabilities = availabilityRepository.findOverlappingAvailabilities(
                availabilityDto.getProfileId(),
                availabilityDto.getStartTime(),
                availabilityDto.getEndTime()
        );

        if (!overlappingAvailabilities.isEmpty()) {
            throw new ValidationException("Availability slot overlaps with an existing booking!");
        }
    }

    /**
     * Checks the db for any conflicting slots (duplicates)
     * @param availabilityDto the details of the slot to validate
     */
    private void validateStartAndEndTime(AvailabilityDto availabilityDto) {
        if (availabilityDto.getStartTime() == null || availabilityDto.getEndTime() == null) {
            throw new ValidationException(" Start Time and End Time must not be null.");
        }

        if (availabilityDto.getStartTime().isAfter(availabilityDto.getEndTime())) {
            throw new ValidationException(" Start Time must be before End Time.");
        }

        // Optional: Prevent zero-length slots
        if (availabilityDto.getStartTime().equals(availabilityDto.getEndTime())) {
            throw new ValidationException("Start Time and End Time cannot be the same.");
        }
    }

    /**
     * Retrieves availbility if it exists, else throws. This is carried out here instead of at service layer to centralise validation exception handling
     * @param id the id of the slot to fetch
     * @return the availability or throw
     */
        public Availability fetchAndValidateById(Long id) {
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new ValidationException("No existing Availability slot with id: " + id));
    }
}

