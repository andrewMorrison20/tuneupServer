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

    /**
     * Retrieve all profiles in paged format
     * @param page
     * @return PAge ProfileDto - set of profiles in the db
     */
    @GetMapping()
    public Page<ProfileDto> getAllProfiles(Pageable page) {
        return profileService.findProfilesDto(page);
    }

    /**
     * Retrieve a full profile by its id
     * @param id profile to retrieve from the database
     * @return profile if exsits
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable Long id) {
        ProfileDto profile = profileService.getProfileDto(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    /**
     * Retrieve a profile by its associate UserID
     * @param id id of the profile to retrieve
     * @return ProfileDto - existing profile else throw
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<ProfileDto> getProfileByAppUserId(@PathVariable Long id) {
        ProfileDto profile = profileService.getProfileDtoByUserId(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    /**
     * Create a new profile
     * @param profileDto profile to create
     * @return ProfileDto - newly created profile
     */
    @PostMapping
    public ResponseEntity<ProfileDto> createProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto createdProfile = profileService.createProfile(profileDto);
        return new ResponseEntity<>(createdProfile, HttpStatus.CREATED);

    }

    /**
     * Update an existing profile
     * @param profileDto profile with updated fields
     * @return the update profile
     */
    @PutMapping("/update")
    public ResponseEntity<ProfileDto> updateProfile(@RequestBody ProfileDto profileDto) {
        ProfileDto updatedProfile = profileService.updateProfile(profileDto);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    /**
     * Update the pricing ofan existing profile
     * @param priceDtoSet the new pricing
     * @param profileId the id of the profile to update
     * @return number of rows updated
     */
    @PutMapping("/update/pricing/{profileId}")
    public ResponseEntity<Integer> updatePricing(@RequestBody Set<PriceDto> priceDtoSet, @PathVariable Long profileId) {
        Integer rowsEffected = profileService.updatePricing(priceDtoSet,profileId);
        return ResponseEntity.ok(rowsEffected);
    }

    /**
     * Update the qualifications associated with a profile
     * @param qualificationDtoSet the set of new qualifications
     * @param profileId id of the profile to update
     * @return number of rows updated
     */
    @PutMapping("/update/qualifications/{profileId}")
    public ResponseEntity<Integer> updateQualifications(@RequestBody Set<ProfileInstrumentQualificationDto>
                                                                    qualificationDtoSet, @PathVariable Long profileId){
        Integer rowsEffected = profileService.updateProfileInstrumentQualifications(profileId,qualificationDtoSet);
        return ResponseEntity.ok(rowsEffected);
    }

    /**
     * Find profiles matching a search criteria
     * @param criteria the criteria to search on
     * @param pageable
     * @return Page ProfileDto - page of matching profiles
     */
    @PostMapping("/search")
    public Page<ProfileDto> findBySpec(@RequestBody(required = false) ProfileSearchCriteriaDto criteria, Pageable pageable) {
        if (criteria == null) {
            criteria = new ProfileSearchCriteriaDto();
        }
        return profileService.searchProfiles(criteria, pageable);
    }

    /**
     * Get the set of instrument qualification combinations for a given profile
     * @param profileId Id of the profile to fetch combinations for
     * @return Set ProfileInstrumentQualifications
     */
    @GetMapping("/instrumentQualifications/{profileId}")
    public ResponseEntity<Set<ProfileInstrumentQualificationDto>> getProfileInstrumentQualfiications(@PathVariable Long profileId){
        Set<ProfileInstrumentQualificationDto> instrumentQualificationDtos = profileService.getProfileQualificationsById(profileId);
        return ResponseEntity.ok().body(instrumentQualificationDtos);
    }
}
