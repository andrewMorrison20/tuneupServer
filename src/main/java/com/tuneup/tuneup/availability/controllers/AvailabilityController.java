package com.tuneup.tuneup.availability.controllers;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * This API handles availability creation and management
 */
@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    /**
     * Retrieve availability slots for a given profile by their associate id.
     * @param profileId Id of the profile to retrieve availability for.
     * @return set of availabilities as Dtos.
     */
    @GetMapping("/{profileId}")
    public ResponseEntity<Set<AvailabilityDto>> getAvailability(@PathVariable Long profileId) {
        Set<AvailabilityDto> availabilityList = availabilityService.getAllAvailabilityByProfile(profileId);
        return ResponseEntity.ok(availabilityList);
    }

    /**
     * Retrieve all Availability within a given timeframe for a profile.
     * @param profileId Id of the profile to retrieve availability for.
     * @param start beginning of search window
     * @param end end of search window
     * @return Set of availability as DTOs
     */
    @GetMapping("/{profileId}/period")
    public ResponseEntity<Set<AvailabilityDto>> getPeriodAvailability(
            @PathVariable Long profileId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Set<AvailabilityDto> availabilityList = availabilityService.getProfilePeriodAvailability(profileId, start, end);
        return ResponseEntity.ok(availabilityList);
    }

    /**
     * Bacth create availability slots for a given profile
     * @param profileId Id of profile to create availability for
     * @param availabilityDtos set of availability slots to creat
     * @return Set of AvailabilityDto - created slots as Dtos
     */
    @PostMapping("/{profileId}/batchCreate")
    public ResponseEntity<Set<AvailabilityDto>> createBatchAvailability(
            @PathVariable Long profileId,
            @RequestBody List<AvailabilityDto> availabilityDtos) {

        Set<AvailabilityDto> savedAvailability = availabilityService.batchCreate(profileId, availabilityDtos);

        return ResponseEntity.ok(savedAvailability);
    }

    /**
     * Create a single availability slot for a given profile.
     * @param profileId Id of the profile to created availability for
     * @param availabilityDto details of the slot to create
     * @return AvailabilityDto- the created slot
     */
    @PostMapping("/{profileId}")
    public ResponseEntity<AvailabilityDto> createAvailability(
            @PathVariable Long profileId,
            @RequestBody AvailabilityDto availabilityDto) {

        AvailabilityDto savedAvailability = availabilityService.createAvailability(profileId, availabilityDto);

        return ResponseEntity.ok(savedAvailability);
    }

    /**
     * Update a given availability slot for a given profile.
     * @param profileId Id of the profile to create a slot for.
     * @param availabilityDto slot to update
     * @return AvailabilityDto - updated slot
     */
    @PatchMapping("/update/{profileId}")
    public ResponseEntity<AvailabilityDto> updateAvailability(
            @PathVariable Long profileId,
            @RequestBody AvailabilityDto availabilityDto) {

        AvailabilityDto savedAvailability = availabilityService.updateAvailability(profileId, availabilityDto);

        return ResponseEntity.ok(savedAvailability);
    }

    /**
     * Delete a given availabilityDto for a profile.
     * @param profileId the id of the profile to delete availability for
     * @param availabilityId the slot to delete
     * @return success status
     */
    @DeleteMapping("/delete/{profileId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long profileId,
            @RequestParam Long availabilityId) {

        availabilityService.deleteAvailabilityById(profileId, availabilityId);
        return ResponseEntity.noContent().build();
    }
}
