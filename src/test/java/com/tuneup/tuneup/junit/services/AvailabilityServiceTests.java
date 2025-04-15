package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import com.tuneup.tuneup.availability.validators.AvailabilityValidator;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.ProfileValidator;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityServiceTests {

    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private ProfileValidator profileValidator;
    @Mock
    private AvailabilityValidator availabilityValidator;
    @Mock
    private AvailabilityMapper availabilityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUnbookedAvailabilityByProfile() {
        Long profileId = 1L;
        Set<Availability> availabilities = Set.of(new Availability());
        when(availabilityRepository.findByProfileIdAndStatus(profileId, AvailabilityStatus.AVAILABLE)).thenReturn(availabilities);
        Set<Availability> result = availabilityService.getUnbookedAvailabilityByProfile(profileId);
        verify(profileValidator).validateProfileId(profileId);
        assertEquals(availabilities, result);
    }

    @Test
    void testGetAllAvailabilityByProfile() {
        Long profileId = 1L;
        Availability availability = new Availability();
        AvailabilityDto dto = new AvailabilityDto();
        when(availabilityRepository.findByProfileId(profileId)).thenReturn(Set.of(availability));
        when(availabilityMapper.toAvailabilityDto(availability)).thenReturn(dto);
        Set<AvailabilityDto> result = availabilityService.getAllAvailabilityByProfile(profileId);
        verify(profileValidator).validateProfileId(profileId);
        assertTrue(result.contains(dto));
    }

    @Test
    void testBatchCreate() {
        Long profileId = 1L;
        Profile profile = new Profile();
        profile.setId(profileId);
        AvailabilityDto dto = new AvailabilityDto();
        Availability availability = new Availability();
        availability.setProfile(profile);
        availability.setStatus(AvailabilityStatus.AVAILABLE);
        when(profileValidator.fetchById(profileId)).thenReturn(profile);
        when(availabilityMapper.toAvailability(dto)).thenReturn(availability);
        when(availabilityRepository.saveAll(anySet())).thenReturn(List.of(availability));
        when(availabilityMapper.toAvailabilityDto(availability)).thenReturn(dto);
        Set<AvailabilityDto> result = availabilityService.batchCreate(profileId, List.of(dto));
        assertTrue(result.contains(dto));
    }

    @Test
    void testCreateAvailability() {
        Long profileId = 1L;
        Profile profile = new Profile();
        AvailabilityDto dto = new AvailabilityDto();
        Availability availability = new Availability();
        when(profileValidator.fetchById(profileId)).thenReturn(profile);
        when(availabilityMapper.toAvailability(dto)).thenReturn(availability);
        when(availabilityRepository.save(any())).thenReturn(availability);
        when(availabilityMapper.toAvailabilityDto(availability)).thenReturn(dto);
        AvailabilityDto result = availabilityService.createAvailability(profileId, dto);
        assertEquals(dto, result);
    }

    @Test
    void testGetProfilePeriodAvailability() {
        Long profileId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);
        Availability availability = new Availability();
        AvailabilityDto dto = new AvailabilityDto();
        when(availabilityRepository.findProfilePeriodSlots(profileId, start, end)).thenReturn(Set.of(availability));
        when(availabilityMapper.toAvailabilityDto(availability)).thenReturn(dto);
        Set<AvailabilityDto> result = availabilityService.getProfilePeriodAvailability(profileId, start, end);
        verify(profileValidator).validateProfileId(profileId);
        assertTrue(result.contains(dto));
    }

    @Test
    void testGetAvailabilityByIdInternal() {
        Availability availability = new Availability();
        when(availabilityValidator.fetchAndValidateById(1L)).thenReturn(availability);
        assertEquals(availability, availabilityService.getAvailabilityByIdInternal(1L));
    }

    @Test
    void testHandleAvailabilityAdjustment_ExactMatch() {
        Availability availability = new Availability();
        availability.setStartTime(LocalDateTime.now());
        availability.setEndTime(LocalDateTime.now().plusHours(1));
        availability.setStatus(AvailabilityStatus.AVAILABLE);
        when(availabilityRepository.save(any())).thenReturn(availability);
        Availability result = availabilityService.handleAvailabilityAdjustment(availability, availability.getStartTime(), availability.getEndTime());
        assertEquals(AvailabilityStatus.PENDING, result.getStatus());
    }

    @Test
    void testHandleAvailabilityAdjustment_Split() {
        Availability availability = new Availability();
        availability.setStartTime(LocalDateTime.now());
        availability.setEndTime(availability.getStartTime().plusHours(2));
        Profile profile = new Profile();
        profile.setId(1L);
        availability.setProfile(profile);
        when(availabilityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Availability result = availabilityService.handleAvailabilityAdjustment(availability, availability.getStartTime().plusMinutes(30), availability.getStartTime().plusMinutes(90));
        assertEquals(AvailabilityStatus.PENDING, result.getStatus());
    }

    @Test
    void testUpdateAvailabilityStatus() {
        Availability availability = new Availability();
        AvailabilityDto dto = new AvailabilityDto();
        when(availabilityRepository.save(any())).thenReturn(availability);
        when(availabilityMapper.toAvailabilityDto(availability)).thenReturn(dto);
        assertEquals(dto, availabilityService.updateAvailabilityStatus(availability, AvailabilityStatus.PENDING));
    }

    @Test
    void testUpdateAvailability_Valid() {
        Long profileId = 1L;
        Availability availability = new Availability();
        availability.setProfile(new Profile());
        availability.getProfile().setId(profileId);
        availability.setStartTime(LocalDateTime.now());
        availability.setEndTime(LocalDateTime.now().plusHours(1));
        AvailabilityDto dto = new AvailabilityDto();
        dto.setId(1L);
        dto.setStatus(AvailabilityStatus.AVAILABLE);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(dto.getStartTime().plusHours(1));
        when(availabilityRepository.findById(dto.getId())).thenReturn(Optional.of(availability));
        when(availabilityRepository.save(any())).thenReturn(availability);
        when(availabilityMapper.toAvailabilityDto(availability)).thenReturn(dto);
        AvailabilityDto result = availabilityService.updateAvailability(profileId, dto);
        assertEquals(dto, result);
    }

    @Test
    void testUpdateAvailability_InvalidProfile() {
        Long profileId = 1L;
        Availability availability = new Availability();
        availability.setProfile(new Profile());
        availability.getProfile().setId(2L);
        AvailabilityDto dto = new AvailabilityDto();
        dto.setId(1L);
        when(availabilityRepository.findById(dto.getId())).thenReturn(Optional.of(availability));
        assertThrows(ValidationException.class, () -> availabilityService.updateAvailability(profileId, dto));
    }

    @Test
    void testUpdateAvailability_InvalidAvailabilityId() {
        Long profileId = 1L;
        AvailabilityDto dto = new AvailabilityDto();
        dto.setId(999L);

        when(availabilityRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> availabilityService.updateAvailability(profileId, dto));
    }

    @Test
    void testDeleteAvailabilityById_Valid() {
        Long profileId = 1L;
        Long id = 1L;
        Availability availability = new Availability();
        Profile profile = new Profile();
        profile.setId(profileId);
        availability.setProfile(profile);
        when(availabilityRepository.findById(id)).thenReturn(Optional.of(availability));
        availabilityService.deleteAvailabilityById(profileId, id);
        verify(availabilityRepository).delete(availability);
    }

    @Test
    void testDeleteAvailabilityById_Invalid() {
        Long profileId = 1L;
        Long id = 2L;
        when(availabilityRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> availabilityService.deleteAvailabilityById(profileId, id));
    }

    @Test
    void testDeleteAvailability() {
        Availability availability = new Availability();
        availabilityService.deleteAvailability(availability);
        verify(availabilityRepository).delete(availability);
    }

    @Test
    void testSave() {
        Availability availability = new Availability();
        when(availabilityRepository.save(availability)).thenReturn(availability);
        assertEquals(availability, availabilityService.save(availability));
    }
}
