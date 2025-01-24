package com.tuneup.tuneup.availability;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/profile/{profileId}")
    public ResponseEntity<Set<Availability>> getAvailability(@PathVariable Long profileId) {
        Set<Availability> availabilityList = availabilityService.getAllAvailabilityByProfile(profileId);

        if (availabilityList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(availabilityList);
        }
        return ResponseEntity.ok(availabilityList);
    }
}

