package com.tuneup.tuneup.qualifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileInstrumentQualificationRepository extends JpaRepository<ProfileInstrumentQualification, Long> {

    @Query("SELECT uq FROM ProfileInstrumentQualification uq WHERE uq.profile.id = :profileId")
    List<ProfileInstrumentQualification> findByUserProfileId(@Param("profileId") Long profileId);
}