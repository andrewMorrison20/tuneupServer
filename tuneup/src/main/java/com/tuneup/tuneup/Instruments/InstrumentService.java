package com.tuneup.tuneup.Instruments;

import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstrumentService {
    private final InstrumentValidator instrumentValidator;
    private final InstrumentRepository instrumentRepository;
    private final InstrumentMapper instrumentMapper;

    public InstrumentService(InstrumentValidator instrumentValidator, InstrumentRepository instrumentRepository, InstrumentMapper instrumentMapper) {
        this.instrumentValidator = instrumentValidator;
        this.instrumentRepository = instrumentRepository;
        this.instrumentMapper = instrumentMapper;
    }

    public Set<InstrumentDto> findall() {
       List<Instrument> instruments = instrumentRepository.findAll();
        return instruments.stream()
                .map(instrumentMapper :: toInstrumentDto)
                .collect(Collectors.toSet());
    }

    /**
     * Included for the admin console to add new instruments
     * @param instrumentDto instrument to create/ add to db
     * @return instrument that was successfully created. Else throw exception
     */
    @Transactional
    public InstrumentDto createInstrument(InstrumentDto instrumentDto){
        instrumentValidator.validateInstrumentDto(instrumentDto);
        Instrument instrument = instrumentMapper.toInstrument(instrumentDto);
        Instrument createdInstrument = instrumentRepository.save(instrument);
        return instrumentMapper.toInstrumentDto(createdInstrument);
    }
}