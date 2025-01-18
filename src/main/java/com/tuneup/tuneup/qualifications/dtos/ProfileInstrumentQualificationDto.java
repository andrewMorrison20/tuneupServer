package com.tuneup.tuneup.qualifications.dtos;

import org.springframework.stereotype.Component;

@Component
public class ProfileInstrumentQualificationDto {

    private Long id;
    private Long instrumentId;
    private Long qualificationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Long getQualificationId() {
        return qualificationId;
    }

    public void setQualificationId(Long qualificationId) {
        this.qualificationId = qualificationId;
    }
}
