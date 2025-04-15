package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.mappers.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.tuitions.dtos.TuitionDto;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import com.tuneup.tuneup.tuitions.mappers.TuitionMapper;
import com.tuneup.tuneup.tuitions.services.TuitionService;
import com.tuneup.tuneup.tuitions.validators.TuitionValidator;
import com.tuneup.tuneup.tuitions.repositories.TuitionRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TuitionServiceTests {

    @Mock
    private TuitionRepository tuitionRepository;

    @Mock
    private ProfileService profileService;

    @Mock
    private TuitionMapper tuitionMapper;

    @Mock
    private TuitionValidator tuitionValidator;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private TuitionService tuitionService;

    @Test
    void testCreateTuition() {
        TuitionDto tuitionDto = new TuitionDto();
        tuitionDto.setStudentProfileId(1L);
        tuitionDto.setTutorProfileId(2L);
        Profile studentProfile = new Profile();
        studentProfile.setId(1L);
        Profile tutorProfile = new Profile();
        tutorProfile.setId(2L);
        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(studentProfile);
        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(tutorProfile);
        doNothing().when(tuitionValidator).validateDto(tuitionDto);
        Tuition tuitionEntity = new Tuition();
        when(tuitionMapper.toEntity(tuitionDto)).thenReturn(tuitionEntity);
        tuitionEntity.setId(10L);
        when(tuitionRepository.save(tuitionEntity)).thenReturn(tuitionEntity);
        TuitionDto returnedDto = new TuitionDto();
        returnedDto.setId(10L);
        when(tuitionMapper.toDto(tuitionEntity)).thenReturn(returnedDto);
        TuitionDto result = tuitionService.createTuition(tuitionDto);
        assertEquals(studentProfile, tuitionEntity.getStudent());
        assertEquals(tutorProfile, tuitionEntity.getTutor());
        assertEquals(returnedDto, result);
        verify(profileService).fetchProfileEntityInternal(1L);
        verify(profileService).fetchProfileEntityInternal(2L);
        verify(tuitionValidator).validateDto(tuitionDto);
        verify(tuitionMapper).toEntity(tuitionDto);
        verify(tuitionRepository).save(tuitionEntity);
        verify(tuitionMapper).toDto(tuitionEntity);
    }

    @Test
    void testExistsByProfileIds() {
        Long tutorId = 2L;
        Long studentId = 1L;
        when(tuitionValidator.existsByTutorStudentId(tutorId, studentId)).thenReturn(true);
        boolean exists = tuitionService.existsByProfileIds(tutorId, studentId);
        assertTrue(exists);
        verify(tuitionValidator).existsByTutorStudentId(tutorId, studentId);
    }

    @Test
    void testGetByProfileIds_found() {
        Long tutorId = 2L;
        Long studentId = 1L;
        Tuition tuition = new Tuition();
        when(tuitionRepository.findByStudentIdAndTutorId(studentId, tutorId)).thenReturn(Optional.of(tuition));
        Tuition result = tuitionService.getByProfileIds(tutorId, studentId);
        assertEquals(tuition, result);
        verify(tuitionRepository).findByStudentIdAndTutorId(studentId, tutorId);
    }

    @Test
    void testGetByProfileIds_notFound() {
        Long tutorId = 2L;
        Long studentId = 1L;
        when(tuitionRepository.findByStudentIdAndTutorId(studentId, tutorId)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(ValidationException.class, () -> tuitionService.getByProfileIds(tutorId, studentId));
        assertEquals("No existing tuition", ex.getMessage());
    }

    @Test
    void testUpdateTuition() {
        TuitionDto tuitionDto = new TuitionDto();
        TuitionDto result = tuitionService.updateTuition(5L, tuitionDto);
        assertNull(result);
    }

    @Test
    void testGetTuitionById() {
        Long tuitionId = 5L;
        Tuition tuitionEntity = new Tuition();
        tuitionEntity.setId(tuitionId);
        when(tuitionValidator.fetchAndValidateById(tuitionId)).thenReturn(tuitionEntity);
        TuitionDto tuitionDto = new TuitionDto();
        tuitionDto.setId(tuitionId);
        when(tuitionMapper.toDto(tuitionEntity)).thenReturn(tuitionDto);
        TuitionDto result = tuitionService.getTuitionById(tuitionId);
        assertEquals(tuitionDto, result);
        verify(tuitionValidator).fetchAndValidateById(tuitionId);
        verify(tuitionMapper).toDto(tuitionEntity);
    }

    @Test
    void testGetTuitionEntityByIdInternal() {
        Long tuitionId = 5L;
        Tuition tuitionEntity = new Tuition();
        tuitionEntity.setId(tuitionId);
        when(tuitionValidator.fetchAndValidateById(tuitionId)).thenReturn(tuitionEntity);
        Tuition result = tuitionService.getTuitionEntityByIdInternal(tuitionId);
        assertEquals(tuitionEntity, result);
        verify(tuitionValidator).fetchAndValidateById(tuitionId);
    }

    @Test
    void testDeleteTuition() {
        tuitionService.deleteTuition(10L);
    }

    @Test
    void testGetRequestsByProfile_whenProfileIsTutor() {
        Long profileId = 3L;
        Profile tutorProfile = new Profile();
        tutorProfile.setId(profileId);
        tutorProfile.setProfileType(ProfileType.TUTOR);
        when(profileService.fetchProfileEntityInternal(profileId)).thenReturn(tutorProfile);
        Pageable pageable = Pageable.unpaged();
        when(profileService.existById(anyLong())).thenReturn(true);
        Profile dummyProfile = new Profile();
        ProfileDto dummyProfileDto = new ProfileDto();
        Page<Profile> page = new PageImpl<>(List.of(dummyProfile));
        when(tuitionRepository.findStudentsByTutorId(profileId, true, pageable)).thenReturn(page);
        when(profileMapper.toProfileDto(dummyProfile)).thenReturn(dummyProfileDto);
        Page<ProfileDto> result = tuitionService.getRequestsByProfile(profileId, pageable, true);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dummyProfileDto, result.getContent().get(0));
    }

    @Test
    void testGetRequestsByProfile_whenProfileIsNotTutor() {
        Long profileId = 4L;
        Profile studentProfile = new Profile();
        studentProfile.setId(profileId);
        studentProfile.setProfileType(ProfileType.STUDENT);
        when(profileService.fetchProfileEntityInternal(profileId)).thenReturn(studentProfile);
        when(profileService.existById(profileId)).thenReturn(true);
        Pageable pageable = Pageable.unpaged();
        Profile dummyProfile = new Profile();
        ProfileDto dummyProfileDto = new ProfileDto();
        Page<Profile> page = new PageImpl<>(List.of(dummyProfile));
        when(tuitionRepository.findTutorsByStudentId(profileId, true, pageable)).thenReturn(page);
        when(profileMapper.toProfileDto(dummyProfile)).thenReturn(dummyProfileDto);
        Page<ProfileDto> result = tuitionService.getRequestsByProfile(profileId, pageable, true);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dummyProfileDto, result.getContent().get(0));
    }

    @Test
    void testGetStudentsByTutor() {
        Long tutorId = 5L;
        when(profileService.existById(anyLong())).thenReturn(true);
        Pageable pageable = Pageable.unpaged();
        Profile dummyProfile = new Profile();
        ProfileDto dummyProfileDto = new ProfileDto();
        Page<Profile> page = new PageImpl<>(List.of(dummyProfile));
        when(tuitionRepository.findStudentsByTutorId(tutorId, false, pageable)).thenReturn(page);
        when(profileMapper.toProfileDto(dummyProfile)).thenReturn(dummyProfileDto);
        Page<ProfileDto> result = tuitionService.getStudentsByTutor(tutorId, pageable, false);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dummyProfileDto, result.getContent().get(0));
    }

    @Test
    void testGetTutorsByStudent() {
        Long studentId = 6L;
        when(profileService.existById(anyLong())).thenReturn(true);
        Pageable pageable = Pageable.unpaged();
        Profile dummyProfile = new Profile();
        ProfileDto dummyProfileDto = new ProfileDto();
        Page<Profile> page = new PageImpl<>(List.of(dummyProfile));
        when(tuitionRepository.findTutorsByStudentId(studentId, false, pageable)).thenReturn(page);
        when(profileMapper.toProfileDto(dummyProfile)).thenReturn(dummyProfileDto);
        Page<ProfileDto> result = tuitionService.getTutorsByStudent(studentId, pageable, false);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(dummyProfileDto, result.getContent().get(0));
    }

    @Test
    void testGetTuitionByProfileIds_sameTypeThrowsException() {
        Long profileId = 7L;
        Long requesterId = 8L;
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setProfileType(ProfileType.TUTOR);
        Profile requester = new Profile();
        requester.setId(requesterId);
        requester.setProfileType(ProfileType.TUTOR);
        when(profileService.fetchProfileEntityInternal(profileId)).thenReturn(profile);
        when(profileService.fetchProfileEntityInternal(requesterId)).thenReturn(requester);
        ValidationException ex = assertThrows(ValidationException.class, () -> tuitionService.getTuitionByProfileIds(profileId, requesterId));
        assertTrue(ex.getMessage().contains("Tuitions only exist between differing profile types"));
    }

    @Test
    void testGetTuitionByProfileIds_studentRequester() {
        Long profileId = 9L;
        Long requesterId = 10L;
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setProfileType(ProfileType.TUTOR);
        Profile requester = new Profile();
        requester.setId(requesterId);
        requester.setProfileType(ProfileType.STUDENT);
        when(profileService.fetchProfileEntityInternal(profileId)).thenReturn(profile);
        when(profileService.fetchProfileEntityInternal(requesterId)).thenReturn(requester);
        Tuition tuition = new Tuition();
        when(tuitionValidator.fetchAndValidateTuitionByIds(requesterId, profileId)).thenReturn(tuition);
        TuitionDto tuitionDto = new TuitionDto();
        when(tuitionMapper.toDto(tuition)).thenReturn(tuitionDto);
        TuitionDto result = tuitionService.getTuitionByProfileIds(profileId, requesterId);
        assertEquals(tuitionDto, result);
    }

    @Test
    void testGetTuitionByProfileIds_tutorRequester() {
        Long profileId = 11L;
        Long requesterId = 12L;
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setProfileType(ProfileType.STUDENT);
        Profile requester = new Profile();
        requester.setId(requesterId);
        requester.setProfileType(ProfileType.TUTOR);
        when(profileService.fetchProfileEntityInternal(profileId)).thenReturn(profile);
        when(profileService.fetchProfileEntityInternal(requesterId)).thenReturn(requester);
        Tuition tuition = new Tuition();
        when(tuitionValidator.fetchAndValidateTuitionByIds(profileId, requesterId)).thenReturn(tuition);
        TuitionDto tuitionDto = new TuitionDto();
        when(tuitionMapper.toDto(tuition)).thenReturn(tuitionDto);
        TuitionDto result = tuitionService.getTuitionByProfileIds(profileId, requesterId);
        assertEquals(tuitionDto, result);
    }

    @Test
    void testDeactivateTuition() {
        Long id = 13L;
        Tuition tuition = new Tuition();
        tuition.setActiveTuition(true);
        when(tuitionRepository.findById(id)).thenReturn(Optional.of(tuition));
        tuitionService.deactivateTuition(id);
        assertFalse(tuition.isActiveTuition());
        verify(tuitionRepository).save(tuition);
    }

    @Test
    void testReactivateTuition() {
        Long id = 14L;
        Tuition tuition = new Tuition();
        tuition.setActiveTuition(false);
        when(tuitionRepository.findById(id)).thenReturn(Optional.of(tuition));
        tuitionService.reactivateTuition(id);
        assertTrue(tuition.isActiveTuition());
        verify(tuitionRepository).save(tuition);
    }

    @Test
    void testFindById_found() {
        Long id = 15L;
        Tuition tuition = new Tuition();
        tuition.setId(id);
        when(tuitionRepository.findById(id)).thenReturn(Optional.of(tuition));
        Tuition result = tuitionService.findById(id);
        assertEquals(tuition, result);
    }

    @Test
    void testFindById_notFound() {
        Long id = 16L;
        when(tuitionRepository.findById(id)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(ValidationException.class, () -> tuitionService.findById(id));
        assertEquals("No tuition found for id: " + id, ex.getMessage());
    }
}
