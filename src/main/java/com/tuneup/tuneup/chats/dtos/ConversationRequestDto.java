package com.tuneup.tuneup.chats.dtos;

public class ConversationRequestDto {

    private Long userId;
    private Long participantId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }
}
