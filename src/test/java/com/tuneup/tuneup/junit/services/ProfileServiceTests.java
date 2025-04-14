package com.tuneup.tuneup.junit.services;


import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.Instruments.InstrumentMapper;
import com.tuneup.tuneup.Instruments.InstrumentService;
import com.tuneup.tuneup.genres.Genre;
import com.tuneup.tuneup.genres.GenreMapper;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.PriceMapper;
import com.tuneup.tuneup.pricing.PriceValidator;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileSearchCriteriaDto;
import com.tuneup.tuneup.profiles.enums.LessonType;
import com.tuneup.tuneup.profiles.mappers.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileValidator;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.qualifications.ProfileInstrumentQualification;
import com.tuneup.tuneup.qualifications.Qualification;
import com.tuneup.tuneup.qualifications.dtos.ProfileInstrumentQualificationDto;
import com.tuneup.tuneup.qualifications.mappers.ProfileInstrumentQualificationMapper;
import com.tuneup.tuneup.qualifications.repositories.ProfileInstrumentQualificationRepository;
import com.tuneup.tuneup.qualifications.services.QualificationService;
import com.tuneup.tuneup.regions.entities.Region;
import com.tuneup.tuneup.regions.mappers.RegionMapper;
import com.tuneup.tuneup.users.model.AppUser;
import com.tuneup.tuneup.users.services.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

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
        private QualificationService qualificationService;

        @Mock
        private PriceValidator priceValidator;

        @Mock
        private AppUserService appUserService;

        @Mock
        private InstrumentService instrumentService;

        @Mock
        private PriceMapper priceMapper;

        @Mock
        private ProfileInstrumentQualificationRepository profileInstrumentQualificationRepository;

        @Mock
        private ProfileInstrumentQualificationMapper profileInstrumentQualificationMapper;

        @Mock
        private InstrumentMapper instrumentMapper;

        @Mock
        private GenreMapper genreMapper;

        @Mock
        private RegionMapper regionMapper;

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

            ProfileDto result = profileService.createProfile(inputDto);

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
            Long profileId = 1L;
            Profile mockProfile = new Profile();
            ProfileDto expectedDto = new ProfileDto();

            when(profileRepository.findById(profileId)).thenReturn(Optional.of(mockProfile));
            when(profileMapper.toProfileDto(mockProfile)).thenReturn(expectedDto);

            ProfileDto result = profileService.getProfileDto(profileId);

            verify(profileRepository).findById(profileId);
            verify(profileMapper).toProfileDto(mockProfile);

            assertNotNull(result);
            assertEquals(expectedDto, result);
        }

        @Test
        void getProfileDto_shouldReturnNullIfProfileDoesNotExist() {
            Long profileId = 1L;

            when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

            ProfileDto result = profileService.getProfileDto(profileId);

            verify(profileRepository).findById(profileId);
            verify(profileMapper, never()).toProfileDto(any());

            assertNull(result);
        }
        @Test
        void updatePricing_ShouldReturnAffectedRows() {
            Set<PriceDto> priceDtoSet = Collections.singleton(new PriceDto());
            when(priceValidator.validateOrCreatePricing(priceDtoSet)).thenReturn(Collections.singleton(new Price()));
            when(profileValidator.fetchById(1L)).thenReturn(new Profile());

            Integer result = profileService.updatePricing(priceDtoSet, 1L);
            assertEquals(1, result);
        }

        @Test
        void updateProfileInstrumentQualifications_ShouldReturnAffectedRows() {
            Set<ProfileInstrumentQualificationDto> qualificationDtoSet = Collections.singleton(new ProfileInstrumentQualificationDto());
            when(profileValidator.fetchById(1L)).thenReturn(new Profile());
            when(instrumentService.getInstrumentByIdInternal(anyLong())).thenReturn(new Instrument());
            when(qualificationService.getQualificationByIdInternal(anyLong())).thenReturn(new Qualification());;
            Integer result = profileService.updateProfileInstrumentQualifications(1L, qualificationDtoSet);
            assertEquals(1, result);
        }

        // Existing test class remains unchanged...

        @Test
        void updateProfile_shouldUpdateAllFields_WhenDtoHasValues() {
            ProfileDto dto = new ProfileDto();
            dto.setId(1L);
            dto.setDisplayName("John");
            dto.setBio("Bio");
            dto.setProfileType(ProfileType.STUDENT);
            dto.setLessonType(LessonType.INPERSON);
            dto.setGenres(Collections.emptySet());
            dto.setPrices(Collections.emptySet());
            dto.setInstruments(Collections.emptySet());
            dto.setTuitionRegion(new com.tuneup.tuneup.regions.RegionDto());


            Profile existingProfile = new Profile();
            when(profileRepository.findById(any())).thenReturn(Optional.of(existingProfile));
            when(profileRepository.save(existingProfile)).thenReturn(existingProfile);
            when(profileMapper.toProfileDto(existingProfile)).thenReturn(dto);
            when(instrumentMapper.toInstrument(any())).thenReturn((new Instrument()));
            when(priceMapper.toPrice(any())).thenReturn(new Price());
            when(genreMapper.toGenre(any())).thenReturn(new Genre());
            when(regionMapper.toRegion(any())).thenReturn(new Region());

            ProfileDto result = profileService.updateProfile(dto);

            assertNotNull(result);
            verify(profileRepository).save(existingProfile);
        }

        @Test
        void updateProfile_shouldThrow_WhenProfileNotFound() {
            ProfileDto dto = new ProfileDto();
            dto.setId(99L);

            when(profileRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> profileService.updateProfile(dto));
        }

        @Test
        void getProfileDtoByUserId_shouldReturnDto() {
            Long userId = 1L;
            Profile profile = new Profile();
            ProfileDto dto = new ProfileDto();

            when(profileRepository.findByAppUserId(userId)).thenReturn(profile);
            when(profileMapper.toProfileDto(profile)).thenReturn(dto);

            ProfileDto result = profileService.getProfileDtoByUserId(userId);

            assertEquals(dto, result);
            verify(profileValidator).existsByUser(userId);
        }

        @Test
        void existById_shouldReturnTrue() {
            when(profileValidator.existsById(1L)).thenReturn(true);
            assertTrue(profileService.existById(1L));
        }

        @Test
        void fetchProfileEntityInternal_shouldReturnProfile() {
            Profile profile = new Profile();
            when(profileValidator.fetchById(1L)).thenReturn(profile);
            assertEquals(profile, profileService.fetchProfileEntityInternal(1L));
        }

        @Test
        void searchProfiles_shouldReturnPage() {
            Page<Profile> profilePage = new PageImpl<>(List.of(new Profile()));
            Pageable pageable = PageRequest.of(0, 10);
            ProfileDto dto = new ProfileDto();

            when(profileRepository.findAll(ArgumentMatchers.<Specification<Profile>>any(), ArgumentMatchers.<Pageable>any()))
                    .thenReturn(profilePage);
            when(profileMapper.toProfileDto(any())).thenReturn(dto);

            Page<ProfileDto> result = profileService.searchProfiles(new ProfileSearchCriteriaDto(), pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(dto, result.getContent().get(0));
        }

        @Test
        void getProfileQualificationsById_shouldReturnMappedQualifications() {
            Long profileId = 1L;
            ProfileInstrumentQualification qual = new ProfileInstrumentQualification();
            ProfileInstrumentQualificationDto dto = new ProfileInstrumentQualificationDto();

            when(profileInstrumentQualificationRepository.findByProfileId(profileId)).thenReturn(List.of(qual));
            when(profileInstrumentQualificationMapper.toDto(qual)).thenReturn(dto);

            Set<ProfileInstrumentQualificationDto> result = profileService.getProfileQualificationsById(profileId);

            assertEquals(1, result.size());
            assertTrue(result.contains(dto));
        }

        @Test
        void getProfilesWithoutChatHistory_shouldReturnMappedPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Profile> profilePage = new PageImpl<>(List.of(new Profile()));
            ProfileDto dto = new ProfileDto();

            when(profileRepository.findProfilesWithoutChatHistory(1L, true, true, pageable)).thenReturn(profilePage);
            when(profileMapper.toProfileDto(any())).thenReturn(dto);

            Page<ProfileDto> result = profileService.getProfilesWithoutChatHistory(1L, true, true, pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(dto, result.getContent().get(0));
        }

    }


