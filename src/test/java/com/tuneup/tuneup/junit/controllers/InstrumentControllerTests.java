package com.tuneup.tuneup.junit.controllers;


import com.tuneup.tuneup.Instruments.InstrumentController;
import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.Instruments.InstrumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstrumentControllerTests {

    @Mock
    private InstrumentService instrumentService;

    @InjectMocks
    private InstrumentController instrumentController;

    @Test
    void testGetAllInstrumentsReturnsSet() {
        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setId(1L);
        instrumentDto.setName("Guitar");

        Set<InstrumentDto> instruments = Collections.singleton(instrumentDto);
        when(instrumentService.findall()).thenReturn(instruments);

        ResponseEntity<Set<InstrumentDto>> response = instrumentController.getAllInstruments();
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCreateInstrumentReturnDto() {
        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setId(1L);
        instrumentDto.setName("Piano");
        when(instrumentService.createInstrument(instrumentDto)).thenReturn(instrumentDto);

        ResponseEntity<InstrumentDto> response = instrumentController.createInstrument(instrumentDto);
        assertNotNull(response.getBody());
        assertEquals(instrumentDto, response.getBody());
    }
}
