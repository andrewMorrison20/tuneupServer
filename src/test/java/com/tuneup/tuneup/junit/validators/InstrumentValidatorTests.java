package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.entities.Instrument;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.Instruments.services.InstrumentValidator;
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
class InstrumentValidatorTests {

    @Mock
    private InstrumentRepository instrumentRepository;

    @InjectMocks
    private InstrumentValidator instrumentValidator;

    private InstrumentDto validDto;
    private InstrumentDto nullNameDto;
    private InstrumentDto emptyNameDto;

    @BeforeEach
    void setUp() {
        validDto = new InstrumentDto();
        validDto.setName("Guitar");

        nullNameDto = new InstrumentDto();
        nullNameDto.setName(null);

        emptyNameDto = new InstrumentDto();
        emptyNameDto.setName("");
    }

    @Test
    void validateInstrumentDto_WithValidName_ShouldNotThrow() {
        when(instrumentRepository.existsByName("Guitar")).thenReturn(false);
        assertDoesNotThrow(() -> instrumentValidator.validateInstrumentDto(validDto));
    }

    @Test
    void validateInstrumentDto_WithNullName_ShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> instrumentValidator.validateInstrumentDto(nullNameDto));
        assertEquals("Instrument name must be non null", exception.getMessage());
    }

    @Test
    void validateInstrumentDto_WithEmptyName_ShouldThrowValidationException() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> instrumentValidator.validateInstrumentDto(emptyNameDto));
        assertEquals("Instrument name must be non null", exception.getMessage());
    }

    @Test
    void validateInstrumentDto_WhenInstrumentAlreadyExists_ShouldThrowValidationException() {
        when(instrumentRepository.existsByName("Guitar")).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> instrumentValidator.validateInstrumentDto(validDto));
        assertEquals("Instrument already exists", exception.getMessage());
    }

    @Test
    void fetchAndValidateById_WhenInstrumentExists_ShouldReturnInstrument() {
        Instrument instrument = new Instrument();
        instrument.setId(1L);
        instrument.setName("Piano");
        when(instrumentRepository.findById(1L)).thenReturn(Optional.of(instrument));

        Instrument result = instrumentValidator.fetchAndValidateById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Piano", result.getName());
    }

    @Test
    void fetchAndValidateById_WhenInstrumentDoesNotExist_ShouldThrowValidationException() {
        when(instrumentRepository.findById(2L)).thenReturn(Optional.empty());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> instrumentValidator.fetchAndValidateById(2L));
        assertEquals("Instrument with the given ID not found", exception.getMessage());
    }
}
