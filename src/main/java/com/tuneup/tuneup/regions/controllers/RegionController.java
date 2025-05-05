package com.tuneup.tuneup.regions.controllers;

import com.tuneup.tuneup.regions.dtos.RegionDto;
import com.tuneup.tuneup.regions.services.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * This API is used to retrieve region level address data, for more granular/GPS data see addressController
 */
@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    /**
     * Retrieve region suggestions based on query string
     * @param query the search criteria
     * @return set of matching regions
     */
    @GetMapping
    public ResponseEntity<Set<RegionDto>> getRegions(@RequestParam String query) {
       Set<RegionDto> regions =  regionService.getRegions(query);
        return ResponseEntity.ok(regions);
    }
}
