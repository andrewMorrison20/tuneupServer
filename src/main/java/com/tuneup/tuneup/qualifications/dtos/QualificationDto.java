package com.tuneup.tuneup.qualifications.dtos;

import com.tuneup.tuneup.qualifications.QualificationName;
import org.springframework.stereotype.Component;

@Component
public class QualificationDto {
    private Long id;
    private QualificationName name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QualificationName getName() {
        return name;
    }

    public void setName(QualificationName name) {
        this.name = name;
    }
}
