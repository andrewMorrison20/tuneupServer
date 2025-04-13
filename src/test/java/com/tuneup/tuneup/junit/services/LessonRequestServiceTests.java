package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.enums.LessonRequestStatus;
import com.tuneup.tuneup.availability.mappers.AvailabilityMapper;
import com.tuneup.tuneup.availability.mappers.LessonRequestMapper;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import com.tuneup.tuneup.availability.services.LessonRequestService;
import com.tuneup.tuneup.availability.services.LessonService;
import com.tuneup.tuneup.availability.validators.LessonRequestValidator;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.notifications.NotificationType;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileMapper;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.enums.LessonType;
import com.tuneup.tuneup.tuitions.TuitionDto;
import com.tuneup.tuneup.tuitions.TuitionService;
import com.tuneup.tuneup.users.model.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonRequestServiceTest {

    private LessonRequestRepository lessonRequestRepository;
    private LessonRequestMapper lessonRequestMapper;
    private ProfileService profileService;
    private ProfileMapper profileMapper;
    private AvailabilityService availabilityService;
    private LessonRequestValidator lessonRequestValidator;
    private TuitionService tuitionService;
    private AvailabilityMapper availabilityMapper;
    private LessonService lessonService;
    private ApplicationEventPublisher eventPublisher;

    private LessonRequestService lessonRequestService;

    private LessonRequestDto requestDto;
    private Availability availability;
    private Availability adjustedAvailability;
    private Profile studentProfile;
    private Profile tutorProfile;
    private LessonRequest lessonRequest;
    private TuitionDto tuitionDto;

    @BeforeEach
    void setUp() {
        lessonRequestRepository = mock(LessonRequestRepository.class);
        lessonRequestMapper = mock(LessonRequestMapper.class);
        profileService = mock(ProfileService.class);
        profileMapper = mock(ProfileMapper.class);
        availabilityService = mock(AvailabilityService.class);
        lessonRequestValidator = mock(LessonRequestValidator.class);
        tuitionService = mock(TuitionService.class);
        availabilityMapper = mock(AvailabilityMapper.class);
        lessonService = mock(LessonService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        lessonRequestService = new LessonRequestService(
                lessonRequestRepository, lessonRequestMapper, profileService, profileMapper,
                availabilityService, lessonRequestValidator, tuitionService, availabilityMapper,
                lessonService, eventPublisher
        );

        requestDto = new LessonRequestDto();
        requestDto.setAvailabilityId(1L);
        requestDto.setStudentProfileId(2L);
        requestDto.setTutorProfileId(3L);
        requestDto.setRequestedStartTime(LocalDateTime.now());
        requestDto.setRequestedEndTime(LocalDateTime.now().plusHours(1));

        availability = new Availability();
        adjustedAvailability = new Availability();

        AppUser studentAppUser = new AppUser();
        studentAppUser.setId(99L);

        AppUser tutorAppUser = new AppUser();
        tutorAppUser.setId(100L);

        studentProfile = new Profile();
        studentProfile.setAppUser(studentAppUser);
        studentProfile.setDisplayName("Student One");

        tutorProfile = new Profile();
        tutorProfile.setAppUser(tutorAppUser);
        tutorProfile.setDisplayName("Tutor One");

        lessonRequest = new LessonRequest();
        lessonRequest.setStudent(studentProfile);
        lessonRequest.setTutor(tutorProfile);
        lessonRequest.setAvailability(availability);
        lessonRequest.setLessonType(LessonType.ONLINE);

        tuitionDto = new TuitionDto();
        tuitionDto.setId(999L);
        tuitionDto.setActiveTuition(true);
    }

    @Test
    void processLessonRequest_shouldCreateLessonRequest() {
        when(availabilityService.getAvailabilityByIdInternal(1L)).thenReturn(availability);
        when(availabilityService.handleAvailabilityAdjustment(eq(availability), any(), any())).thenReturn(adjustedAvailability);
        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(studentProfile);
        when(profileService.fetchProfileEntityInternal(3L)).thenReturn(tutorProfile);
        when(lessonRequestMapper.toLessonRequest(requestDto)).thenReturn(lessonRequest);
        when(lessonRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(lessonRequestMapper.toDto(any())).thenReturn(requestDto);

        LessonRequestDto result = lessonRequestService.processLessonRequest(requestDto);

        assertEquals(requestDto, result);
    }

    @Test
    void getTutorRequestsByStudent_shouldReturnRequests() {
        Profile requester = new Profile();
        requester.setProfileType(ProfileType.STUDENT);

        Profile profile = new Profile();
        profile.setProfileType(ProfileType.TUTOR);

        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(requester);
        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(profile);

        when(lessonRequestRepository.findRequestsByTutorIdAndStudentId(1L, 2L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(Collections.singletonList(lessonRequest)));
        when(lessonRequestMapper.toDto(any())).thenReturn(new LessonRequestDto());

        Page<LessonRequestDto> result = lessonRequestService.getTutorRequestsByStudent(1L, 2L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateLessonRequestStatus_shouldDeleteRequestAndPublishEvent() {
        when(lessonRequestValidator.fetchAndValidateById(1L)).thenReturn(lessonRequest);
        when(tuitionService.getTuitionByProfileIds(any(), any())).thenReturn(tuitionDto);
        when(tuitionService.existsByProfileIds(100L, 99L)).thenReturn(true);
        when(availabilityMapper.toAvailabilityDto(any())).thenReturn(null);
        lessonRequestService.updateLessonRequestStatus(1L, LessonRequestStatus.CONFIRMED.name(), false);

        verify(lessonRequestRepository).delete(lessonRequest);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        NotificationEvent event = captor.getValue();
        assertEquals(NotificationType.REQUEST_ACCEPTED, event.getNotificationType());
        assertEquals(99L, event.getUserId());
    }

    @Test
    void updateLessonRequestStatus_shouldHandleDeclinedStatus() {
        when(lessonRequestValidator.fetchAndValidateById(1L)).thenReturn(lessonRequest);

        lessonRequestService.updateLessonRequestStatus(1L, LessonRequestStatus.DECLINED.name(), false);

        verify(lessonRequestRepository).delete(lessonRequest);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        NotificationEvent event = captor.getValue();
        assertEquals(NotificationType.REQUEST_REJECTED, event.getNotificationType());
        assertEquals(99L, event.getUserId());
    }

    @Test
    void updateLessonRequestStatus_shouldHandleInactiveTuition() {

        when(lessonRequestValidator.fetchAndValidateById(1L)).thenReturn(lessonRequest);
        when( lessonRequestRepository.findAllByAvailabilityId(any())).thenReturn(Set.of(lessonRequest));
        tuitionDto.setActiveTuition(false);
        lessonRequestService.updateLessonRequestStatus(1L, LessonRequestStatus.DECLINED.name(), true);

        verify(lessonRequestRepository).delete(lessonRequest);

        ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        NotificationEvent event = captor.getValue();
        assertEquals(NotificationType.REQUEST_REJECTED, event.getNotificationType());
        assertEquals(99L, event.getUserId());
    }

    @Test
    void getRequestsByTutor_shouldReturnDtoPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LessonRequest> lessonRequestPage = new PageImpl<>(Collections.singletonList(lessonRequest));
        LessonRequestDto dto = new LessonRequestDto();

        when(profileService.existById(1L)).thenReturn(true);
        when(lessonRequestRepository.findByTutorId(1L, pageable)).thenReturn(lessonRequestPage);
        when(lessonRequestMapper.toDto(any())).thenReturn(dto);

        Page<LessonRequestDto> result = lessonRequestService.getRequestsByTutor(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void getRequestsByStudent_shouldReturnDtoPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<LessonRequest> lessonRequestPage = new PageImpl<>(Collections.singletonList(lessonRequest));
        LessonRequestDto dto = new LessonRequestDto();

        when(profileService.existById(2L)).thenReturn(true);
        when(lessonRequestRepository.findByStudentId(2L, pageable)).thenReturn(lessonRequestPage);
        when(lessonRequestMapper.toDto(any())).thenReturn(dto);

        Page<LessonRequestDto> result = lessonRequestService.getRequestsByStudent(2L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
    }

    @Test
    void getAllRequestProfilesByProfileId_shouldReturnStudentsByTutor() {
        Pageable pageable = PageRequest.of(0, 10);
        ProfileDto profileDto = new ProfileDto();
        Page<Profile> students = new PageImpl<>(Collections.singletonList(studentProfile));

        Profile tutor = new Profile();
        tutor.setProfileType(ProfileType.TUTOR);

        when(profileService.fetchProfileEntityInternal(3L)).thenReturn(tutor);
        when(lessonRequestRepository.findStudentsByTutorId(3L, pageable)).thenReturn(students);
        when(profileMapper.toProfileDto(any())).thenReturn(profileDto);

        Page<ProfileDto> result = lessonRequestService.getAllRequestProfilesByProfileId(3L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(profileDto, result.getContent().get(0));
    }

    @Test
    void getAllRequestProfilesByProfileId_shouldReturnTutorsByStudent() {
        Pageable pageable = PageRequest.of(0, 10);
        ProfileDto profileDto = new ProfileDto();
        Page<Profile> tutors = new PageImpl<>(Collections.singletonList(tutorProfile));

        Profile student = new Profile();
        student.setProfileType(ProfileType.STUDENT);

        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(student);
        when(lessonRequestRepository.findTutorsByStudentId(2L, pageable)).thenReturn(tutors);
        when(profileMapper.toProfileDto(any())).thenReturn(profileDto);

        Page<ProfileDto> result = lessonRequestService.getAllRequestProfilesByProfileId(2L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(profileDto, result.getContent().get(0));
    }

    @Test
    void getAllRequestProfilesByProfileId_shouldReturnEmptyForInvalidProfileType() {
        Pageable pageable = PageRequest.of(0, 10);
        Profile unknownTypeProfile = new Profile();
        unknownTypeProfile.setProfileType(null); // simulate unexpected or null type

        when(profileService.fetchProfileEntityInternal(999L)).thenReturn(unknownTypeProfile);

        Page<ProfileDto> result = lessonRequestService.getAllRequestProfilesByProfileId(999L, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
