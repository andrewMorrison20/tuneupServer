package com.tuneup.tuneup.qualifications.repositories;

import com.tuneup.tuneup.qualifications.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {

}
