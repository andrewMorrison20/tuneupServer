package com.tuneup.tuneup.tuitions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuitionRepository extends JpaRepository<Tuition,Long> {
}
