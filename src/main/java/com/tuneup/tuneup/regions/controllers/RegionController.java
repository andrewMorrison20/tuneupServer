package com.tuneup.tuneup.regions.controllers;

import com.tuneup.tuneup.regions.RegionDto;
import com.tuneup.tuneup.regions.services.RegionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public ResponseEntity<Set<RegionDto>> getRegions(@RequestParam String query) {
       Set<RegionDto> regions =  regionService.getRegions(query);
        return ResponseEntity.ok(regions);
    }
}
