package com.tuneup.tuneup.notifications.enums;

/**
 * Enum to denote the different types of notifications
 */
public enum NotificationType {
    NEW_CHAT("New Chat Message"),
    LESSON_REQUEST("Lesson Request"),
    REQUEST_REJECTED("Request Rejected"),
    LESSON_CANCEL("Lesson Cancelled"),
    NEW_PAYMENT("New Payment"),
    PAYMENT_OVERDUE("Payment Overdue"),
    PAYMENT_MADE("Payment Made"),
    PAYMENT_DUE("Payment Due"),
    REQUEST_ACCEPTED("Request Accepted");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

