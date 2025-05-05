package com.tuneup.tuneup.chats.dtos;


import java.time.LocalDateTime;

public class MessageDto {

    private long id;
    private Long senderProfileId;
    private Long conversationId;
    private String content;
    private String senderName;
    private String senderProfilePictureUrl;

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    private LocalDateTime timestamp;

    public long getId() {
        return id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfilePictureUrl() {
        return senderProfilePictureUrl;
    }

    public void setSenderProfilePictureUrl(String senderProfilePictureUrl) {
        this.senderProfilePictureUrl = senderProfilePictureUrl;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getSenderProfileId() {
        return senderProfileId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public void setSenderProfileId(Long senderProfileId) {
        this.senderProfileId = senderProfileId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


