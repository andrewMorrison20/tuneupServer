package com.tuneup.tuneup.Instruments;
import org.springframework.stereotype.Component;

@Component
public class InstrumentDto {

    private String name;
    private Long id;

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
