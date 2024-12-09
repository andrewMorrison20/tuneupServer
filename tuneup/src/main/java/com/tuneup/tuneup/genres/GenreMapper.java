package com.tuneup.tuneup.genres;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface GenreMapper {

    Genre toGenre(GenreDto genreDto);

    GenreDto toGenreDto(Genre genre);
}
