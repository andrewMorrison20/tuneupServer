package com.tuneup.tuneup.notifications;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private NotificationType type;
    private String message;
    private Long userId;
    private LocalDateTime timestamp;

    private boolean read;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public NotificationType getType() {
        return type;
    }
    public void setType(NotificationType type) {
        this.type = type;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
}
