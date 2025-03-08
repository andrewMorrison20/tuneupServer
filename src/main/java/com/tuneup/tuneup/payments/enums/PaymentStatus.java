package com.tuneup.tuneup.payments.enums;


public enum PaymentStatus {
    PAID("Paid"),
    DUE("Due"),
    OVERDUE("Overdue");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
