package com.tuneup.tuneup.genres.mappers;

import com.tuneup.tuneup.genres.dtos.GenreDto;
import com.tuneup.tuneup.genres.entities.Genre;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface GenreMapper {

    Genre toGenre(GenreDto genreDto);

    GenreDto toGenreDto(Genre genre);
}
