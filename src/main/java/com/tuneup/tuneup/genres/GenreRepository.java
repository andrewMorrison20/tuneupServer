package com.tuneup.tuneup.genres;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Boolean existsByName(String name);
}
