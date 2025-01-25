package com.tuneup.tuneup.profiles.controllers;


import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;

import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable Long id) {
        ProfileDto profile = profileService.getProfileDto(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDto> getProfileByAppUserId(@PathVariable Long id) {
        ProfileDto profile = profileService.getProfileDtoByUserId(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto createdProfile = profileService.createProfile(profileDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);

    }

    @PutMapping("/update")
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto updatedProfile = profileService.updateProfile(profileDto);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @PutMapping("/update/pricing/{profileId}")
    public ResponseEntity<Integer> updatePricing(@RequestBody Set<PriceDto> priceDtoSet, @PathVariable Long profileId) {
        Integer rowsEffected = profileService.updatePricing(priceDtoSet,profileId);
        return ResponseEntity.ok(rowsEffected);
    }

    @PutMapping("/update/qualifications/{profileId}")
    public ResponseEntity<Integer> updateQualifications(@RequestBody Set<ProfileInstrumentQualificationDto>
                                                                    qualificationDtoSet, @PathVariable Long profileId){
        Integer rowsEffected = profileService.updateProfileInstrumentQualifications(profileId,qualificationDtoSet);
        return ResponseEntity.ok(rowsEffected);
    }
    @PostMapping("/search")
    public Page<ProfileDto> findBySpec(@RequestBody(required = false) ProfileSearchCriteriaDto criteria, Pageable pageable) {
        if (criteria == null) {
            criteria = new ProfileSearchCriteriaDto();
        }
        return profileService.searchProfiles(criteria, pageable);
    }
}
