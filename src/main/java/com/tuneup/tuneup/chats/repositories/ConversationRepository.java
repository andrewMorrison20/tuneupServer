package com.tuneup.tuneup.chats.repositories;


import com.tuneup.tuneup.chats.entities.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE c.profile1.id = :profileId OR c.profile2.id = :profileId")
    Page<Conversation> findByProfileId(@Param("profileId") Long profileId, Pageable pageable);


    @Query("SELECT c FROM Conversation c WHERE (c.profile1.id = :profile1Id AND c.profile2.id = :profile2Id) OR (c.profile1.id = :profile2Id AND c.profile2.id = :profile1Id)")
    Optional<Conversation> findByProfiles(@Param("profile1Id") Long profile1Id, @Param("profile2Id") Long profile2Id);

}

