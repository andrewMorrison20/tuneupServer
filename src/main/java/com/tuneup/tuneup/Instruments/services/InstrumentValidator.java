package com.tuneup.tuneup.Instruments.services;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.entities.Instrument;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class InstrumentValidator {


    private final InstrumentRepository instrumentRepository;

    public InstrumentValidator(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    /**
     * Validate an instrument dto prior to creating entity.
     * @param instrumentDto the instrument to validate.
     */
    public void validateInstrumentDto(InstrumentDto instrumentDto) {
        validateInstrumentName(instrumentDto.getName());
    }

    /**
     * Check instrument is valid and not duplicate
     * @param name nameof instrument
     */
    private void validateInstrumentName(String name) {
        if(name == null || name.isEmpty()){
            throw new ValidationException("Instrument name must be non null");
        }
        if(instrumentRepository.existsByName(name)){
            throw new ValidationException("Instrument already exists");
        }
    }

    /**
     * Retrieve an instrument by its id else throw. Carried out here instead of service layer to centralise validation exception handling
     * @param id id of the instrument to retrieve
     * @return the existing instrument
     */
    public Instrument fetchAndValidateById(Long id){
            return instrumentRepository.findById(id)
                    .orElseThrow(() -> new ValidationException("Instrument with the given ID not found"));
        }
}
