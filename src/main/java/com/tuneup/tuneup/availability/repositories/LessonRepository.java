package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.Lesson;
import com.tuneup.tuneup.availability.dtos.LessonSummaryDto;
import com.tuneup.tuneup.availability.enums.LessonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("SELECT l FROM Lesson l " +
            "LEFT JOIN Payment p ON p.lesson.id = l.id " +
            "WHERE l.tuition.id = :tuitionId " +
            "AND l.lessonStatus = :status " +
            "AND p.id IS NULL")
    Set<Lesson> findCompletedLessonsWithoutPayment(@Param("tuitionId") Long tuitionId,
                                                   @Param("status") LessonStatus status);




    Set<Lesson> findAllByTuitionId(Long tuitionId);

    @Query("SELECT l FROM Lesson l " +
            "WHERE l.availability.startTime BETWEEN :start AND :end " +
            "AND l.tuition.id = :tuitionId")
        Set<Lesson> findLessonsByPeriod(@Param("tuitionId") Long tuitionId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT l FROM Lesson l " +
            "WHERE l.availability.startTime BETWEEN :start AND :end " +
            "AND l.tuition.tutor.id = :tutorId")
    Set<Lesson> findAllLessonsByTutorId(@Param("tutorId") Long tutorId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT l FROM Lesson l " +
            "WHERE l.availability.startTime BETWEEN :start AND :end " +
            "AND l.tuition.tutor.id = :tutorId")
    Set<Lesson> findAllLessonsByStudentId(@Param("tutorId") Long tutorId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("SELECT new com.tuneup.tuneup.availability.dtos.LessonSummaryDto(" +
            "l.id, t.id, s.displayName, s.id, l.lessonStatus, l.lessonType) " +
            "FROM Lesson l " +
            "JOIN l.tuition t " +
            "JOIN t.student s " +
            "WHERE l.availability.id = :availabilityId AND l.lessonStatus IN ('CONFIRMED', 'COMPLETED')")
    LessonSummaryDto findLessonSummaryByAvailabilityId(Long availabilityId);
}



