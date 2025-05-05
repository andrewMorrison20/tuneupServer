package com.tuneup.tuneup.pricing.services;

import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.entities.Price;
import com.tuneup.tuneup.pricing.enums.Period;
import com.tuneup.tuneup.pricing.repositories.PriceRepository;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class PriceValidator {

    private final PriceRepository priceRepository;

    public PriceValidator(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Validate a price dto set before creation, centralised here to avoid complex logic splitting across service and validator layers
     * @param priceDtoSet set of prices to validate
     * @return the set of created prices
     */
    public Set<Price> validateOrCreatePricing(Set<PriceDto> priceDtoSet) {

        Map<Boolean, List<PriceDto>> partitioned = priceDtoSet.stream()
                .collect(Collectors.partitioningBy(PriceDto::isStandardPricing));

        Set<Price> standardPrices = validateOrCreateStandardPricing(new HashSet<>(partitioned.get(true)));
        Set<Price> customPrices = validateOrCreateCustomPricing(new HashSet<>(partitioned.get(false)));

        return Stream.concat(standardPrices.stream(), customPrices.stream())
                .collect(Collectors.toSet());
    }

    /**
     * Validate prices, fetch if existing, create if not (but still valid)
     * @param priceDtoSet prices to validate and create
     * @return set Price - the set of valid prices
     */
    public Set<Price> validateOrCreateStandardPricing(Set<PriceDto> priceDtoSet) {
        return priceDtoSet.stream()
                .map(dto -> {
                    validatePeriod(dto.getPeriod());
                    validateRate(dto.getRate());

                    return priceRepository.findByPeriodAndRate(Period.valueOf(dto.getPeriod()), dto.getRate())
                            .orElseGet(() -> {
                                Price newPrice = new Price();
                                newPrice.setPeriod(Period.valueOf(dto.getPeriod()));
                                newPrice.setRate(dto.getRate());
                                newPrice.setStandardPricing(true);
                                return priceRepository.save(newPrice);
                            });
                })
                .collect(Collectors.toSet());
    }


    /**
     * Validate and create custom pricing
     * @param priceDtoSet set of prices to validate
     * @return Set Price - valid pricing
     */
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

    /**
     * Validate a price prior to creation
     * @param priceDto price to validate
     */
    public void validatePriceDto(PriceDto priceDto) {

        validatePeriod(priceDto.getPeriod());
        validateRate(priceDto.getRate());

        Optional<Price> price = priceRepository.findByPeriodAndRate(
                Period.valueOf(priceDto.getPeriod()), priceDto.getRate()
        );
        if (price.isPresent()) {
            throw new ValidationException("Pricing combination already exists!");
        }
    }

    /**
     * Validate the rate of the price
     * @param rate rate to validate
     */
    private void validateRate(Double rate) {
        if (rate == null || rate <= 0) {
            throw new ValidationException("Rate must be a positive number.");
        }
    }

    /**
     * Validate the period of the price
     * @param period period to validate
     */
    private void validatePeriod(String period) {
        try {
            Period.valueOf(period);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid period: " + period);
        }
    }

    /**
     * retrieve a price by id
     * @param id
     * @return price
     */
    public Price fetchAndValidateById(long id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Price with that ID not found"));
    }
}



