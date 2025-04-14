package com.tuneup.tuneup.genres.controller;

import com.tuneup.tuneup.genres.dtos.GenreDto;
import com.tuneup.tuneup.genres.services.GenreService;
import com.tuneup.tuneup.profiles.entities.Profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        GenreDto createdGenre = genreService.createGenre(genreDto);
        return ResponseEntity.ok(createdGenre);
    }

    @GetMapping
    public ResponseEntity<Set<GenreDto>> getGenres() {
        Set<GenreDto> genreDtos =genreService.getAll();
        return ResponseEntity.ok(genreDtos);
    }
    /**
     * Endpoint to get profiles associated with a specific genre.
     *
     * @param genreId The ID of the genre.
     * @return ResponseEntity containing the set of profiles.
     */
    @GetMapping("/{genreId}/profiles")
    public ResponseEntity<Set<Profile>> getProfilesForGenre(@PathVariable Long genreId) {
        Set<Profile> profiles = genreService.getProfilesForGenre(genreId);
        if (profiles == null || profiles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profiles);
    }
}

