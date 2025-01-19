package com.tuneup.tuneup.junit.controllers;

import com.tuneup.tuneup.genres.GenreController;
import com.tuneup.tuneup.genres.GenreDto;
import com.tuneup.tuneup.genres.GenreService;
import com.tuneup.tuneup.profiles.Profile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreControllerTests {

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreController genreController;

    @Test
    void createGenre_ShouldReturnCreatedGenre() {
        GenreDto genreDto = new GenreDto();

        genreDto.setId(1L);
        genreDto.setName("Rock");

        when(genreService.createGenre(genreDto)).thenReturn(genreDto);
        ResponseEntity<GenreDto> response = genreController.createGenre(genreDto);
        assertNotNull(response.getBody());
        assertEquals(genreDto, response.getBody());
    }

    @Test
    void getGenres_ShouldReturnSetOfGenres() {
        GenreDto genreDto = new GenreDto();

        genreDto.setId(1L);
        genreDto.setName("Rock");

        Set<GenreDto> genreSet = Collections.singleton(genreDto);
        when(genreService.getAll()).thenReturn(genreSet);

        ResponseEntity<Set<GenreDto>> response = genreController.getGenres();
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getProfilesForGenre_ShouldReturnProfiles() {
        Profile profile = new Profile();
        profile.setId(1L);
        profile.setDisplayName("John Doe");

        Set<Profile> profiles = Collections.singleton(profile);
        when(genreService.getProfilesForGenre(1L)).thenReturn(profiles);

        ResponseEntity<Set<Profile>> response = genreController.getProfilesForGenre(1L);
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getProfilesForGenre_ShouldReturnNotFoundIfEmpty() {
        when(genreService.getProfilesForGenre(1L)).thenReturn(Collections.emptySet());

        ResponseEntity<Set<Profile>> response = genreController.getProfilesForGenre(1L);
        assertEquals(404, response.getStatusCode().value());
    }
}
