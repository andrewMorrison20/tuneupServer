package com.tuneup.tuneup.profiles.controllers;


import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final ProfileService profileService;

    public UserProfileController(final ProfileService profileService) {

        this.profileService = profileService;
    }

    @GetMapping()
    public Page<ProfileDto> getAllProfiles(Pageable page) {
        return profileService.findProfilesDto(page);
    }

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto createdProfile = profileService.createProfile(profileDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);

    }
}
