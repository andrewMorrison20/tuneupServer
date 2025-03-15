package com.tuneup.tuneup.chats;


import com.tuneup.tuneup.profiles.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.profile1.id = :profileId OR c.profile2.id = :profileId)")
    List<Conversation> findByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT c FROM Conversation c WHERE (c.profile1.id = :profile1 AND c.profile2.id = :profile2) OR (c.profile1.id = :profile2 AND c.profile2.id = :profile1)")
    Optional<Conversation> findByProfiles(@Param("profile1") Profile profile1, @Param("profile2") Profile profile2);

}

