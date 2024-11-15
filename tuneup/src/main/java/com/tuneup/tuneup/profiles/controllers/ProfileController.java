package com.tuneup.tuneup.profiles.controllers;


import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.hibernate.query.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(final ProfileService profileService) {

        this.profileService = profileService;
    }

    @GetMapping("/profiles")
    public Page<ProfileDto> getAllProfiles(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return profileService.getAllProfiles(page, size);
    }

}
