package com.tuneup.tuneup.availability.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LessonRequestStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    DECLINED("declined");

    private final String value;

    LessonRequestStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LessonRequestStatus fromValue(String value) {
        for (LessonRequestStatus status : LessonRequestStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid lesson request status: " + value);
    }
}

