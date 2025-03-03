package com.tuneup.tuneup.payments;


import com.tuneup.tuneup.tuitions.TuitionDto;

public class PaymentDto {


    private TuitionDto tuitionDto;
    private String lessonDate;
    private Double amount;
    private String status;

    public TuitionDto getTuitionDto() {
        return tuitionDto;
    }

    public void setTuitionDto(TuitionDto tuitionDto) {
        this.tuitionDto = tuitionDto;
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
