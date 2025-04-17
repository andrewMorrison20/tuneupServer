package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.availability.controllers.AvailabilityController;
import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.services.AvailabilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTests {

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailabilityController controller;

    private AvailabilityDto dto1;
    private AvailabilityDto dto2;
    private Set<AvailabilityDto> dtoSet;
    private List<AvailabilityDto> dtoList;

    @BeforeEach
    void setUp() {
        dto1 = new AvailabilityDto();
        dto1.setProfileId(1L);
        dto1.setStartTime(LocalDateTime.of(2025, 4, 17, 9, 0));
        dto1.setEndTime(LocalDateTime.of(2025, 4, 17, 10, 0));

        dto2 = new AvailabilityDto();
        dto2.setProfileId(1L);
        dto2.setStartTime(LocalDateTime.of(2025, 4, 17, 11, 0));
        dto2.setEndTime(LocalDateTime.of(2025, 4, 17, 12, 0));

        dtoSet = Set.of(dto1, dto2);
        dtoList = List.of(dto1, dto2);
    }

    @Test
    void getAvailability_ReturnsDtoSet() {
        when(availabilityService.getAllAvailabilityByProfile(1L)).thenReturn(dtoSet);

        ResponseEntity<Set<AvailabilityDto>> resp = controller.getAvailability(1L);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dtoSet, resp.getBody());
        verify(availabilityService).getAllAvailabilityByProfile(1L);
    }

    @Test
    void getPeriodAvailability_ReturnsDtoSet() {
        LocalDateTime start = LocalDateTime.of(2025, 4, 17, 8, 0);
        LocalDateTime end = LocalDateTime.of(2025, 4, 17, 13, 0);
        when(availabilityService.getProfilePeriodAvailability(1L, start, end)).thenReturn(dtoSet);

        ResponseEntity<Set<AvailabilityDto>> resp = controller.getPeriodAvailability(1L, start, end);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dtoSet, resp.getBody());
        verify(availabilityService).getProfilePeriodAvailability(1L, start, end);
    }

    @Test
    void createBatchAvailability_ReturnsDtoSet() {
        when(availabilityService.batchCreate(1L, dtoList)).thenReturn(dtoSet);

        ResponseEntity<Set<AvailabilityDto>> resp = controller.createBatchAvailability(1L, dtoList);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dtoSet, resp.getBody());
        verify(availabilityService).batchCreate(1L, dtoList);
    }

    @Test
    void createAvailability_ReturnsDto() {
        when(availabilityService.createAvailability(2L, dto1)).thenReturn(dto1);

        ResponseEntity<AvailabilityDto> resp = controller.createAvailability(2L, dto1);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto1, resp.getBody());
        verify(availabilityService).createAvailability(2L, dto1);
    }

    @Test
    void updateAvailability_ReturnsDto() {
        when(availabilityService.updateAvailability(3L, dto2)).thenReturn(dto2);

        ResponseEntity<AvailabilityDto> resp = controller.updateAvailability(3L, dto2);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto2, resp.getBody());
        verify(availabilityService).updateAvailability(3L, dto2);
    }

    @Test
    void deleteAvailability_ReturnsNoContent() {
        doNothing().when(availabilityService).deleteAvailabilityById(4L, 99L);

        ResponseEntity<Void> resp = controller.deleteAvailability(4L, 99L);

        assertEquals(204, resp.getStatusCodeValue());
        assertNull(resp.getBody());
        verify(availabilityService).deleteAvailabilityById(4L, 99L);
    }
}
