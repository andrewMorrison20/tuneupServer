package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.genres.GenreDto;
import com.tuneup.tuneup.genres.GenreRepository;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.repositories.ProfileRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ProfileValidator {

    private final InstrumentRepository instrumentRepository;
    private final GenreRepository genreRepository;
    private final ProfileRepository profileRepository;

    public ProfileValidator(InstrumentRepository instrumentRepository, GenreRepository genreRepository, ProfileRepository profileRepository) {
        this.instrumentRepository = instrumentRepository;
        this.genreRepository = genreRepository;
        this.profileRepository = profileRepository;
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

    public void existsByUser(Long userId) {
        if(!profileRepository.existsByAppUserId(userId)){
            throw new ValidationException("Profile with id " + userId + " does not exist");
        };

    }

    public Profile fetchById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ValidationException("Profile with id " + profileId + " does not exist"));
    }

    public void validateProfileId(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ValidationException("Profile with ID " + profileId + " does not exist");
        }
    }

    public Boolean existsById(long profileId) {
       return profileRepository.existsById(profileId);
    }
}
