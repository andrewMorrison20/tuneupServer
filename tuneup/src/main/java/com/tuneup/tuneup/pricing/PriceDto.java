package com.tuneup.tuneup.pricing;

import org.springframework.stereotype.Component;

@Component
public class PriceDto {
    private String period; // ONE_HOUR, TWO_HOURS, CUSTOM, etc.
    private Double rate;
    private String description;


    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
}
