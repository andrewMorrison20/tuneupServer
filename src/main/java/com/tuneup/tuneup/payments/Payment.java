package com.tuneup.tuneup.payments;

import com.tuneup.tuneup.availability.entities.Lesson;
import com.tuneup.tuneup.payments.enums.PaymentStatus;
import com.tuneup.tuneup.tuitions.entities.Tuition;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tuition_id")
    private Tuition tuition;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    private LocalDateTime reminderSentOn;

    public Lesson getLesson() {
        return lesson;
    }

    public LocalDateTime getReminderSentOn() {
        return reminderSentOn;
    }

    public void setReminderSentOn(LocalDateTime reminderSentOn) {
        this.reminderSentOn = reminderSentOn;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public Tuition getTuition() {
        return tuition;
    }

    public void setTuition(Tuition tuition) {
        this.tuition = tuition;
    }

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime dueDate;

    private LocalDateTime paidOn;

    public LocalDateTime getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(LocalDateTime paidOn) {
        this.paidOn = paidOn;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    private String invoiceUrl;

}
