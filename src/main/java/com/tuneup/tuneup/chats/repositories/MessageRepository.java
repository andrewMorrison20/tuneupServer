package com.tuneup.tuneup.chats.repositories;

import com.tuneup.tuneup.chats.entities.Conversation;
import com.tuneup.tuneup.chats.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByTimestampAsc(Conversation conversation);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.timestamp DESC")
    Page<Message> findMessagesByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

}
