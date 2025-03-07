package com.tuneup.tuneup.payments;


import java.time.LocalDateTime;

public class PaymentDto {


    private Long tuitionId;
    private LocalDateTime lessonDate;
    private Double amount;
    private String status;
    private Long lessonId;

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

    public PaymentDto(Long id, Long tuitionId, LocalDateTime lessonDate, Double amount, String status,
                      String displayName, String dueDate, String invoiceUrl) {
        this.id = id;
        this.tuitionId = tuitionId;
        this.lessonDate = lessonDate;
        this.amount = amount;
        this.status = status;
        this.displayName = displayName;
        this.dueDate = dueDate;
        this.invoiceUrl = invoiceUrl;
    }

    public Long getTuitionId() {
        return tuitionId;
    }

    public void setTuitionId(Long tuitionId) {
        this.tuitionId = tuitionId;
    }


    private String dueDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

}
