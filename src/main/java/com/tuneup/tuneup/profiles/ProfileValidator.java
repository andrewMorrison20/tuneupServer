package com.tuneup.tuneup.profiles;

import com.tuneup.tuneup.Instruments.dtos.InstrumentDto;
import com.tuneup.tuneup.Instruments.repositories.InstrumentRepository;
import com.tuneup.tuneup.genres.dtos.GenreDto;
import com.tuneup.tuneup.genres.repositories.GenreRepository;
import com.tuneup.tuneup.profiles.dtos.ProfileDto;
import com.tuneup.tuneup.profiles.entities.Profile;
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

    /**
     * Validate a profile before updating/creating
     * @param instrumentRepository
     * @param genreRepository
     * @param profileRepository
     */
    public void validatorProfileDto(ProfileDto profileDto) {
        validateDisplayName(profileDto.getDisplayName());
        validateInstruments(profileDto.getInstruments());
        validateGenres(profileDto.getGenres());

    }

    /**
     * Validate the aossicated genres
     * @param genres
     */
    private void validateGenres(Set<GenreDto> genres) {
        if(genres!= null) {
            for (GenreDto genre : genres) {
                if(! genreRepository.existsById(genre.getId())){
                    throw new ValidationException("Genre with id " + genre.getId() + " does not exist");
                }
            }
        }
    }

    /**
     * Validate the associate instruments
     * @param instruments
     */
    private void validateInstruments(Set<InstrumentDto> instruments) {
        if (instruments == null || instruments.isEmpty()) {
           return;
        }
        for (InstrumentDto instrument : instruments) {
           if(! instrumentRepository.existsById(instrument.getId())){
               throw new ValidationException("The instrument with id " + instrument.getId() + " does not exist");
           };
        }
    }

    /**
     * Validate the display name
     * @param displayName
     */
    private void validateDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new ValidationException("Display name cannot be empty");
        }
    }

    /**
     * Check if profile exists for user id
     * @param userId
     */
    public void existsByUser(Long userId) {
        if(!profileRepository.existsByAppUserId(userId)){
            throw new ValidationException("Profile with id " + userId + " does not exist");
        };

    }

    /**
     * Retrieve profile by its Id
     * @param profileId
     * @return profile else throw
     */
    public Profile fetchById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ValidationException("Profile with id " + profileId + " does not exist"));
    }

    /**
     * check profile exists by its id (doesn't load profile into memory)
     * @param profileId
     */
    public void validateProfileId(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new ValidationException("Profile with ID " + profileId + " does not exist");
        }
    }

    /**
     * For scenarios where we dont want to implement error handling in caller method and only need boolean operator
     * @param profileId
     * @return Booleean result
     */
    public Boolean existsById(long profileId) {
       return profileRepository.existsById(profileId);
    }
}
