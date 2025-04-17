package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.tuitions.dtos.TuitionDto;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import com.tuneup.tuneup.tuitions.repositories.TuitionRepository;
import com.tuneup.tuneup.tuitions.validators.TuitionValidator;
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
class TuitionValidatorTests {

    @Mock
    private TuitionRepository tuitionRepository;

    @InjectMocks
    private TuitionValidator validator;

    private TuitionDto dto;
    private Tuition tuition;

    @BeforeEach
    void setUp() {
        dto = new TuitionDto();
        dto.setTutorProfileId(5L);
        dto.setStudentProfileId(7L);

        tuition = new Tuition();
        tuition.setId(99L);
    }

    @Test
    void validateDto_DoesNotThrow_WhenNoExistingTuition() {
        when(tuitionRepository.existsByTutorIdAndStudentId(5L, 7L)).thenReturn(false);
        assertDoesNotThrow(() -> validator.validateDto(dto));
        verify(tuitionRepository).existsByTutorIdAndStudentId(5L, 7L);
    }

    @Test
    void validateDto_Throws_WhenTuitionExists() {
        when(tuitionRepository.existsByTutorIdAndStudentId(5L, 7L)).thenReturn(true);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateDto(dto));
        assertEquals("Tuition for these profiles already exists!", ex.getMessage());
    }

    @Test
    void existsByTutorStudentId_ReturnsTrue() {
        when(tuitionRepository.existsByTutorIdAndStudentId(5L, 7L)).thenReturn(true);
        assertTrue(validator.existsByTutorStudentId(5L, 7L));
        verify(tuitionRepository).existsByTutorIdAndStudentId(5L, 7L);
    }

    @Test
    void existsByTutorStudentId_ReturnsFalse() {
        when(tuitionRepository.existsByTutorIdAndStudentId(5L, 7L)).thenReturn(false);
        assertFalse(validator.existsByTutorStudentId(5L, 7L));
    }

    @Test
    void fetchAndValidateById_ReturnsTuition_WhenFound() {
        when(tuitionRepository.findById(42L)).thenReturn(Optional.of(tuition));
        Tuition result = validator.fetchAndValidateById(42L);
        assertSame(tuition, result);
    }

    @Test
    void fetchAndValidateById_Throws_WhenNotFound() {
        when(tuitionRepository.findById(42L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.fetchAndValidateById(42L));
        assertEquals("No existing tuition for id: 42", ex.getMessage());
    }

    @Test
    void fetchAndValidateTuitionByIds_ReturnsTuition_WhenFound() {
        when(tuitionRepository.findByStudentIdAndTutorId(7L, 5L)).thenReturn(Optional.of(tuition));
        Tuition result = validator.fetchAndValidateTuitionByIds(7L, 5L);
        assertSame(tuition, result);
    }

    @Test
    void fetchAndValidateTuitionByIds_Throws_WhenNotFound() {
        when(tuitionRepository.findByStudentIdAndTutorId(7L, 5L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.fetchAndValidateTuitionByIds(7L, 5L));
        assertEquals("No tuition exists for the given student and tutor", ex.getMessage());
    }
}

