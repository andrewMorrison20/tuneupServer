package com.tuneup.tuneup.chats.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class ConversationDto {

    private List<ConversationParticipantDto> participants;
    private LocalDateTime lastMessageTimestamp;
    private Long id;
    private String lastMessage;

    public List<ConversationParticipantDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ConversationParticipantDto> participants) {
        this.participants = participants;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(LocalDateTime lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
