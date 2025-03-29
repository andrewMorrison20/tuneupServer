package com.tuneup.tuneup.qualifications.dtos;

import com.tuneup.tuneup.qualifications.QualificationName;
import org.springframework.stereotype.Component;

@Component
public class ProfileInstrumentQualificationDto {

    private Long id;
    private Long instrumentId;
    private Long qualificationId;
    private String instrumentName;
    private QualificationName qualificationName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public QualificationName getQualificationName() {
        return qualificationName;
    }

    public void setQualificationName(QualificationName qualificationName) {
        this.qualificationName = qualificationName;
    }

    public String getInstrumentName(){
        return instrumentName;
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
