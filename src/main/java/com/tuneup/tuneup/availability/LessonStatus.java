package com.tuneup.tuneup.availability;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LessonStatus {
    CONFIRMED("confirmed"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String value;

    LessonStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LessonStatus fromValue(String value) {
        for (LessonStatus status : LessonStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid lesson status: " + value);
    }
}
