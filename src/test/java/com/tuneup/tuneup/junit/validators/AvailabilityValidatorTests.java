package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.availability.dtos.AvailabilityDto;
import com.tuneup.tuneup.availability.entities.Availability;
import com.tuneup.tuneup.availability.repositories.AvailabilityRepository;
import com.tuneup.tuneup.availability.validators.AvailabilityValidator;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityValidatorTests {

    @Mock
    private AvailabilityRepository repo;

    @InjectMocks
    private AvailabilityValidator validator;

    private AvailabilityDto dto;
    private LocalDateTime now, later;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2025, 4, 17, 9, 0);
        later = LocalDateTime.of(2025, 4, 17, 10, 0);
        dto = new AvailabilityDto();
        dto.setProfileId(123L);
        dto.setStartTime(now);
        dto.setEndTime(later);
    }

    @Test
    void validateDto_NoOverlap_DoesNotThrow() {
        when(repo.findOverlappingAvailabilities(123L, now, later))
                .thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> validator.validateAvailabilityDto(dto));
        verify(repo).findOverlappingAvailabilities(123L, now, later);
    }

    @Test
    void validateDto_NullTimes_Throws() {
        dto.setStartTime(null);
        ValidationException ex1 = assertThrows(
                ValidationException.class,
                () -> validator.validateAvailabilityDto(dto)
        );
        assertTrue(ex1.getMessage().contains("must not be null"));

        dto.setStartTime(now);
        dto.setEndTime(null);
        ValidationException ex2 = assertThrows(
                ValidationException.class,
                () -> validator.validateAvailabilityDto(dto)
        );
        assertTrue(ex2.getMessage().contains("must not be null"));
    }

    @Test
    void validateDto_StartAfterEnd_Throws() {
        dto.setStartTime(later);
        dto.setEndTime(now);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateAvailabilityDto(dto)
        );
        assertTrue(ex.getMessage().contains("before End Time"));
    }

    @Test
    void validateDto_ZeroLength_Throws() {
        dto.setStartTime(now);
        dto.setEndTime(now);
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateAvailabilityDto(dto)
        );
        assertTrue(ex.getMessage().contains("cannot be the same"));
    }

    @Test
    void validateDto_WithOverlap_Throws() {
        Availability overlap = new Availability();
        overlap.setId(1L);
        when(repo.findOverlappingAvailabilities(123L, now, later))
                .thenReturn(List.of(overlap));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateAvailabilityDto(dto)
        );
        assertTrue(ex.getMessage().contains("overlaps"));
    }

    @Test
    void fetchAndValidateById_Found() {
        Availability a = new Availability();
        a.setId(77L);
        when(repo.findById(77L)).thenReturn(Optional.of(a));
        Availability result = validator.fetchAndValidateById(77L);
        assertSame(a, result);
    }

    @Test
    void fetchAndValidateById_NotFound_Throws() {
        when(repo.findById(88L)).thenReturn(Optional.empty());
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.fetchAndValidateById(88L)
        );
        assertEquals("No existing Availability slot with id: 88", ex.getMessage());
    }
}
