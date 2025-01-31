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

    /**
     *  Validate if availability slot is still available
     */
    protected void validateAvailability(Availability availability) {
        if (!availability.getStatus().equals(AvailabilityStatus.AVAILABLE)) {
            throw new IllegalStateException("Slot is no longer available");
        }
    }

    /**
     *  Handles availability adjustment logic
     */
    protected Availability handleAvailabilityAdjustment(Availability availability, LocalDateTime requestStart, LocalDateTime requestEnd) {
        if (availability.getStartTime().equals(requestStart) && availability.getEndTime().equals(requestEnd)) {
            availability.setStatus(AvailabilityStatus.PENDING);
            return availabilityRepository.save(availability);
        }
        return createPendingSlot(availability, requestStart, requestEnd);
    }

    /**
     *  Create a pending availability slot and adjust the existing availability
     */
    private Availability createPendingSlot(Availability availability, LocalDateTime requestStart, LocalDateTime requestEnd) {
        Availability pendingAvailability = new Availability();
        pendingAvailability.setProfile(availability.getProfile());
        pendingAvailability.setStartTime(requestStart);
        pendingAvailability.setEndTime(requestEnd);
        pendingAvailability.setStatus(AvailabilityStatus.PENDING);
        pendingAvailability = availabilityRepository.save(pendingAvailability);

        adjustExistingAvailability(availability, requestStart, requestEnd);

        return pendingAvailability;
    }

    /**
     *  Adjust the existing availability slot after creating a pending one
     */
    private void adjustExistingAvailability(Availability availability, LocalDateTime requestStart, LocalDateTime requestEnd) {
        boolean isSplitRequired = availability.getStartTime().isBefore(requestStart) && availability.getEndTime().isAfter(requestEnd);

        if (isSplitRequired) {
            createNewAvailability(availability, requestEnd, availability.getEndTime());
            availability.setEndTime(requestStart);
        } else if (availability.getStartTime().isBefore(requestStart)) {
            availability.setEndTime(requestStart);
        } else if (availability.getEndTime().isAfter(requestEnd)) {
            availability.setStartTime(requestEnd);
        }

        availabilityRepository.save(availability);
    }

    /**
     *  Creates a new availability slot
     */
    private void createNewAvailability(Availability original, LocalDateTime start, LocalDateTime end) {
        Availability newAvailability = new Availability();
        newAvailability.setProfile(original.getProfile());
        newAvailability.setStartTime(start);
        newAvailability.setEndTime(end);
        newAvailability.setStatus(AvailabilityStatus.AVAILABLE);
        availabilityRepository.save(newAvailability);
    }

    public AvailabilityDto updateAvailabilityStatus(Availability availability, AvailabilityStatus availabilityStatus) {

        availability.setStatus(availabilityStatus);
        availability = availabilityRepository.save(availability);
        return availabilityMapper.toAvailabilityDto(availability);
    }
}
