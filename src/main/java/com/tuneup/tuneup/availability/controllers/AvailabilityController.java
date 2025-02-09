package com.tuneup.tuneup.availability.controllers;
import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<Set<AvailabilityDto>> getAvailability(@PathVariable Long profileId) {
        Set<AvailabilityDto> availabilityList = availabilityService.getAllAvailabilityByProfile(profileId);
        return ResponseEntity.ok(availabilityList);
    }

    @GetMapping("/{profileId}/period")
    public ResponseEntity<Set<AvailabilityDto>> getPeriodAvailability(
            @PathVariable Long profileId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Set<AvailabilityDto> availabilityList = availabilityService.getProfilePeriodAvailability(profileId, start, end);
        return ResponseEntity.ok(availabilityList);
    }

    @PostMapping("/{profileId}/batchCreate")
    public ResponseEntity<Set<AvailabilityDto>> setAvailability(
            @PathVariable Long profileId,
            @RequestBody Set<AvailabilityDto> availabilityDtos) {

        Set<AvailabilityDto> savedAvailability = availabilityService.batchCreate(profileId, availabilityDtos);

        return ResponseEntity.ok(savedAvailability);
    }

}

