package com.tuneup.tuneup.profiles.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tuneup.tuneup.users.exceptions.ValidationException;

public enum LessonType {

    ONLINE("Online", "Lessons are conducted fully online"),
    INPERSON("In Person", "Lessons are conducted face-to-face"),
    ONLINE_AND_INPERSON("Online & In-Person", "Both online and in-person options available");

    private final String displayName;
    private final String description;

    LessonType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static LessonType fromString(String value) {
        for (LessonType type : LessonType.values()) {
            if (type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new ValidationException("Unknown LessonType: " + value);
    }
}

