package com.tuneup.tuneup.chats.dtos;

import org.springframework.stereotype.Component;

@Component
public class ConversationParticipantDto {

    private long id;
    private String displayName;
    private String profilePictureUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
