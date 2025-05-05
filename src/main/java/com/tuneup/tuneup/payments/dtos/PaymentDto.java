package com.tuneup.tuneup.payments.dtos;


import com.tuneup.tuneup.payments.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentDto {


    private Long tuitionId;
    private LocalDateTime lessonDate;
    private Double amount;
    private PaymentStatus status;
    private Long lessonId;
    private LocalDateTime paidOn;

    public LocalDateTime getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDateTime paidOn) {
        this.paidOn = paidOn;
    }

    public LocalDateTime getReminderSentOn() {
        return reminderSentOn;
    }

    public void setReminderSentOn(LocalDateTime reminderSentOn) {
        this.reminderSentOn = reminderSentOn;
    }

    private LocalDateTime reminderSentOn;

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String displayName;

    public PaymentDto() {
    }

    public PaymentDto(Long id, Long tuitionId, LocalDateTime lessonDate,Long lessonId, Double amount, PaymentStatus status,
                      String displayName, LocalDateTime dueDate, String invoiceUrl, LocalDateTime paidOn,  LocalDateTime reminderSentOn) {
        this.id = id;
        this.tuitionId = tuitionId;
        this.lessonDate = lessonDate;
        this.lessonId = lessonId;
        this.amount = amount;
        this.status = status;
        this.displayName = displayName;
        this.dueDate = dueDate;
        this.invoiceUrl = invoiceUrl;
        this.paidOn = paidOn;
        this.reminderSentOn = reminderSentOn;
    }

    public Long getTuitionId() {
        return tuitionId;
    }

    public void setTuitionId(Long tuitionId) {
        this.tuitionId = tuitionId;
    }


    private LocalDateTime dueDate;
    private String invoiceUrl;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public LocalDateTime getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDateTime lessonDate) {
        this.lessonDate = lessonDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

}
