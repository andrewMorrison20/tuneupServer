package com.tuneup.tuneup.Instruments;

import org.springframework.stereotype.Component;

@Component
public class InstrumentValidator {


    public void validateInstrumentDto(InstrumentDto instrumentDto) {
        validateInstrumentName(instrumentDto.getName());
    }
}
