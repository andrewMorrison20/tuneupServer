package com.tuneup.tuneup.pricing;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PriceValidator {

    private final PriceRepository priceRepository;

    public PriceValidator(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Set<Price> validateOrCreatePricing(Set<PriceDto> priceDtoSet) {
        return priceDtoSet.stream()
                .map(dto -> priceRepository.findByPeriodAndRate(Period.valueOf(dto.getPeriod()), dto.getRate())
                        .orElseGet(() -> {
                            Price newPrice = new Price();
                            newPrice.setPeriod(Period.valueOf(dto.getPeriod()));
                            newPrice.setRate(dto.getRate());
                            return priceRepository.save(newPrice);
                        }))
                .collect(Collectors.toSet());
    }
}
