package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.controllers.ProfileController;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    private ProfileController profileController;

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
}
