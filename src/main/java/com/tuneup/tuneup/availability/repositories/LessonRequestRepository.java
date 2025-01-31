package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.LessonRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LessonRequestRepository extends JpaRepository<LessonRequest, Long> {

    Set<LessonRequest> findByStudentIdAndTutorId(long studentId, long tutorId);
}
