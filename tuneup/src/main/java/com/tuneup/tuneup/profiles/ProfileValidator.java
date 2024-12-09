package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.genres.GenreDto;
import com.tuneup.tuneup.genres.GenreRepository;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProfileValidator {

    private final InstrumentRepository instrumentRepository;
    private final GenreRepository genreRepository;

    public ProfileValidator(InstrumentRepository instrumentRepository, GenreRepository genreRepository) {
        this.instrumentRepository = instrumentRepository;
        this.genreRepository = genreRepository;
    }

    public void validatorProfileDto(ProfileDto profileDto) {
        validateDisplayName(profileDto.getDisplayName());
        validateInstruments(profileDto.getInstruments());
        validateGenres(profileDto.getGenres());

    }

    private void validateGenres(Set<GenreDto> genres) {
        if(genres!= null) {
            for (GenreDto genre : genres) {
                if(! genreRepository.existsById(genre.getId())){
                    throw new ValidationException("Genre with id " + genre.getId() + " does not exist");
                }
            }
        }
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
