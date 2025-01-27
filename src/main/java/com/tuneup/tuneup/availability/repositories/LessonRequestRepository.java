package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.LessonRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRequestRepository extends JpaRepository<LessonRequest, Long> {
}
