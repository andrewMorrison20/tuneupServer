package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.controllers.UserProfileController;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTests {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private UserProfileController profileController;

    private ProfileDto profileDto;

    @BeforeEach
    void setUp() {
        profileDto = new ProfileDto();
        profileDto.setDisplayName("John Doe");
        profileDto.setId(1L);
        profileDto.setBio("Bio");
    }

    @Test
    void testGetProfileByIdReturnsProfileDto() {
        when(profileService.getProfileDto(1L)).thenReturn(profileDto);
        ResponseEntity<ProfileDto> response = profileController.getProfileById(1L);
        assertNotNull(response.getBody());
        assertEquals(profileDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateProfileReturnsCreatedProfile() {
        when(profileService.createProfile(profileDto)).thenReturn(profileDto);
        ResponseEntity<ProfileDto> response = profileController.createProfile(profileDto);
        assertNotNull(response.getBody());
        assertEquals(profileDto, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testUpdateProfileReturnsUpdatedProfile() {
        when(profileService.updateProfile(profileDto)).thenReturn(profileDto);
        ResponseEntity<ProfileDto> response = profileController.updateProfile(profileDto);
        assertNotNull(response.getBody());
        assertEquals(profileDto, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdatePricingReturnsAffectedRows() {
        Set<PriceDto> priceDtoSet = Collections.singleton(new PriceDto());
        when(profileService.updatePricing(priceDtoSet, 1L)).thenReturn(1);
        ResponseEntity<Integer> response = profileController.updatePricing(priceDtoSet, 1L);
        assertEquals(1, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateQualificationsReturnsAffectedRows() {
        Set<ProfileInstrumentQualificationDto> qualificationDtoSet = Collections.singleton(new ProfileInstrumentQualificationDto());
        when(profileService.updateProfileInstrumentQualifications(1L, qualificationDtoSet)).thenReturn(1);
        ResponseEntity<Integer> response = profileController.updateQualifications(qualificationDtoSet, 1L);
        assertEquals(1, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAllProfilesReturnsPageOfProfiles() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProfileDto> pageResult = new PageImpl<>(Collections.singletonList(profileDto), pageable, 1);
        when(profileService.findProfilesDto(pageable)).thenReturn(pageResult);
        Page<ProfileDto> result = profileController.getAllProfiles(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetProfileByAppUserIdReturnsProfileDto() {
        ProfileDto profileByUser = new ProfileDto();
        profileByUser.setId(2L);
        profileByUser.setDisplayName("Jane Doe");
        when(profileService.getProfileDtoByUserId(2L)).thenReturn(profileByUser);
        ResponseEntity<ProfileDto> response = profileController.getProfileByAppUserId(2L);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileByUser, response.getBody());
    }

    @Test
    void testFindBySpec_WithNullCriteria() {
        Pageable pageable = PageRequest.of(0, 10);
        ProfileSearchCriteriaDto defaultCriteria = new ProfileSearchCriteriaDto();
        Page<ProfileDto> pageResult = new PageImpl<>(Collections.singletonList(profileDto), pageable, 1);
        when(profileService.searchProfiles(any(ProfileSearchCriteriaDto.class), eq(pageable))).thenReturn(pageResult);
        Page<ProfileDto> result = profileController.findBySpec(null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindBySpec_WithNonNullCriteria() {
        Pageable pageable = PageRequest.of(0, 10);
        ProfileSearchCriteriaDto criteria = new ProfileSearchCriteriaDto();
        Page<ProfileDto> pageResult = new PageImpl<>(Collections.singletonList(profileDto), pageable, 1);
        when(profileService.searchProfiles(criteria, pageable)).thenReturn(pageResult);
        Page<ProfileDto> result = profileController.findBySpec(criteria, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetProfileInstrumentQualificationsReturnsDtos() {
        Long profileId = 1L;
        Set<ProfileInstrumentQualificationDto> qualifications = Collections.singleton(new ProfileInstrumentQualificationDto());
        when(profileService.getProfileQualificationsById(profileId)).thenReturn(qualifications);
        ResponseEntity<Set<ProfileInstrumentQualificationDto>> response = profileController.getProfileInstrumentQualfiications(profileId);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(qualifications, response.getBody());
    }
}
