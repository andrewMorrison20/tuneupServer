package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.qualifications.controllers.QualificationController;
import com.tuneup.tuneup.qualifications.dtos.QualificationDto;
import com.tuneup.tuneup.qualifications.enums.QualificationName;
import com.tuneup.tuneup.qualifications.services.QualificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QualificationControllerTests {

    @Mock
    private QualificationService qualificationService;

    @InjectMocks
    private QualificationController controller;

    private QualificationDto dto1;
    private QualificationDto dto2;

    @BeforeEach
    void setUp() {
        dto1 = new QualificationDto();
        dto1.setId(1L);
        dto1.setName(QualificationName.GRADE_1);

        dto2 = new QualificationDto();
        dto2.setId(2L);
        dto2.setName(QualificationName.GRADE_2);
    }

    @Test
    void getAllQualifications_ReturnsSet() {
        Set<QualificationDto> set = Set.of(dto1, dto2);
        when(qualificationService.getAllQualifications()).thenReturn(set);

        ResponseEntity<Set<QualificationDto>> resp = controller.getAllQualifications();

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(set, resp.getBody());
        verify(qualificationService).getAllQualifications();
    }

    @Test
    void batchCreateQualifications_ReturnsCreatedList() {
        List<QualificationDto> input = List.of(dto1, dto2);
        when(qualificationService.batchCreateQualifications(input)).thenReturn(input);

        ResponseEntity<List<QualificationDto>> resp = controller.batchCreateQualifications(input);

        assertEquals(201, resp.getStatusCodeValue());
        assertSame(input, resp.getBody());
        verify(qualificationService).batchCreateQualifications(input);
    }

    @Test
    void getQualificationById_ReturnsDto() {
        when(qualificationService.getQualificationById(5L)).thenReturn(dto1);

        ResponseEntity<QualificationDto> resp = controller.getQualificationById(5L);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto1, resp.getBody());
        verify(qualificationService).getQualificationById(5L);
    }

    @Test
    void addQualification_ReturnsCreatedDto() {
        when(qualificationService.addQualification(dto2)).thenReturn(dto2);

        ResponseEntity<QualificationDto> resp = controller.addQualification(dto2);

        assertEquals(201, resp.getStatusCodeValue());
        assertSame(dto2, resp.getBody());
        verify(qualificationService).addQualification(dto2);
    }

    @Test
    void updateQualification_ReturnsUpdatedDto() {
        when(qualificationService.updateQualification(7L, dto1)).thenReturn(dto1);

        ResponseEntity<QualificationDto> resp = controller.updateQualification(7L, dto1);

        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto1, resp.getBody());
        verify(qualificationService).updateQualification(7L, dto1);
    }

    @Test
    void deleteQualification_Success() {
        doNothing().when(qualificationService).deleteQualification(8L);

        ResponseEntity<Void> resp = controller.deleteQualification(8L);

        assertEquals(204, resp.getStatusCodeValue());
        assertNull(resp.getBody());
        verify(qualificationService).deleteQualification(8L);
    }

    @Test
    void deleteQualification_NotFound() {
        doThrow(new RuntimeException("not found")).when(qualificationService).deleteQualification(9L);

        ResponseEntity<Void> resp = controller.deleteQualification(9L);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
        verify(qualificationService).deleteQualification(9L);
    }
}
