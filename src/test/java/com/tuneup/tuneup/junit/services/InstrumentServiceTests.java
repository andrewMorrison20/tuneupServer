package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.entities.Instrument;
import com.tuneup.tuneup.Instruments.mappers.InstrumentMapper;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.Instruments.services.InstrumentService;
import com.tuneup.tuneup.Instruments.services.InstrumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentServiceTests {

    @Mock
    private InstrumentValidator instrumentValidator;

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private InstrumentMapper instrumentMapper;

    @InjectMocks
    private InstrumentService instrumentService;

    private Instrument instrument;
    private InstrumentDto instrumentDto;

    @BeforeEach
    void setUp() {
        // Create a dummy Instrument and InstrumentDto for testing
        instrument = new Instrument();
        instrument.setId(1L);
        instrument.setName("Guitar");

        instrumentDto = new InstrumentDto();
        instrumentDto.setId(1L);
        instrumentDto.setName("Guitar");
    }

    @Test
    void findAll_shouldReturnSetOfInstrumentDtos() {

        Instrument instrument1 = new Instrument();
        Instrument instrument2 = new Instrument();

        InstrumentDto dto1 = new InstrumentDto();
        InstrumentDto dto2 = new InstrumentDto();

        when(instrumentRepository.findAll()).thenReturn(List.of(instrument1, instrument2));
        when(instrumentMapper.toInstrumentDto(instrument1)).thenReturn(dto1);
        when(instrumentMapper.toInstrumentDto(instrument2)).thenReturn(dto2);

        Set<InstrumentDto> result = instrumentService.findall();

        verify(instrumentRepository).findAll();
        verify(instrumentMapper).toInstrumentDto(instrument1);
        verify(instrumentMapper).toInstrumentDto(instrument2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));
    }

    @Test
    void createInstrument_shouldValidateAndSaveInstrument() {
        InstrumentDto inputDto = new InstrumentDto();
        Instrument mockInstrument = new Instrument();
        Instrument savedInstrument = new Instrument();
        InstrumentDto expectedDto = new InstrumentDto();

        when(instrumentMapper.toInstrument(inputDto)).thenReturn(mockInstrument);
        when(instrumentRepository.save(mockInstrument)).thenReturn(savedInstrument);
        when(instrumentMapper.toInstrumentDto(savedInstrument)).thenReturn(expectedDto);


        InstrumentDto result = instrumentService.createInstrument(inputDto);

        verify(instrumentValidator).validateInstrumentDto(inputDto);
        verify(instrumentMapper).toInstrument(inputDto);
        verify(instrumentRepository).save(mockInstrument);
        verify(instrumentMapper).toInstrumentDto(savedInstrument);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void getInstrumentById_ShouldReturnInstrumentDto() {

        when(instrumentValidator.fetchAndValidateById(1L)).thenReturn(instrument);
        when(instrumentMapper.toInstrumentDto(instrument)).thenReturn(instrumentDto);

        InstrumentDto result = instrumentService.getInstrumentById(1L);

        assertNotNull(result);
        assertEquals(instrumentDto.getId(), result.getId());
        assertEquals(instrumentDto.getName(), result.getName());
        verify(instrumentValidator, times(1)).fetchAndValidateById(1L);
        verify(instrumentMapper, times(1)).toInstrumentDto(instrument);
    }

    @Test
    void getInstrumentByIdInternal_ShouldReturnInstrumentEntity() {
        // Arrange: Simulate the validator returning a valid instrument.
        when(instrumentValidator.fetchAndValidateById(1L)).thenReturn(instrument);

        // Act: Call the internal retrieval method.
        Instrument result = instrumentService.getInstrumentByIdInternal(1L);

        // Assert: Verify that the returned instrument is the same.
        assertNotNull(result);
        assertEquals(instrument.getId(), result.getId());
        assertEquals(instrument.getName(), result.getName());
        verify(instrumentValidator, times(1)).fetchAndValidateById(1L);
    }
}
