package com.tuneup.tuneup.availability.services;
import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.validators.AvailabilityValidator;
import com.tuneup.tuneup.profiles.Profile;
import org.springframework.stereotype.Service;
import com.tuneup.tuneup.profiles.ProfileValidator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    public Set<AvailabilityDto> getAllAvailabilityByProfile(Long profileId) {
        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileId(profileId)
                .stream()
                .map(availabilityMapper::toAvailabilityDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<AvailabilityDto> batchCreate(Long profileId, Set<AvailabilityDto> availabilityDtos) {

        Profile profile = profileValidator.fetchById(profileId);

        Set<Availability> availabilityEntities = availabilityDtos.stream()
                .peek(availabilityValidator::validateAvailabilityDto)
                .map(availabilityMapper::toAvailability)
                .peek(availability -> availability.setProfile(profile))
                .collect(Collectors.toSet());

        List<Availability> savedAvailability = availabilityRepository.saveAll(availabilityEntities);

        return savedAvailability.stream()
                .map(availabilityMapper::toAvailabilityDto)
                .collect(Collectors.toSet());
    }

    public Set<AvailabilityDto> getProfilePeriodAvailability(Long profileId, LocalDateTime start, LocalDateTime end) {

        profileValidator.validateProfileId(profileId);
        Set<Availability> availability = availabilityRepository.findProfilePeriodSlots(profileId,start,end);

        return availability.stream()
                .map(availabilityMapper :: toAvailabilityDto)
                .collect(Collectors.toSet());
    }

    public Availability getAvailabilityByIdInternal(Long id) {
        return availabilityValidator.fetchAndValidateById(id);
    }
}
