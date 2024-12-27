package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.Instruments.Instrument;
import com.tuneup.tuneup.Instruments.InstrumentDto;
import com.tuneup.tuneup.genres.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenreServiceTests {

    @Mock
    private GenreRepository genreRepo;

    @Mock
    private GenreValidator genreValidator;

    @Mock
    private GenreMapper genreMapper;

    @InjectMocks
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllReturnsNonEmpty(){

        Genre testGenre1 = new Genre();
        Genre testGenre2 = new Genre();

        testGenre1.setName("testGenre1");
        testGenre2.setName("testGenre2");

        GenreDto dto1 = new GenreDto();
        GenreDto dto2 = new GenreDto();

        when(genreRepo.findAll()).thenReturn(List.of(testGenre1, testGenre2));
        when(genreMapper.toGenreDto(testGenre1)).thenReturn(dto1);
        when(genreMapper.toGenreDto(testGenre2)).thenReturn(dto2);

        List<Genre> results = List.of(testGenre1,testGenre2);
        when(genreRepo.findAll()).thenReturn(results);

        Set<GenreDto> resultsDto = genreService.getAll();

        assertEquals(2,resultsDto.size());

        assertTrue(resultsDto.contains(dto1));
        assertTrue(resultsDto.contains(dto2));
    }

    @Test
    void findAllReturnsEmptySet(){
        when(genreRepo.findAll()).thenReturn(Collections.emptyList());
        Set<GenreDto> resultsDto = genreService.getAll();
        assertEquals(0,resultsDto.size());
    }

    @Test
    void createInstrument_shouldValidateAndSaveInstrument() {
        GenreDto genreDto = new GenreDto();
        Genre mockGenre = new Genre();

        Genre savedGenre = new Genre();

        GenreDto inputDto = new GenreDto();
        GenreDto expectedDto = new GenreDto();

        when(genreMapper.toGenre(inputDto)).thenReturn(mockGenre);
        when(genreRepo.save(mockGenre)).thenReturn(savedGenre);
        when(genreMapper.toGenreDto(savedGenre)).thenReturn(expectedDto);


        GenreDto result = genreService.createGenre(inputDto);

        //verify(GenreValidator).validateInstrumentDto(inputDto);
        verify(genreMapper).toGenre(inputDto);
        verify(genreRepo).save(mockGenre);
        verify(genreMapper).toGenreDto(savedGenre);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }
}
