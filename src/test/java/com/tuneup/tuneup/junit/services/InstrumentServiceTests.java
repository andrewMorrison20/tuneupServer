package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.entities.Instrument;
import com.tuneup.tuneup.Instruments.mappers.InstrumentMapper;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.Instruments.services.InstrumentService;
import com.tuneup.tuneup.Instruments.services.InstrumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InstrumentServiceTests {

    @Mock
    private InstrumentValidator instrumentValidator;

    @Mock
    private InstrumentRepository instrumentRepository;

    @Mock
    private InstrumentMapper instrumentMapper;

    @InjectMocks
    private InstrumentService instrumentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
}
