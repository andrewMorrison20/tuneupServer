package com.tuneup.tuneup.chats;


public class MessageDto {
    private Long senderProfileId;
    private Long conversationId;
    private String content;

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


