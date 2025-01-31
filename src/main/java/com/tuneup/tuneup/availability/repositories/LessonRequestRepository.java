package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.LessonRequest;
import com.tuneup.tuneup.profiles.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface LessonRequestRepository extends JpaRepository<LessonRequest, Long> {

    Page<LessonRequest> findByTutorId(Long tutorId, Pageable pageable);

    Page<LessonRequest> findByStudentId(Long studentId, Pageable pageable);

    Page<LessonRequest> findByStudentIdAndTutorId(Long studentId, Long tutorId, Pageable pageable);

    @Query("SELECT DISTINCT s FROM LessonRequest lr JOIN lr.student s WHERE lr.tutor.id = :tutorId ORDER BY s.displayName ASC")
    Page<Profile> findStudentsByTutorId(@Param("tutorId") Long tutorId, Pageable pageable);

}
