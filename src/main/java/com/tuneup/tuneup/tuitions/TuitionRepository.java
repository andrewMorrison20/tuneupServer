package com.tuneup.tuneup.tuitions;

import com.tuneup.tuneup.profiles.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TuitionRepository extends JpaRepository<Tuition,Long> {

    Boolean existsByTutorIdAndStudentId(Long tutorId, Long studentId);

    Page<Tuition> findAllByTutorId(Long tutorId, Pageable pageable);


    Page<Tuition> findAllByStudentId(Long profileId, Pageable pageable);

    @Query("SELECT t.student FROM Tuition t WHERE t.tutor.id = :tutorId AND t.activeTuition =:active")
    Page<Profile> findStudentsByTutorId(@Param("tutorId") Long tutorId, @Param("active") boolean active, Pageable pageable);

    @Query("SELECT t.tutor FROM Tuition t WHERE t.student.id = :studentId AND t.activeTuition =:active")
    Page<Profile> findTutorsByStudentId(@Param("studentId") Long studentId, @Param("active") boolean active, Pageable pageable);

    Optional<Tuition> findByStudentIdAndTutorId(Long studentProfileId, Long tutorProfileId);
}
