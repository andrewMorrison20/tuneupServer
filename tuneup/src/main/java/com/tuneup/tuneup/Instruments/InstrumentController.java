package com.tuneup.tuneup.Instruments;

import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
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


    @GetMapping
    public ResponseEntity<Set<InstrumentDto>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.findall());
    }

    @PostMapping
    public ResponseEntity<InstrumentDto> createInstrument(@RequestBody InstrumentDto instrumentDto) {
        InstrumentDto createdInstrument = instrumentService.createInstrument(instrumentDto);
        return ResponseEntity.ok(createdInstrument);
    }
}
