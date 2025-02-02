package com.tuneup.tuneup.tuitions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TuitionRepository extends JpaRepository<Tuition,Long> {

    Boolean existsByTutorIdAndStudentId(Long tutorId, Long studentId);

    Page<Tuition> findAllByTutorId(Long tutorId, Pageable pageable);

}
