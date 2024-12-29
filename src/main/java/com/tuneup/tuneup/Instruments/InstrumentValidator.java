package com.tuneup.tuneup.Instruments;

import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class InstrumentValidator {


    private final InstrumentRepository instrumentRepository;

    public InstrumentValidator(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public void validateInstrumentDto(InstrumentDto instrumentDto) {
        validateInstrumentName(instrumentDto.getName());
    }

    private void validateInstrumentName(String name) {
        if(name == null || name.isEmpty()){
            throw new ValidationException("Instrument name must be non null");
        }
        if(instrumentRepository.existsByName(name)){
            throw new ValidationException("Instrument already exists");
        }
    }
}
