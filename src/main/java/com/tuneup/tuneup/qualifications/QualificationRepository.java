package com.tuneup.tuneup.qualifications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualificationRepository extends JpaRepository<Qualification, Long> {
    List<Qualification> findByInstrument_Name(String instrumentName);
}
