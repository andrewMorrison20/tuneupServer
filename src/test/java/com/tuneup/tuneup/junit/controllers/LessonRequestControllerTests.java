package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.availability.dtos.LessonRequestDto;
import com.tuneup.tuneup.availability.dtos.UpdateLessonRequestStatusDto;
import com.tuneup.tuneup.availability.enums.LessonRequestStatus;
import com.tuneup.tuneup.availability.services.LessonRequestService;
import com.tuneup.tuneup.availability.controllers.LessonRequestController;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonRequestControllerTests {

    @Mock
    private LessonRequestService lessonRequestService;

    @InjectMocks
    private LessonRequestController controller;

    private LessonRequestDto requestDto;
    private LessonRequestDto returnedDto;
    private UpdateLessonRequestStatusDto statusDto;
    private ProfileDto student1;
    private ProfileDto student2;

    @BeforeEach
    void setUp() {
        requestDto = new LessonRequestDto();
        requestDto.setStudentProfileId(50L);
        requestDto.setAvailabilityId(60L);


        returnedDto = new LessonRequestDto();
        returnedDto.setId(100L);
        returnedDto.setStudentProfileId(50L);
        returnedDto.setAvailabilityId(60L);

        statusDto = new UpdateLessonRequestStatusDto();
        statusDto.setStatus(LessonRequestStatus.CONFIRMED.getValue());
        statusDto.setAutoDeclineConflicts(true);

        student1 = new ProfileDto();
        student1.setId(1L);
        student1.setDisplayName("Alice");
        student2 = new ProfileDto();
        student2.setId(2L);
        student2.setDisplayName("Bob");
    }

    @Test
    void createLessonRequest_ReturnsCreatedDto() {
        when(lessonRequestService.processLessonRequest(requestDto))
                .thenReturn(returnedDto);

        ResponseEntity<LessonRequestDto> resp = controller.createLessonRequest(requestDto);

        assertEquals(201, resp.getStatusCodeValue());
        assertSame(returnedDto, resp.getBody());
        verify(lessonRequestService).processLessonRequest(requestDto);
    }

    @Test
    void getLessonRequestsByTutor_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<LessonRequestDto> page = new PageImpl<>(List.of(returnedDto), pageable, 1);

        when(lessonRequestService.getRequestsByTutor(10L, pageable)).thenReturn(page);

        ResponseEntity<Page<LessonRequestDto>> resp =
                controller.getLessonRequestsByTutor(10L, pageable);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(page, resp.getBody());
        verify(lessonRequestService).getRequestsByTutor(10L, pageable);
    }

    @Test
    void getLessonRequestsByStudent_ReturnsPage() {
        Pageable pageable = PageRequest.of(1, 3);
        Page<LessonRequestDto> page = new PageImpl<>(List.of(returnedDto), pageable, 1);

        when(lessonRequestService.getRequestsByStudent(20L, pageable)).thenReturn(page);

        ResponseEntity<Page<LessonRequestDto>> resp =
                controller.getLessonRequestsByStudent(20L, pageable);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(page, resp.getBody());
        verify(lessonRequestService).getRequestsByStudent(20L, pageable);
    }

    @Test
    void getLessonRequestsByStudentAndTutor_ReturnsPage() {
        Pageable pageable = PageRequest.of(2, 5);
        Page<LessonRequestDto> page = new PageImpl<>(List.of(returnedDto), pageable, 1);

        when(lessonRequestService.getTutorRequestsByStudent(30L, 40L, pageable))
                .thenReturn(page);

        ResponseEntity<Page<LessonRequestDto>> resp =
                controller.getLessonRequestsByStudentAndTutor(30L, 40L, pageable);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(page, resp.getBody());
        verify(lessonRequestService).getTutorRequestsByStudent(30L, 40L, pageable);
    }

    @Test
    void getStudentsByTutor_ReturnsPageOfProfiles() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProfileDto> page = new PageImpl<>(List.of(student1, student2), pageable, 2);

        when(lessonRequestService.getAllRequestProfilesByProfileId(70L, pageable))
                .thenReturn(page);

        ResponseEntity<Page<ProfileDto>> resp =
                controller.getStudentsByTutor(70L, pageable);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(page, resp.getBody());
        verify(lessonRequestService).getAllRequestProfilesByProfileId(70L, pageable);
    }

    @Test
    void updateRequestStatus_CallsServiceAndReturnsOk() {
        doNothing().when(lessonRequestService)
                .updateLessonRequestStatus(123L, LessonRequestStatus.CONFIRMED.getValue(), true);

        ResponseEntity<Void> resp =
                controller.updateRequestStatus(123L, statusDto);

        assertEquals(200, resp.getStatusCodeValue());
        verify(lessonRequestService)
                .updateLessonRequestStatus(123L, LessonRequestStatus.CONFIRMED.getValue(), true);
    }
}
