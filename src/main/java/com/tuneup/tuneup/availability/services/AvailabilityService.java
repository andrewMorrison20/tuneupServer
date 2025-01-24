package com.tuneup.tuneup.availability.services;
import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.validators.AvailabilityValidator;
import org.springframework.stereotype.Service;
import com.tuneup.tuneup.profiles.ProfileValidator;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final ProfileValidator profileValidator;
    private final AvailabilityValidator availabilityValidator;
    private final AvailabilityMapper availabilityMapper;

    public AvailabilityService(AvailabilityRepository availabilityRepository, ProfileValidator profileValidator, AvailabilityValidator availabilityValidator, AvailabilityMapper availabilityMapper) {
        this.availabilityRepository = availabilityRepository;
        this.profileValidator = profileValidator;
        this.availabilityValidator = availabilityValidator;
        this.availabilityMapper = availabilityMapper;
    }

    public Set<Availability> getUnbookedAvailabilityByProfile(Long profileId) {

        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileIdAndStatus(profileId, AvailabilityStatus.AVAILABLE);
    }

    public Set<Availability> getAllAvailabilityByProfile(Long profileId) {
        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileId(profileId);
    }

    @Transactional
    public void batchCreate(Long profileId, Set<AvailabilityDto> availabilityDtos) {
        // Validate first, then map to entity and collect
        Set<Availability> availabilityEntities = availabilityDtos.stream()
                .peek(availabilityValidator::validateAvailabilityDto)  // ✅ Validate in-place
                .map(availabilityMapper::toAvailability)  // ✅ Convert to entity
                .collect(Collectors.toSet());  // ✅ Collect the results

        availabilityRepository.saveAll(availabilityEntities);  // ✅ Save in batch
    }

}
