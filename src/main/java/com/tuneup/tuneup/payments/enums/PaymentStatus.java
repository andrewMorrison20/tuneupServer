package com.tuneup.tuneup.payments.enums;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    @JsonAlias({"Paid", "PAID", "paid"})
    PAID("Paid"),

    @JsonAlias({"Due", "DUE", "due"})
    DUE("Due"),

    @JsonAlias({"Overdue", "OVERDUE", "overdue"})
    OVERDUE("Overdue");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @JsonCreator // Handles case-insensitive input
    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid payment status: " + value);
    }
}
