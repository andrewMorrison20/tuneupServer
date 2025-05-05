package com.tuneup.tuneup.chats.entities;

import com.tuneup.tuneup.profiles.entities.Profile;
import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_profile_id", nullable = false)
    private Profile senderProfile;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isRead = false;

    //Transients aren't persisted to the db for the message entity
    @Transient
    private String senderName;

    @Transient
    private String senderProfilePictureUrl;

    public String getSenderProfilePictureUrl() {
        return senderProfilePictureUrl;
    }

    public void setSenderProfilePictureUrl(String senderProfilePictureUrl) {
        this.senderProfilePictureUrl = senderProfilePictureUrl;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Profile getSenderProfile() {
        return senderProfile;
    }

    public void setSenderProfile(Profile senderProfile) {
        this.senderProfile = senderProfile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    /**
     * Grab the fields required at front end from the db prior to conversion to dto
     */
    @PostLoad
    private void populateTransientFields() {
        if (senderProfile != null) {
            this.senderName = senderProfile.getDisplayName();
            this.senderProfilePictureUrl = (senderProfile.getProfilePicture() != null)
                    ? senderProfile.getProfilePicture().getUrl()
                    : null;
        }
    }
}
