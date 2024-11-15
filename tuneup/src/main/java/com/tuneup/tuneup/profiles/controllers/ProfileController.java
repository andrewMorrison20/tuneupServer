package com.tuneup.tuneup.profiles.controllers;


import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(final ProfileService profileService) {

        this.profileService = profileService;
    }

    @GetMapping("/profiles")
    public Page<ProfileDto> getAllProfiles(@RequestParam Pageable page) {
        return profileService.findProfilesDto(page);
    }

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto createdProfile = profileService.createProfile(profileDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);

    }
}
