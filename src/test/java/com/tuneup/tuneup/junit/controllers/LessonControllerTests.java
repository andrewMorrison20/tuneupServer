package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.availability.controllers.LessonController;
import com.tuneup.tuneup.availability.dtos.LessonDto;
import com.tuneup.tuneup.availability.dtos.LessonSummaryDto;
import com.tuneup.tuneup.availability.enums.LessonStatus;
import com.tuneup.tuneup.availability.services.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonControllerTests {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController controller;

    private LessonDto lesson1;
    private LessonDto lesson2;
    private LessonSummaryDto summaryDto;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2025, 4, 17, 8, 0);
        end   = LocalDateTime.of(2025, 4, 17, 12, 0);

        lesson1 = new LessonDto();
        lesson1.setId(101L);
        lesson1.setLessonStatus(LessonStatus.CONFIRMED);

        lesson2 = new LessonDto();
        lesson2.setId(102L);
        lesson2.setLessonStatus(LessonStatus.COMPLETED);

        summaryDto = new LessonSummaryDto();
    }

    @Test
    void getLessonsByTuition_ReturnsLessons() {
        when(lessonService.getLessonsByTuitionId(10L, start, end))
                .thenReturn(Set.of(lesson1, lesson2));

        ResponseEntity<Set<LessonDto>> resp =
                controller.getLessonsByTuition(10L, start, end);

        assertEquals(200, resp.getStatusCodeValue());
        assertTrue(resp.getBody().containsAll(Set.of(lesson1, lesson2)));
        verify(lessonService).getLessonsByTuitionId(10L, start, end);
    }

    @Test
    void cancelLesson_DefaultReset_ReturnsNoContent() {
        doNothing().when(lessonService).cancelLesson(20L, false);

        ResponseEntity<Void> resp = controller.cancelLesson(20L, false);

        assertEquals(204, resp.getStatusCodeValue());
        verify(lessonService).cancelLesson(20L, false);
    }

    @Test
    void cancelLesson_WithReset_ReturnsNoContent() {
        doNothing().when(lessonService).cancelLesson(21L, true);

        ResponseEntity<Void> resp = controller.cancelLesson(21L, true);

        assertEquals(204, resp.getStatusCodeValue());
        verify(lessonService).cancelLesson(21L, true);
    }

    @Test
    void updateLessonStatus_ReturnsOk() {
        when(lessonService.updateLessonStatus(30L, LessonStatus.CANCELED))
                .thenReturn(lesson1);

        ResponseEntity<Void> resp =
                controller.updateLessonStatus(30L, LessonStatus.CANCELED);

        assertEquals(200, resp.getStatusCodeValue());
        verify(lessonService).updateLessonStatus(30L, LessonStatus.CANCELED);
    }

    @Test
    void getLessonsByProfile_ReturnsLessons() {
        when(lessonService.getLessonsByProfileId(40L, start, end))
                .thenReturn(Set.of(lesson2));

        ResponseEntity<Set<LessonDto>> resp =
                controller.getLessonsByProfile(40L, start, end);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(Set.of(lesson2), resp.getBody());
        verify(lessonService).getLessonsByProfileId(40L, start, end);
    }

    @Test
    void getLessonsByAvailability_ReturnsSummary() {
        when(lessonService.getLessonSummaryByAvailabilityId(555L))
                .thenReturn(summaryDto);

        ResponseEntity<LessonSummaryDto> resp =
                controller.getLessonsByAvailability(555L);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(summaryDto, resp.getBody());
        verify(lessonService).getLessonSummaryByAvailabilityId(555L);
    }

    @Test
    void getCompletedLessonsByTuition_ReturnsLessons() {
        when(lessonService.getCompletedLessonsByTuitionId(50L, 60L))
                .thenReturn(Set.of(lesson1));

        ResponseEntity<Set<LessonDto>> resp =
                controller.getCompletedLessonsByTuition(50L, 60L);

        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(Set.of(lesson1), resp.getBody());
        verify(lessonService).getCompletedLessonsByTuitionId(50L, 60L);
    }
}
