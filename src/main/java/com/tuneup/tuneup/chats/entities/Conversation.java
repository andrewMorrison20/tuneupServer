package com.tuneup.tuneup.chats.entities;

import com.tuneup.tuneup.profiles.Profile;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile1_id", nullable = false)
    private Profile profile1;

    @ManyToOne
    @JoinColumn(name = "profile2_id", nullable = false)
    private Profile profile2;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @OneToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    public Profile getProfile1() {
        return profile1;
    }

    public void setProfile1(Profile profile1) {
        this.profile1 = profile1;
    }

    public Profile getProfile2() {
        return profile2;
    }

    public void setProfile2(Profile profile2) {
        this.profile2 = profile2;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<Profile> getParticipants() {
        return List.of(profile1, profile2);
    }
}
