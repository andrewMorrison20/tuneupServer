package com.tuneup.tuneup.payments;


public class PaymentDto {


    private Long tuitionId;
    private String lessonDate;
    private Double amount;
    private String status;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private String displayName;

    public PaymentDto() {
    }

    public PaymentDto(Long id, Long tuitionId, String lessonDate, Double amount, String status,
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


    public String getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(String lessonDate) {
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
