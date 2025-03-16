package com.tuneup.tuneup.chats.repositories;

import com.tuneup.tuneup.chats.entities.Conversation;
import com.tuneup.tuneup.chats.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByTimestampAsc(Conversation conversation);
}
