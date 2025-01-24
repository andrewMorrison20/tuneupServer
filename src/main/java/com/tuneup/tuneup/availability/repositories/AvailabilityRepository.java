package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {


    @Query("SELECT a FROM Availability a WHERE a.profile.id = :profileId AND a.status = :status")
    Set<Availability> findByProfileIdAndStatus(@Param("profileId") Long profileId, @Param("status") AvailabilityStatus status);


    @Query("SELECT a FROM Availability a WHERE a.startTime BETWEEN :start AND :end AND a.status = 'AVAILABLE'")
    Set<Availability> findAvailableSlots(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Set<Availability> findByProfileId(long profileId);

}
