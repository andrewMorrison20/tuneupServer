package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.availability.entities.LessonRequest;
import com.tuneup.tuneup.availability.repositories.LessonRequestRepository;
import com.tuneup.tuneup.availability.validators.LessonRequestValidator;
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
class LessonRequestValidatorTests {

    @Mock
    private LessonRequestRepository repo;

    @InjectMocks
    private LessonRequestValidator validator;

    private LessonRequest request;

    @BeforeEach
    void setUp() {
        request = new LessonRequest();
        request.setId(123L);
    }

    @Test
    void validateDuplicateRequest_DoesNotThrow_WhenNotExists() {
        when(repo.existsByStudentIdAndAvailabilityId(10L, 20L)).thenReturn(false);
        assertDoesNotThrow(() -> validator.validateDuplicateRequest(10L, 20L));
        verify(repo).existsByStudentIdAndAvailabilityId(10L, 20L);
    }

    @Test
    void validateDuplicateRequest_Throws_WhenExists() {
        when(repo.existsByStudentIdAndAvailabilityId(10L, 20L)).thenReturn(true);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateDuplicateRequest(10L, 20L)
        );
        assertEquals("Request already exists for Student and availability: 20", ex.getMessage());
    }

    @Test
    void fetchAndValidateById_ReturnsEntity_WhenFound() {
        when(repo.findById(123L)).thenReturn(Optional.of(request));
        LessonRequest result = validator.fetchAndValidateById(123L);
        assertSame(request, result);
        verify(repo).findById(123L);
    }

    @Test
    void fetchAndValidateById_Throws_WhenNotFound() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.fetchAndValidateById(999L)
        );
        assertEquals("Lesson request not found for id: 999", ex.getMessage());
    }
}
