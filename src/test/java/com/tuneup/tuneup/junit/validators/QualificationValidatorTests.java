package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.common.OperationType;
import com.tuneup.tuneup.qualifications.dtos.QualificationDto;
import com.tuneup.tuneup.qualifications.entities.Qualification;
import com.tuneup.tuneup.qualifications.enums.QualificationName;
import com.tuneup.tuneup.qualifications.repositories.QualificationRepository;
import com.tuneup.tuneup.qualifications.services.QualificationValidator;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QualificationValidatorTests {

    @Mock
    private QualificationRepository repo;

    @InjectMocks
    private QualificationValidator validator;

    private QualificationDto dto;
    private Qualification existing;

    @BeforeEach
    void setUp() {
        dto = new QualificationDto();
        existing = new Qualification();
        existing.setId(123L);
    }

    @Test
    void validateQualification_CreateValid_DoesNotThrow() {
        dto.setName(QualificationName.values()[0]);
        assertDoesNotThrow(() ->
                validator.validateQualification(OperationType.CREATE, dto)
        );
    }

    @Test
    void validateQualification_CreateNullDto_Throws() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateQualification(OperationType.CREATE, null)
        );
        assertTrue(ex.getMessage().contains("Qualification cannot be null"));
    }

    @Test
    void validateQualification_CreateNullName_Throws() {
        dto.setName(null);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateQualification(OperationType.CREATE, dto)
        );
        assertTrue(ex.getMessage().contains("Qualification name cannot be null"));
    }

    @Test
    void validateQualification_UpdateValid_ReturnsExisting() {
        dto.setId(123L);
        dto.setName(QualificationName.values()[0]);
        when(repo.findById(123L)).thenReturn(Optional.of(existing));

        Qualification result = validator.validateQualification(OperationType.UPDATE, dto);
        assertSame(existing, result);
    }

    @Test
    void validateQualification_UpdateNullId_Throws() {
        dto.setId(null);
        dto.setName(QualificationName.values()[0]);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateQualification(OperationType.UPDATE, dto)
        );
        assertTrue(ex.getMessage().contains("Qualification ID cannot be null for update"));
    }

    @Test
    void validateQualification_UpdateNotFound_Throws() {
        dto.setId(999L);
        dto.setName(QualificationName.values()[0]);
        when(repo.findById(999L)).thenReturn(Optional.empty());

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateQualification(OperationType.UPDATE, dto)
        );
        assertEquals("Qualification not found with id 999", ex.getMessage());
    }

    @Test
    void validateQualification_UpdateNullName_Throws() {
        dto.setId(123L);
        dto.setName(null);
        when(repo.findById(123L)).thenReturn(Optional.of(existing));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateQualification(OperationType.UPDATE, dto)
        );
        assertTrue(ex.getMessage().contains("Qualification name cannot be null"));
    }

    @Test
    void validateAndFetchById_NullId_Throws() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateAndFetchById(null)
        );
        assertEquals("Qualification ID cannot be null", ex.getMessage());
    }

    @Test
    void validateAndFetchById_NotFound_Throws() {
        when(repo.findById(50L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateAndFetchById(50L)
        );
        assertEquals("Qualification not found with id 50", ex.getMessage());
    }

    @Test
    void validateAndFetchById_Found_ReturnsEntity() {
        when(repo.findById(42L)).thenReturn(Optional.of(existing));
        Qualification result = validator.validateAndFetchById(42L);
        assertSame(existing, result);
    }

    @Test
    void validateDeletion_Exists_Throws() {
        when(repo.existsById(77L)).thenReturn(true);
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> validator.validateDeletion(77L)
        );
        assertEquals("Failed to delete qualification with id 77", ex.getMessage());
    }

    @Test
    void validateDeletion_NotExists_DoesNotThrow() {
        when(repo.existsById(88L)).thenReturn(false);
        assertDoesNotThrow(() -> validator.validateDeletion(88L));
    }
}
