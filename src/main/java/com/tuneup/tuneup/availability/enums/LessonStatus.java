package com.tuneup.tuneup.availability.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Used in lesson entity to normalise status
 */
public enum LessonStatus {
    CONFIRMED("confirmed"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String value;

    LessonStatus(String value) {
        this.value = value;
    }

    /**
     * Return the string value of the enum over rest requests to simplify handling in UI.
     * @return String value of the enim
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Serialise the enum from the string value received in rest requests
     * @param value
     * @return LessStatus
     */
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
