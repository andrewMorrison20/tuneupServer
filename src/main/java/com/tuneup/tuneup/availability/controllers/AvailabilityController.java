package com.tuneup.tuneup.availability.controllers;
import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availability/{profileId}")
    public ResponseEntity<Set<Availability>> getAvailability(@PathVariable Long profileId) {
        Set<Availability> availabilityList = availabilityService.getAllAvailabilityByProfile(profileId);

        if (availabilityList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(availabilityList);
        }
        return ResponseEntity.ok(availabilityList);
    }

    @PostMapping("/availability/{profileId}/batchCreate")
    public ResponseEntity<String> setAvailability(
            @PathVariable Long profileId,
            @RequestBody Set<AvailabilityDto> availability) {

        availabilityService.batchCreate(profileId, availability);
        return ResponseEntity.ok("Availability updated successfully.");
    }

}

