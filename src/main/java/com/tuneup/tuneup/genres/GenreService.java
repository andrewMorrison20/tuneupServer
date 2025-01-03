package com.tuneup.tuneup.genres;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final GenreValidator genreValidator;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper, GenreValidator genreValidator) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
        this.genreValidator = genreValidator;
    }

    public Set<GenreDto> getAll(){
        List<Genre> genres = genreRepository.findAll();
        return genres.stream()
                .map(genreMapper :: toGenreDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public GenreDto createGenre(GenreDto genreDto) {
        genreValidator.validateGenre(genreDto);
        Genre genre = genreMapper.toGenre(genreDto);
        Genre persistedGenre = genreRepository.save(genre);
        return genreMapper.toGenreDto(persistedGenre);
    }
    /**
     * Retrieves profiles associated with a specific genre.
     *
     * @param genreId The ID of the genre.
     * @return A set of profiles or null if the genre doesn't exist.
     */
    public Set<Profile> getProfilesForGenre(Long genreId) {
        return genreRepository.findById(genreId)
                .map(Genre::getProfiles)
                .orElse(null);
    }
}