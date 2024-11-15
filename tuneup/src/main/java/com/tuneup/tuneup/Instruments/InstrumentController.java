package com.tuneup.tuneup.Instruments;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    @GetMapping
    public ResponseEntity<String> getAllInstruments() {
        InstrumentService.findall();
        return ResponseEntity.ok("All instruments");
    }
}
