package com.tuneup.tuneup.qualifications;

import com.fasterxml.jackson.annotation.JsonValue;

public enum QualificationName {
    GRADE_1,
    GRADE_2,
    GRADE_3,
    GRADE_4,
    GRADE_5,
    GRADE_6,
    GRADE_7,
    GRADE_8,
    DIPLOMA,
    CERTIFICATE;

    @JsonValue
    @Override
    public String toString() {
        return switch (this) {
            case GRADE_1 -> "Grade 1";
            case GRADE_2 -> "Grade 2";
            case GRADE_3 -> "Grade 3";
            case GRADE_4 -> "Grade 4";
            case GRADE_5 -> "Grade 5";
            case GRADE_6 -> "Grade 6";
            case GRADE_7 -> "Grade 7";
            case GRADE_8 -> "Grade 8";
            case DIPLOMA -> "Diploma";
            case CERTIFICATE -> "Certificate";
            default -> super.toString();
        };
    }
}

