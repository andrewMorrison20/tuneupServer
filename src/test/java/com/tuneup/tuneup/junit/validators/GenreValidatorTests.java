package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.genres.dtos.GenreDto;
import com.tuneup.tuneup.genres.repositories.GenreRepository;
import com.tuneup.tuneup.genres.services.GenreValidator;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreValidatorTests {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreValidator genreValidator;

    private GenreDto genreDto;

    @BeforeEach
    void setUp() {
        genreDto = new GenreDto();
        genreDto.setName("Rock");
    }

    @Test
    void validateGenre_WhenGenreDoesNotExist_ShouldNotThrowException() {
        when(genreRepository.existsByName(genreDto.getName())).thenReturn(false);
        assertDoesNotThrow(() -> genreValidator.validateGenre(genreDto));
    }

    @Test
    void validateGenre_WhenGenreExists_ShouldThrowValidationException() {
        when(genreRepository.existsByName(genreDto.getName())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class, () -> genreValidator.validateGenre(genreDto));
        assertEquals("Genre Already Exsits!", exception.getMessage());
    }
}
