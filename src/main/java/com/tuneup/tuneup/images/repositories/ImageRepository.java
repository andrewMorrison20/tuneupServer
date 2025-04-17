package com.tuneup.tuneup.images.repositories;

import com.tuneup.tuneup.images.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
