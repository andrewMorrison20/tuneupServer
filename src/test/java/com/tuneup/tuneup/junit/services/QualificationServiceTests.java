package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.common.OperationType;
import com.tuneup.tuneup.qualifications.dtos.QualificationDto;
import com.tuneup.tuneup.qualifications.entities.Qualification;
import com.tuneup.tuneup.qualifications.enums.QualificationName;
import com.tuneup.tuneup.qualifications.mappers.QualificationMapper;
import com.tuneup.tuneup.qualifications.repositories.QualificationRepository;
import com.tuneup.tuneup.qualifications.services.QualificationService;
import com.tuneup.tuneup.qualifications.services.QualificationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class QualificationServiceTests {

    @Mock
    private QualificationRepository repo;

    @Mock
    private QualificationMapper mapper;

    @Mock
    private QualificationValidator validator;

    @InjectMocks
    private QualificationService service;

    private Qualification entity1, entity2;
    private QualificationDto dto1, dto2;

    @BeforeEach
    void setUp() {
        entity1 = new Qualification();
        entity1.setId(1L);
        entity2 = new Qualification();
        entity2.setId(2L);

        dto1 = new QualificationDto();
        dto1.setId(1L);
        dto2 = new QualificationDto();
        dto2.setId(2L);
    }

    @Test
    void getAllQualifications_ReturnsDtoSet() {
        when(repo.findAll()).thenReturn(List.of(entity1, entity2));
        when(mapper.toQualificationDto(entity1)).thenReturn(dto1);
        when(mapper.toQualificationDto(entity2)).thenReturn(dto2);

        Set<QualificationDto> result = service.getAllQualifications();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Set.of(dto1, dto2)));
        verify(repo).findAll();
    }

    @Test
    void getQualificationById_ReturnsDto() {
        when(validator.validateAndFetchById(10L)).thenReturn(entity1);
        when(mapper.toQualificationDto(entity1)).thenReturn(dto1);

        QualificationDto result = service.getQualificationById(10L);

        assertSame(dto1, result);
        verify(validator).validateAndFetchById(10L);
        verify(mapper).toQualificationDto(entity1);
    }

    @Test
    void addQualification_Success() {
        QualificationDto input = new QualificationDto();
        when(validator.validateQualification(OperationType.CREATE, input)).thenReturn(null);
        Qualification toSave = new Qualification();
        when(mapper.toQualification(input)).thenReturn(toSave);
        when(repo.save(toSave)).thenReturn(entity1);
        when(mapper.toQualificationDto(entity1)).thenReturn(dto1);

        QualificationDto result = service.addQualification(input);

        assertSame(dto1, result);
        InOrder inOrder = inOrder(validator, mapper, repo);
        inOrder.verify(validator).validateQualification(OperationType.CREATE, input);
        inOrder.verify(mapper).toQualification(input);
        inOrder.verify(repo).save(toSave);
        inOrder.verify(mapper).toQualificationDto(entity1);
    }

    @Test
    void batchCreateQualifications_Success() {
        QualificationDto d1 = new QualificationDto();
        QualificationDto d2 = new QualificationDto();
        Qualification e1 = new Qualification(), e2 = new Qualification();
        when(validator.validateQualification(OperationType.CREATE, d1)).thenReturn(null);
        when(validator.validateQualification(OperationType.CREATE, d2)).thenReturn(null);
        when(mapper.toQualification(d1)).thenReturn(e1);
        when(mapper.toQualification(d2)).thenReturn(e2);
        when(repo.saveAll(List.of(e1, e2))).thenReturn(List.of(entity1, entity2));
        when(mapper.toQualificationDto(entity1)).thenReturn(dto1);
        when(mapper.toQualificationDto(entity2)).thenReturn(dto2);

        List<QualificationDto> result = service.batchCreateQualifications(List.of(d1, d2));

        assertEquals(2, result.size());
        assertEquals(List.of(dto1, dto2), result);
        verify(repo).saveAll(List.of(e1, e2));
    }

    @Test
    void updateQualification_Success() {
        QualificationDto updateDto = new QualificationDto();
        updateDto.setId(5L);
        updateDto.setName(QualificationName.GRADE_1);
        Qualification existing = new Qualification();
        existing.setName(null);

        when(validator.validateQualification(OperationType.UPDATE, updateDto)).thenReturn(existing);
        when(repo.save(existing)).thenReturn(entity1);
        when(mapper.toQualificationDto(entity1)).thenReturn(dto1);

        QualificationDto result = service.updateQualification(5L, updateDto);

        assertSame(dto1, result);
        assertEquals(updateDto.getName(), existing.getName());
        verify(repo).save(existing);
    }

    @Test
    void deleteQualification_Success() {
        when(validator.validateAndFetchById(7L)).thenReturn(entity1);
        doNothing().when(repo).delete(entity1);
        doNothing().when(validator).validateDeletion(7L);

        assertDoesNotThrow(() -> service.deleteQualification(7L));

        verify(repo).delete(entity1);
        verify(validator).validateDeletion(7L);
    }

    @Test
    void getQualificationByIdInternal_ReturnsEntity() {
        when(validator.validateAndFetchById(8L)).thenReturn(entity2);

        Qualification result = service.getQualificationByIdInternal(8L);

        assertSame(entity2, result);
        verify(validator).validateAndFetchById(8L);
    }
}
