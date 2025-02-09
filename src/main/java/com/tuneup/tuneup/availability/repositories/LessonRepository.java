package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Set<Lesson> findAllByTuitionId(Long tuitionId);
}
