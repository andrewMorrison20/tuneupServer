package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.genres.GenreDto;
import com.tuneup.tuneup.genres.GenreRepository;
import com.tuneup.tuneup.profiles.Profile;
import com.tuneup.tuneup.profiles.ProfileValidator;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileValidatorTest {

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileValidator profileValidator;

    private ProfileDto profileDto;
    private GenreDto genreDto;
    private InstrumentDto instrumentDto;

    @BeforeEach
    void setUp() {
        instrumentDto = new InstrumentDto();
        instrumentDto.setName("Guitar");
        instrumentDto.setId(1L);

        genreDto = new GenreDto();
        genreDto.setId(1L);
        genreDto.setName("Rock");
        profileDto = new ProfileDto();
        profileDto.setDisplayName("Test User");
        profileDto.setInstruments(Set.of(instrumentDto));
        profileDto.setGenres(Set.of(genreDto));
    }

    @Test
    void validatorProfileDto_ShouldNotThrowExceptionForValidProfile() {
        when(instrumentRepository.existsById(anyLong())).thenReturn(true);
        when(genreRepository.existsById(anyLong())).thenReturn(true);
        assertDoesNotThrow(() -> profileValidator.validatorProfileDto(profileDto));
    }

    @Test
    void validatorProfileDto_ShouldThrowExceptionForEmptyDisplayName() {
        profileDto.setDisplayName("");
        assertThrows(ValidationException.class, () -> profileValidator.validatorProfileDto(profileDto));
    }

    @Test
    void validatorProfileDto_ShouldThrowExceptionForInvalidInstrument() {
        when(instrumentRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ValidationException.class, () -> profileValidator.validatorProfileDto(profileDto));
    }

    @Test
    void testValidateProfileId_ValidId(){
        when(profileRepository.existsById(any())).thenReturn(false);
        assertThrows(ValidationException.class, () -> profileValidator.validateProfileId(1L));
    }

    @Test
    void testExistsByProfileId_ValidId() {
        when(profileRepository.existsById(any())).thenReturn(true);
        assertDoesNotThrow(() -> profileValidator.existsById(1L));
    }

    @Test
    void validatorProfileDto_ShouldThrowExceptionForInvalidGenre() {
        when(genreRepository.existsById(anyLong())).thenReturn(false);
        when(instrumentRepository.existsById(any())).thenReturn(true);
        assertThrows(ValidationException.class, () -> profileValidator.validatorProfileDto(profileDto));
    }

    @Test
    void existsByUser_ShouldThrowExceptionIfUserDoesNotExist() {
        when(profileRepository.existsByAppUserId(anyLong())).thenReturn(false);
        assertThrows(ValidationException.class, () -> profileValidator.existsByUser(1L));
    }

    @Test
    void fetchById_ShouldReturnProfileIfExists() {
        Profile profile = new Profile();
        when(profileRepository.findById(anyLong())).thenReturn(Optional.of(profile));

        Profile result = profileValidator.fetchById(1L);
        assertNotNull(result);
    }

    @Test
    void fetchById_ShouldThrowExceptionIfProfileDoesNotExist() {
        when(profileRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> profileValidator.fetchById(1L));
    }
}
