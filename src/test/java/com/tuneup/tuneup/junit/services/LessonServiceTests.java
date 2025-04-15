package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.availability.dtos.*;
import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.entities.Lesson;
import com.tuneup.tuneup.availability.enums.*;
import com.tuneup.tuneup.availability.mappers.LessonMapper;
import com.tuneup.tuneup.availability.repositories.LessonRepository;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import com.tuneup.tuneup.availability.services.LessonService;
import com.tuneup.tuneup.notifications.NotificationEvent;
import com.tuneup.tuneup.profiles.entities.Profile;
import com.tuneup.tuneup.profiles.ProfileService;
import com.tuneup.tuneup.profiles.ProfileType;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import com.tuneup.tuneup.tuitions.repositories.TuitionRepository;
import com.tuneup.tuneup.tuitions.services.TuitionService;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.users.model.AppUser;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationEventPublisher;

import java.time.*;
import java.util.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonServiceTests {

    private LessonMapper lessonMapper;
    private LessonRepository lessonRepository;
    private TuitionRepository tuitionRepository;
    private TuitionService tuitionService;
    private ProfileService profileService;
    private AvailabilityService availabilityService;
    private ApplicationEventPublisher eventPublisher;

    private LessonService lessonService;

    @BeforeEach
    void setUp() {
        lessonMapper = mock(LessonMapper.class);
        lessonRepository = mock(LessonRepository.class);
        tuitionRepository = mock(TuitionRepository.class);
        tuitionService = mock(TuitionService.class);
        profileService = mock(ProfileService.class);
        availabilityService = mock(AvailabilityService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        lessonService = new LessonService(
                lessonMapper, lessonRepository, tuitionRepository,
                tuitionService, profileService, availabilityService, eventPublisher
        );
    }

    @Test
    void createLesson_shouldSaveConfirmedLesson() {
        LessonDto dto = new LessonDto();
        dto.setTuitionId(1L);

        Lesson lesson = new Lesson();
        when(lessonMapper.toEntity(dto)).thenReturn(lesson);
        when(tuitionService.getTuitionEntityByIdInternal(1L)).thenReturn(new Tuition());

        lessonService.createLesson(dto);

        assertEquals(LessonStatus.CONFIRMED, lesson.getLessonStatus());
        verify(lessonRepository).save(lesson);
    }

    @Test
    void getLessonsByTuitionId_shouldReturnMappedLessons() {
        Set<Lesson> lessons = Set.of(new Lesson());
        when(tuitionRepository.existsById(1L)).thenReturn(true);
        when(lessonRepository.findLessonsByPeriod(eq(1L), any(), any())).thenReturn((lessons));
        when(lessonMapper.toDto(any())).thenReturn(new LessonDto());

        Set<LessonDto> result = lessonService.getLessonsByTuitionId(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertEquals(1, result.size());
    }

    @Test
    void cancelLesson_shouldResetAvailabilityAndDelete() {
        Lesson lesson = mock(Lesson.class);
        Availability availability = new Availability();
        availability.setStartTime(LocalDateTime.now());
        availability.setEndTime(LocalDateTime.now().plusHours(1));
        availability.setStatus(AvailabilityStatus.PENDING);

        Profile student = new Profile();
        AppUser studentUser = new AppUser(); studentUser.setId(1L);
        student.setAppUser(studentUser); student.setDisplayName("Student");

        Profile tutor = new Profile();
        AppUser tutorUser = new AppUser(); tutorUser.setId(2L);
        tutor.setAppUser(tutorUser); tutor.setDisplayName("Tutor");

        Tuition tuition = new Tuition();
        tuition.setStudent(student); tuition.setTutor(tutor);

        when(lesson.getAvailability()).thenReturn(availability);
        when(lesson.getTuition()).thenReturn(tuition);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.cancelLesson(1L, true);

        verify(availabilityService).save(availability);
        verify(lessonRepository).deleteById(1L);
        verify(eventPublisher, times(2)).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void cancelLesson_shouldDeleteAvailabilityIfResetIsFalse() {
        Lesson lesson = mock(Lesson.class);
        Availability availability = new Availability();
        availability.setStartTime(LocalDateTime.now());
        availability.setEndTime(LocalDateTime.now().plusHours(1));

        Tuition tuition = new Tuition();
        Profile student = new Profile(); AppUser sUser = new AppUser(); sUser.setId(1L); student.setAppUser(sUser); student.setDisplayName("Student");
        Profile tutor = new Profile(); AppUser tUser = new AppUser(); tUser.setId(2L); tutor.setAppUser(tUser); tutor.setDisplayName("Tutor");
        tuition.setStudent(student); tuition.setTutor(tutor);

        when(lesson.getAvailability()).thenReturn(availability);
        when(lesson.getTuition()).thenReturn(tuition);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.cancelLesson(1L, false);

        verify(availabilityService).deleteAvailability(availability);
        verify(lessonRepository).deleteById(1L);
        verify(eventPublisher, times(2)).publishEvent(any(NotificationEvent.class));
    }

    @Test
    void findLessonById_shouldThrowWhenNotFound() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> lessonService.findLessonById(1L));
    }

    @Test
    void getLessonsByProfileId_shouldFetchByType() {
        Profile tutor = new Profile(); tutor.setProfileType(ProfileType.TUTOR);
        Profile student = new Profile(); student.setProfileType(ProfileType.STUDENT);
        Set<Lesson> lessons = Set.of(new Lesson());

        when(profileService.fetchProfileEntityInternal(1L)).thenReturn(tutor);
        when(lessonRepository.findAllLessonsByTutorId(anyLong(), any(), any())).thenReturn(lessons);
        when(lessonMapper.toDto(any())).thenReturn(new LessonDto());
        Set<LessonDto> tutorLessons = lessonService.getLessonsByProfileId(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertEquals(1, tutorLessons.size());

        when(profileService.fetchProfileEntityInternal(2L)).thenReturn(student);
        when(lessonRepository.findAllLessonsByStudentId(anyLong(), any(), any())).thenReturn(lessons);
        Set<LessonDto> studentLessons = lessonService.getLessonsByProfileId(2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        assertEquals(1, studentLessons.size());
    }

    @Test
    void getLessonSummaryByAvailabilityId_shouldReturnSummary() {
        LessonSummaryDto summary = new LessonSummaryDto();
        when(lessonRepository.findLessonSummaryByAvailabilityId(1L)).thenReturn(summary);
        assertEquals(summary, lessonService.getLessonSummaryByAvailabilityId(1L));
    }

    @Test
    void getCompletedLessonsByTuitionId_shouldReturnLessons() {
        Tuition tuition = new Tuition();
        tuition.setId(9L);
        Set<Lesson> lessons = Set.of(new Lesson());

        when(tuitionService.getByProfileIds(1L, 2L)).thenReturn(tuition);
        when(lessonRepository.findCompletedLessonsWithoutPayment(9L, LessonStatus.COMPLETED)).thenReturn(lessons);
        when(lessonMapper.toDto(any())).thenReturn(new LessonDto());

        Set<LessonDto> result = lessonService.getCompletedLessonsByTuitionId(1L, 2L);
        assertEquals(1, result.size());
    }

    @Test
    void updateLessonStatus_shouldThrowIfNotFound() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> lessonService.updateLessonStatus(1L, LessonStatus.CANCELED));
    }

    @Test
    void updateLessonStatus_shouldUpdateAndReturnDto() {
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonMapper.toDto(lesson)).thenReturn(new LessonDto());

        LessonDto result = lessonService.updateLessonStatus(1L, LessonStatus.CANCELED);

        assertEquals(LessonStatus.CANCELED, lesson.getLessonStatus());
        verify(lessonRepository).save(lesson);
        assertNotNull(result);
    }
}