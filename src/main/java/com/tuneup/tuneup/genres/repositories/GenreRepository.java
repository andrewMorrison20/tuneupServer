package com.tuneup.tuneup.genres.repositories;

import com.tuneup.tuneup.genres.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Boolean existsByName(String name);
}
