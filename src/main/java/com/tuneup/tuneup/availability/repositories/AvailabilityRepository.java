package com.tuneup.tuneup.availability.repositories;

import com.tuneup.tuneup.availability.Availability;
import com.tuneup.tuneup.availability.enums.AvailabilityStatus;
import com.tuneup.tuneup.profiles.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {


    @Query("SELECT a FROM Availability a WHERE a.profile.id = :profileId AND a.status = :status")
    Set<Availability> findByProfileIdAndStatus(@Param("profileId") Long profileId, @Param("status") AvailabilityStatus status);


    @Query("SELECT a FROM Availability a WHERE a.startTime BETWEEN :start AND :end AND a.status = 'AVAILABLE'")
    Set<Availability> findAvailableSlots(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a FROM Availability a " +
            "WHERE a.profile.id = :profileId " +
            "and a.startTime BETWEEN :start AND :end ")
    Set<Availability> findProfilePeriodSlots(@Param("profileId")Long profileId,@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a.profile FROM Availability a WHERE a.startTime BETWEEN :start AND :end AND a.status = 'AVAILABLE'")
    Set<Profile> findProfileByAvailableSlots(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT a.profile.id FROM Availability a " +
            "WHERE a.startTime >= :start AND a.startTime < :end " +
            "AND a.status = 'AVAILABLE'")
    Set<Long> findAvailableProfileIds(@DateTimeFormat(iso= DateTimeFormat.ISO.DATE)@Param("start") LocalDateTime start, @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)@Param("end") LocalDateTime end);

    Set<Availability> findByProfileId(long profileId);


    Boolean existsByProfileIdAndStartTimeAndEndTime(Long profileId, LocalDateTime startTime, LocalDateTime endTime);
}
