package com.tuneup.tuneup.notifications;

import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {

    private final Long userId;
    private final NotificationType notificationType;
    private final String message;


    public NotificationEvent(Object source, Long userId, NotificationType notificationType, String message) {
        super(source);
        this.userId = userId;
        this.notificationType = notificationType;
        this.message = message;

    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getMessage() {
        return message;
    }
}
