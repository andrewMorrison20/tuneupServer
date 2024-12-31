package com.tuneup.tuneup.pricing;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PriceValidator {

    private final PriceRepository priceRepository;

    public PriceValidator(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Set<Price> validateOrCreatePricing(Set<PriceDto> priceDtoSet) {

        Map<Boolean, List<PriceDto>> partitioned = priceDtoSet.stream()
                .collect(Collectors.partitioningBy(PriceDto::isStandardPricing));

        Set<Price> standardPrices = validateOrCreateStandardPricing(new HashSet<>(partitioned.get(true)));
        Set<Price> customPrices = validateOrCreateCustomPricing(new HashSet<>(partitioned.get(false)));

        return Stream.concat(standardPrices.stream(), customPrices.stream())
                .collect(Collectors.toSet());
    }

    public Set<Price> validateOrCreateStandardPricing(Set<PriceDto> priceDtoSet) {
        return priceDtoSet.stream()
                .map(dto -> priceRepository.findByPeriodAndRate(Period.valueOf(dto.getPeriod()), dto.getRate())
                        .orElseGet(() -> {
                            Price newPrice = new Price();
                            newPrice.setPeriod(Period.valueOf(dto.getPeriod()));
                            newPrice.setRate(dto.getRate());
                            newPrice.setStandardPricing(true);
                            return priceRepository.save(newPrice);
                        }))
                .collect(Collectors.toSet());
    }

    public Set<Price> validateOrCreateCustomPricing(Set<PriceDto> priceDtoSet) {
        return priceDtoSet.stream()
                .map(dto -> priceRepository.findByRateAndDescription(dto.getRate(), dto.getDescription())
                        .orElseGet(() -> {
                            Price newPrice = new Price();
                            newPrice.setPeriod(Period.valueOf(dto.getPeriod()));
                            newPrice.setRate(dto.getRate());
                            newPrice.setStandardPricing(false);
                            newPrice.setDescription(dto.getDescription());
                            return priceRepository.save(newPrice);
                        }))
                .collect(Collectors.toSet());
    }

}

