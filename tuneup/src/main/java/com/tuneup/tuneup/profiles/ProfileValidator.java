package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProfileValidator {

    private final InstrumentRepository instrumentRepository;

    public ProfileValidator(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public void validatorProfileDto(ProfileDto profileDto) {
        validateDisplayName(profileDto.getDisplayName());
        validateInstruments(profileDto.getInstruments());

    }

    private void validateInstruments(Set<InstrumentDto> instruments) {
        if (instruments == null || instruments.isEmpty()) {
            throw new ValidationException("The instrument list is empty");
        }
        for (InstrumentDto instrument : instruments) {
           if(! instrumentRepository.existsById(instrument.getId())){
               throw new ValidationException("The instrument with id " + instrument.getId() + " does not exist");
           };
        }
    }

    private void validateDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new ValidationException("Display name cannot be empty");
        }
    }
}
