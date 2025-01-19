package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.pricing.PriceRepository;
import com.tuneup.tuneup.pricing.PriceValidator;
import com.tuneup.tuneup.users.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tuneup.tuneup.pricing.Price;
import com.tuneup.tuneup.pricing.PriceDto;

@ExtendWith(MockitoExtension.class)
class PriceValidatorTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceValidator priceValidator;

    private PriceDto standardPriceDto;
    private PriceDto customPriceDto;
    private Price price;

    @BeforeEach
    void setUp() {
        standardPriceDto = new PriceDto();
        standardPriceDto.setId(1L);
        standardPriceDto.setPeriod("ONE_HOUR");
        standardPriceDto.setRate(100.0);
        standardPriceDto.setStandardPricing(true);

        customPriceDto = new PriceDto();
        customPriceDto.setId(2L);
        customPriceDto.setPeriod("CUSTOM");
        customPriceDto.setRate(100.0);
        customPriceDto.setStandardPricing(false);

        price = new Price();
    }

    @Test
    void testValidatePriceDtoExpectsException() {
        when(priceRepository.findByPeriodAndRate(any(), anyDouble())).thenReturn(Optional.of(price));

        assertThrows(ValidationException.class, () -> priceValidator.validatePriceDto(standardPriceDto));
    }

    @Test
    void testValidatePriceDto() {
        when(priceRepository.findByPeriodAndRate(any(), anyDouble())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> priceValidator.validatePriceDto(standardPriceDto));
    }

    @Test
    void testValidateOrCreatePricingStandard() {
        Set<PriceDto> priceDtos = Collections.singleton(standardPriceDto);
        Set<Price> expectedPrices = Collections.singleton(price);

        when(priceRepository.findByPeriodAndRate(any(), anyDouble())).thenReturn(Optional.empty());
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        Set<Price> result = priceValidator.validateOrCreatePricing(priceDtos);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testValidateOrCreatePricingCustom() {
        Set<PriceDto> priceDtos = Collections.singleton(customPriceDto);

        when(priceRepository.findByRateAndDescription(anyDouble(), any())).thenReturn(Optional.empty());
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        Set<Price> result = priceValidator.validateOrCreatePricing(priceDtos);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testValidateOrCreatePricingStandardAndCustom() {
        Set<PriceDto> priceDtos = Set.of(customPriceDto,standardPriceDto);

        when(priceRepository.findByRateAndDescription(anyDouble(), any())).thenReturn(Optional.empty());
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        Set<Price> result = priceValidator.validateOrCreatePricing(priceDtos);
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}

