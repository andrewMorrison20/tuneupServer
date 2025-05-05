package com.tuneup.tuneup.genres.services;

import com.tuneup.tuneup.genres.dtos.GenreDto;
import com.tuneup.tuneup.genres.repositories.GenreRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class GenreValidator {
    private GenreRepository genreRepository;

    public GenreValidator(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Check for duplicates prior to creation
     * @param genreDto genre to check for duplicates
     */
    public void validateGenre(GenreDto genreDto){
        if(genreRepository.existsByName(genreDto.getName())){
            throw new ValidationException("Genre Already Exsits!");
        }
    }
}
