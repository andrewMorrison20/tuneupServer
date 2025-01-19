package com.tuneup.tuneup.junit.services;


import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileValidator;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    class ProfileServiceTests {

        @Mock
        private ProfileRepository profileRepository;

        @Mock
        private ProfileMapper profileMapper;

        @Mock
        private ProfileValidator profileValidator;

        @Mock
        private AppUserService appUserService;

        @InjectMocks
        private ProfileService profileService;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void findProfilesDto_shouldReturnMappedPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Profile mockProfile1 = new Profile();
            Profile mockProfile2 = new Profile();
            ProfileDto dto1 = new ProfileDto();
            ProfileDto dto2 = new ProfileDto();
            Page<Profile> mockProfiles = new PageImpl<>(List.of(mockProfile1, mockProfile2));

            when(profileRepository.findAll(pageable)).thenReturn(mockProfiles);
            when(profileMapper.toProfileDto(mockProfile1)).thenReturn(dto1);
            when(profileMapper.toProfileDto(mockProfile2)).thenReturn(dto2);

            // Act
            Page<ProfileDto> result = profileService.findProfilesDto(pageable);

            // Assert
            verify(profileRepository).findAll(pageable);
            verify(profileMapper).toProfileDto(mockProfile1);
            verify(profileMapper).toProfileDto(mockProfile2);

            assertNotNull(result);
            assertEquals(2, result.getContent().size());
            assertTrue(result.getContent().contains(dto1));
            assertTrue(result.getContent().contains(dto2));
        }

        @Test
        void createProfile_shouldValidateSaveAndReturnDto() {
            // Arrange
            ProfileDto inputDto = new ProfileDto();
            inputDto.setAppUserId(1L);

            AppUser mockAppUser = new AppUser();
            Profile mockProfile = new Profile();
            Profile savedProfile = new Profile();
            ProfileDto expectedDto = new ProfileDto();

            when(appUserService.findById(inputDto.getAppUserId())).thenReturn(mockAppUser);
            when(profileMapper.toProfile(inputDto)).thenReturn(mockProfile);
            when(profileRepository.save(mockProfile)).thenReturn(savedProfile);
            when(profileMapper.toProfileDto(savedProfile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.createProfile(inputDto);

            // Assert
            verify(profileValidator).validatorProfileDto(inputDto);
            verify(appUserService).findById(inputDto.getAppUserId());
            verify(profileMapper).toProfile(inputDto);
            verify(profileRepository).save(mockProfile);
            verify(profileMapper).toProfileDto(savedProfile);

            assertNotNull(result);
            assertEquals(expectedDto, result);
        }

        @Test
        void getProfileDto_shouldReturnMappedDtoIfProfileExists() {
            // Arrange
            Long profileId = 1L;
            Profile mockProfile = new Profile();
            ProfileDto expectedDto = new ProfileDto();

            when(profileRepository.findById(profileId)).thenReturn(Optional.of(mockProfile));
            when(profileMapper.toProfileDto(mockProfile)).thenReturn(expectedDto);

            // Act
            ProfileDto result = profileService.getProfileDto(profileId);

            // Assert
            verify(profileRepository).findById(profileId);
            verify(profileMapper).toProfileDto(mockProfile);

            assertNotNull(result);
            assertEquals(expectedDto, result);
        }

        @Test
        void getProfileDto_shouldReturnNullIfProfileDoesNotExist() {
            // Arrange
            Long profileId = 1L;

            when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

            // Act
            ProfileDto result = profileService.getProfileDto(profileId);

            // Assert
            verify(profileRepository).findById(profileId);
            verify(profileMapper, never()).toProfileDto(any());

            assertNull(result);
        }
    }


