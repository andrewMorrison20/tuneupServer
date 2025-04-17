package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.reviews.dtos.ReviewDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import com.tuneup.tuneup.reviews.validators.ReviewValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewValidatorTests {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ReviewValidator validator;

    private ReviewDto dto;

    @BeforeEach
    void setUp() {
        dto = new ReviewDto();
        dto.setProfileId(1L);
        dto.setReviewerProfileId(2L);
    }

    @Test
    void validateReviewDto_DoesNotThrow_WhenBothProfilesExist() {
        when(profileRepository.existsById(1L)).thenReturn(true);
        when(profileRepository.existsById(2L)).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateReviewDto(dto));

        verify(profileRepository).existsById(1L);
        verify(profileRepository).existsById(2L);
    }

    @Test
    void validateReviewDto_Throws_WhenProfileIdDoesNotExist() {
        when(profileRepository.existsById(1L)).thenReturn(false);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateReviewDto(dto)
        );
        assertEquals("Profile does not exist", ex.getMessage());

        verify(profileRepository).existsById(1L);
        // second call never happens
        verify(profileRepository, never()).existsById(2L);
    }

    @Test
    void validateReviewDto_Throws_WhenReviewerProfileIdDoesNotExist() {
        when(profileRepository.existsById(1L)).thenReturn(true);
        when(profileRepository.existsById(2L)).thenReturn(false);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validator.validateReviewDto(dto)
        );
        assertEquals("Profile does not exist", ex.getMessage());

        verify(profileRepository).existsById(1L);
        verify(profileRepository).existsById(2L);
    }
}
