package com.tuneup.tuneup.Instruments;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InstrumentService {
    private final InstrumentValidator instrumentValidator;

    public InstrumentService(InstrumentValidator instrumentValidator) {
        this.instrumentValidator = instrumentValidator;
    }

    public static void findall() {

    }

    /**
     * Included for the admin console to add new instruments
     * @param instrumentDto instrument to create/ add to db
     * @return instrument that was successfully created. Else throw exception
     */
    @Transactional
    public InstrumentDto createInstrument(InstrumentDto instrumentDto){
        instrumentValidator.validateInstrumentDto(instrumentDto);
        return instrumentDto;

    }
}
