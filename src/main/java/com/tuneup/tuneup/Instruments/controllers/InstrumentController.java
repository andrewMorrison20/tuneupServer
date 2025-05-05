package com.tuneup.tuneup.Instruments.controllers;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.services.InstrumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }


    /**
     * Get all existing instruments
     * @return Set InstrumentDto
     */
    @GetMapping
    public ResponseEntity<Set<InstrumentDto>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.findall());
    }

    /**
     * Create a new Instrument
     * @param instrumentDto instrument to create
     * @return newly created instrument
     */
    @PostMapping
    public ResponseEntity<InstrumentDto> createInstrument(@RequestBody InstrumentDto instrumentDto) {
        InstrumentDto createdInstrument = instrumentService.createInstrument(instrumentDto);
        return ResponseEntity.ok(createdInstrument);
    }
}
