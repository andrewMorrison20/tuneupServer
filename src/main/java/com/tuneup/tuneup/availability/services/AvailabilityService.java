package com.tuneup.tuneup.availability.services;

import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.validators.AvailabilityValidator;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.users.exceptions.ValidationException;
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

    /**
     * Retrieve all availability for an associated profile.
     *
     * @param profileId id of the profile to retrieve available slots for
     * @return Set Availability - the set of available slots
     */
    public Set<Availability> getUnbookedAvailabilityByProfile(Long profileId) {

        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileIdAndStatus(profileId, AvailabilityStatus.AVAILABLE);
    }

    /**
     * Get all existing availability for a given profile (all statuses)
     *
     * @param profileId the id of the profile to retrieve availability for.
     * @return Set of AvailabilityDto
     */
    public Set<AvailabilityDto> getAllAvailabilityByProfile(Long profileId) {
        profileValidator.validateProfileId(profileId);
        return availabilityRepository.findByProfileId(profileId)
                .stream()
                .map(availabilityMapper::toAvailabilityDto)
                .collect(Collectors.toSet());
    }

    /**
     * Runs in a transaction (see @Transactional) batch create a set of availability for a given profile.
     *
     * @param profileId        Id of the profile to create slots for
     * @param availabilityDtos set of availability slots to create
     * @return Set AvailabilityDto - newly created slots
     */
    @Transactional
    public Set<AvailabilityDto> batchCreate(Long profileId, List<AvailabilityDto> availabilityDtos) {

        Profile profile = profileValidator.fetchById(profileId);

        Set<Availability> availabilityEntities = availabilityDtos.stream()
                .peek(availabilityValidator::validateAvailabilityDto)
                .map(availabilityMapper::toAvailability)
                .peek(availability -> availability.setProfile(profile))
                .peek(availability -> availability.setStatus(AvailabilityStatus.AVAILABLE))
                .collect(Collectors.toSet());

        List<Availability> savedAvailability = availabilityRepository.saveAll(availabilityEntities);

        return savedAvailability.stream()
                .map(availabilityMapper::toAvailabilityDto)
                .collect(Collectors.toSet());
    }

    /**
     * Create a single Availability slot for a given profile.
     *
     * @param profileId       id of the profile to create new slot for
     * @param availabilityDto the slot to create
     * @return AvailabilityDto - the newly created slot
     */
    @Transactional
    public AvailabilityDto createAvailability(Long profileId, AvailabilityDto availabilityDto) {

        Profile profile = profileValidator.fetchById(profileId);
        availabilityValidator.validateAvailabilityDto(availabilityDto);
        availabilityDto.setStatus(AvailabilityStatus.AVAILABLE);
        Availability availability = availabilityMapper.toAvailability(availabilityDto);
        availability.setProfile(profile);
        availability = availabilityRepository.save(availability);

        return availabilityMapper.toAvailabilityDto(availability);
    }

    /**
     * Get all availability slots for a given profile within a predefined period.
     *
     * @param profileId Id of the profile to retrieve slots for
     * @param start     start of the time period
     * @param end       end of the time period
     * @return Set AvailabilityDto-  the set of existing slots in that period.
     */
    public Set<AvailabilityDto> getProfilePeriodAvailability(Long profileId, LocalDateTime start, LocalDateTime end) {

        profileValidator.validateProfileId(profileId);
        Set<Availability> availability = availabilityRepository.findProfilePeriodSlots(profileId, start, end);

        return availability.stream()
                .map(availabilityMapper::toAvailabilityDto)
                .collect(Collectors.toSet());
    }

    /**
     * FOR INTERNAL USE ONLY.
     * Get a full availability Entity by its Id.
     *
     * @param id of the slot to retrieve
     * @return Availability - the existing slot in the DB
     */
    public Availability getAvailabilityByIdInternal(Long id) {
        return availabilityValidator.fetchAndValidateById(id);
    }

    /**
     * Validate if availability slot is still available
     *
     * @param availability - the slot to validate
     */
    protected void validateAvailability(Availability availability) {
        if (availability.getStatus().equals(AvailabilityStatus.BOOKED)) {
            throw new IllegalStateException("Slot is no longer available");
        }
    }

    /**
     * Handles availability adjustment logic
     */
    @Transactional
    public Availability handleAvailabilityAdjustment(Availability availability, LocalDateTime requestStart, LocalDateTime requestEnd) {
        if (availability.getStartTime().equals(requestStart) && availability.getEndTime().equals(requestEnd)) {
            availability.setStatus(AvailabilityStatus.PENDING);
            return availabilityRepository.save(availability);
        }
        return createPendingSlot(availability, requestStart, requestEnd);
    }

    /**
     * Create a pending availability slot and adjust the existing availability
     *
     * @param availability the slots to create
     * @param requestStart start time of the request
     * @param requestEnd   end time of the request
     * @return Availability - the newly created slot
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
     * Adjusts the current availability slot depending on lesson request time intersection.
     *
     * @param availability the slot to adjust.
     * @param requestStart the start time of the requested lesson.
     * @param requestEnd   the end time of the requested lesson
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
     * Creates a new availability slot when a request partially consumes the existing slot.
     *
     * @param original the existing slot - prior to receiving a request against it.
     * @param start    start time of the request.
     * @param end      end time of the request
     */
    private void createNewAvailability(Availability original, LocalDateTime start, LocalDateTime end) {
        Availability newAvailability = new Availability();
        newAvailability.setProfile(original.getProfile());
        newAvailability.setStartTime(start);
        newAvailability.setEndTime(end);
        newAvailability.setStatus(AvailabilityStatus.AVAILABLE);
        availabilityRepository.save(newAvailability);
    }

    /**
     * Update the status of
     *
     * @param availability
     * @param availabilityStatus
     * @return
     */
    public AvailabilityDto updateAvailabilityStatus(Availability availability, AvailabilityStatus availabilityStatus) {

        availability.setStatus(availabilityStatus);
        availability = availabilityRepository.save(availability);
        return availabilityMapper.toAvailabilityDto(availability);
    }

    /**
     * Update an eixsitng availability slot for a given profile
     *
     * @param profileId       the profile that the availability relates to
     * @param availabilityDto the  availability to update
     * @return the updated availability
     */
    public AvailabilityDto updateAvailability(Long profileId, AvailabilityDto availabilityDto) {
        //Validatae the profile calling the update is registered to the slot
        Availability availability = availabilityRepository
                .findById(availabilityDto.getId())
                .orElseThrow(() -> new ValidationException(
                        "No availability found for id : " + availabilityDto.getId()
                ));

        if (!profileId.equals(availability.getProfile().getId())) {
            throw new ValidationException("This availability slot cannot be edited by a profile that did not create it. incorrect id : " + profileId);
        }

        if (availabilityDto.getStatus() != null) {

            availability.setStatus(availabilityDto.getStatus());
        }

        availability.setStartTime(availabilityDto.getStartTime());
        availability.setEndTime(availabilityDto.getEndTime());

        availability = availabilityRepository.save(availability);

        return availabilityMapper.toAvailabilityDto(availability);
    }

    /**
     * Delete an availability slot for a given profile id
     *
     * @param profileId      the id of the profile attempting delete
     * @param availabilityId the id of the slot to delete
     */
    public void deleteAvailabilityById(Long profileId, Long availabilityId) {

        Availability availability = availabilityRepository
                .findById(availabilityId)
                .orElseThrow(() -> new ValidationException(
                        "No availability found for id : " + availabilityId
                ));

        if (!profileId.equals(availability.getProfile().getId())) {
            throw new ValidationException("Profile attempting delete does not have permission. Incorrect Id : " + profileId);
        }
        availabilityRepository.delete(availability);
    }

    /**
     * delete an availability
     *
     * @param availability to delete
     */
    public void deleteAvailability(Availability availability) {
        availabilityRepository.delete(availability);
    }

    /**
     * save an availability, Used in lesson service to prevent accessing repo layer.
     *
     * @param availability to save
     * @return new created Availability
     */
    public Availability save(Availability availability) {
        return availabilityRepository.save(availability);
    }
}

