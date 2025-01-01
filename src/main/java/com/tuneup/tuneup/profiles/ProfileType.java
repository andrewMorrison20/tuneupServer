package com.tuneup.tuneup.profiles;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProfileType {
    @JsonProperty("Student")
    STUDENT,

    @JsonProperty("Parent")
    PARENT,

    @JsonProperty("Tutor")
    TUTOR;
}
