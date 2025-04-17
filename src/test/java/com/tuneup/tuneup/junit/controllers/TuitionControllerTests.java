package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.tuitions.controllers.TuitionController;
import com.tuneup.tuneup.tuitions.dtos.TuitionDto;
import com.tuneup.tuneup.tuitions.services.TuitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TuitionControllerTests {

    @Mock
    private TuitionService tuitionService;

    @InjectMocks
    private TuitionController controller;

    private TuitionDto tuitionDto;
    private ProfileDto profileDto1;
    private ProfileDto profileDto2;

    @BeforeEach
    void setUp() {
        tuitionDto = new TuitionDto();
        tuitionDto.setId(10L);
        tuitionDto.setTutorProfileId(5L);
        tuitionDto.setStudentProfileId(7L);

        profileDto1 = new ProfileDto();
        profileDto1.setId(1L);
        profileDto1.setDisplayName("Alice");

        profileDto2 = new ProfileDto();
        profileDto2.setId(2L);
        profileDto2.setDisplayName("Bob");
    }

    @Test
    void getTuitionById_ReturnsDto() {
        when(tuitionService.getTuitionById(10L)).thenReturn(tuitionDto);

        ResponseEntity<TuitionDto> resp = controller.getTuitionById(10L);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(tuitionDto, resp.getBody());
        verify(tuitionService).getTuitionById(10L);
    }

    @Test
    void getTuitionsByProfile_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ProfileDto> page = new PageImpl<>(List.of(profileDto1, profileDto2), pageable, 2);
        when(tuitionService.getRequestsByProfile(3L, pageable, true)).thenReturn(page);

        ResponseEntity<Page<ProfileDto>> resp =
                controller.getTuitionsByProfile(3L, true, pageable);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(page, resp.getBody());
        verify(tuitionService).getRequestsByProfile(3L, pageable, true);
    }

    @Test
    void getTuitionByStudentAndTutor_ReturnsDto() {
        when(tuitionService.getTuitionByProfileIds(7L, 5L)).thenReturn(tuitionDto);

        ResponseEntity<TuitionDto> resp =
                controller.getTuitionByStudentAndTutor(7L, 5L);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(tuitionDto, resp.getBody());
        verify(tuitionService).getTuitionByProfileIds(7L, 5L);
    }

    @Test
    void createTuition_ReturnsDto() {
        when(tuitionService.createTuition(tuitionDto)).thenReturn(tuitionDto);

        ResponseEntity<TuitionDto> resp =
                controller.createTuition(tuitionDto);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(tuitionDto, resp.getBody());
        verify(tuitionService).createTuition(tuitionDto);
    }

    @Test
    void updateTuition_ReturnsDto() {
        TuitionDto updateDto = new TuitionDto();
        updateDto.setId(11L);
        when(tuitionService.updateTuition(11L, updateDto)).thenReturn(updateDto);

        ResponseEntity<TuitionDto> resp =
                controller.updateTuition(11L, updateDto);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(updateDto, resp.getBody());
        verify(tuitionService).updateTuition(11L, updateDto);
    }

    @Test
    void deactivateTuition_NoContent() {
        doNothing().when(tuitionService).deactivateTuition(12L);

        ResponseEntity<Void> resp =
                controller.deactivateTuition(12L);

        assertEquals(204, resp.getStatusCodeValue());
        assertNull(resp.getBody());
        verify(tuitionService).deactivateTuition(12L);
    }

    @Test
    void deleteTuition_NoContent() {
        doNothing().when(tuitionService).deleteTuition(13L);

        ResponseEntity<Void> resp =
                controller.deleteTuition(13L);

        assertEquals(204, resp.getStatusCodeValue());
        assertNull(resp.getBody());
        verify(tuitionService).deleteTuition(13L);
    }
}
