package com.tuneup.tuneup.profiles.repositories;



import com.tuneup.tuneup.profiles.entities.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> , JpaSpecificationExecutor<Profile> {

    Profile findByAppUserId(Long userId);

    boolean existsByAppUserId(Long userId);


    @Query("""
       SELECT p FROM Profile p
       WHERE p.id IN (
           SELECT CASE 
                      WHEN :isTutor = TRUE THEN t.student.id 
                      ELSE t.tutor.id 
                  END
           FROM Tuition t
           WHERE (:isTutor = TRUE AND t.tutor.id = :profileId)
               OR (:isTutor = FALSE AND t.student.id = :profileId)
               AND t.activeTuition = :active
        )
        AND p.id NOT IN (
            SELECT DISTINCT CASE 
                               WHEN c.profile1.id = :profileId THEN c.profile2.id 
                               ELSE c.profile1.id 
                           END
            FROM Conversation c
            WHERE c.profile1.id = :profileId OR c.profile2.id = :profileId
        )
    """)
        Page<Profile> findProfilesWithoutChatHistory(@Param("profileId") Long profileId,
                                                     @Param("isTutor") boolean isTutor,
                                                     @Param("active") boolean active,
                                                     Pageable pageable);

    @Modifying
    @Query("UPDATE Profile p SET p.deletedAt = :timestamp WHERE p.appUser.id IN :ids")
    void softDeleteProfilesByUserIds(@Param("ids") List<Long> ids, @Param("timestamp") LocalDateTime timestamp);

}



