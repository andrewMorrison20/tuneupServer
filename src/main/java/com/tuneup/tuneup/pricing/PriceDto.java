package com.tuneup.tuneup.pricing;

import org.springframework.stereotype.Component;

@Component
public class PriceDto {
    private long id;
    private String period;
    private boolean standardPricing;
    private Double rate;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isStandardPricing() {
        return standardPricing;
    }

    public void setStandardPricing(boolean standardPricing) {
        this.standardPricing = standardPricing;
    }

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

