package com.tuneup.tuneup.junit.validators;

import com.tuneup.tuneup.pricing.enums.Period;
import com.tuneup.tuneup.pricing.repositories.PriceRepository;
import com.tuneup.tuneup.pricing.services.PriceValidator;
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
import com.tuneup.tuneup.pricing.entities.Price;
import com.tuneup.tuneup.pricing.PriceDto;

@ExtendWith(MockitoExtension.class)
class PriceValidatorTests {

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
        when(priceRepository.findByPeriodAndRate(any(), anyDouble())).thenReturn(Optional.empty());
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        Set<Price> result = priceValidator.validateOrCreatePricing(priceDtos);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testStandardPricingInvalidPeriodThrowsException() {
        PriceDto invalidPeriodDto = new PriceDto();
        invalidPeriodDto.setId(3L);
        invalidPeriodDto.setPeriod("INVALID");
        invalidPeriodDto.setRate(100.0);
        invalidPeriodDto.setStandardPricing(true);
        Set<PriceDto> dtos = Collections.singleton(invalidPeriodDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> priceValidator.validateOrCreateStandardPricing(dtos));
        assertEquals("Invalid period: INVALID", exception.getMessage());
    }

    @Test
    void testStandardPricingInvalidRate_NullThrowsException() {
        PriceDto invalidRateDto = new PriceDto();
        invalidRateDto.setId(4L);
        invalidRateDto.setPeriod("ONE_HOUR");
        invalidRateDto.setRate(null);
        invalidRateDto.setStandardPricing(true);
        Set<PriceDto> dtos = Collections.singleton(invalidRateDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> priceValidator.validateOrCreateStandardPricing(dtos));
        assertEquals("Rate must be a positive number.", exception.getMessage());
    }

    @Test
    void testStandardPricingInvalidRate_NonPositiveThrowsException() {
        PriceDto invalidRateDto = new PriceDto();
        invalidRateDto.setId(5L);
        invalidRateDto.setPeriod("ONE_HOUR");
        invalidRateDto.setRate(0.0); // Non-positive rate
        invalidRateDto.setStandardPricing(true);
        Set<PriceDto> dtos = Collections.singleton(invalidRateDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> priceValidator.validateOrCreateStandardPricing(dtos));
        assertEquals("Rate must be a positive number.", exception.getMessage());
    }

    @Test
    void testCustomPricingInvalidPeriodThrowsException() {
        PriceDto invalidCustomDto = new PriceDto();
        invalidCustomDto.setId(6L);
        invalidCustomDto.setPeriod("INVALID");
        invalidCustomDto.setRate(100.0);
        invalidCustomDto.setStandardPricing(false);
        invalidCustomDto.setDescription("Custom price");
        Set<PriceDto> dtos = Collections.singleton(invalidCustomDto);


        assertThrows(IllegalArgumentException.class,
                () -> priceValidator.validateOrCreateCustomPricing(dtos));
    }

    @Test
    void testFetchAndValidateById_ValidId() {
        long id = 1L;
        Price expectedPrice = new Price();
        expectedPrice.setId(id);
        when(priceRepository.findById(id)).thenReturn(Optional.of(expectedPrice));

        Price result = priceValidator.fetchAndValidateById(id);
        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void testFetchAndValidateById_InvalidIdThrowsException() {
        long id = 999L;
        when(priceRepository.findById(id)).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> priceValidator.fetchAndValidateById(id));
        assertEquals("Price with that ID not found", exception.getMessage());
    }

    @Test
    void testStandardPricingExistingPriceDoesNotCallSave() {
        PriceDto dto = new PriceDto();
        dto.setId(7L);
        dto.setPeriod("ONE_HOUR");
        dto.setRate(100.0);
        dto.setStandardPricing(true);
        Set<PriceDto> dtos = Collections.singleton(dto);
        Price existingPrice = new Price();

        when(priceRepository.findByPeriodAndRate(Period.ONE_HOUR, 100.0))
                .thenReturn(Optional.of(existingPrice));

        Set<Price> result = priceValidator.validateOrCreateStandardPricing(dtos);
        verify(priceRepository, never()).save(any(Price.class));
        assertTrue(result.contains(existingPrice));
    }

    @Test
    void testCustomPricingExistingPriceDoesNotCallSave() {
        PriceDto dto = new PriceDto();
        dto.setId(8L);
        dto.setPeriod("CUSTOM");
        dto.setRate(200.0);
        dto.setStandardPricing(false);
        dto.setDescription("Special custom price");
        Set<PriceDto> dtos = Collections.singleton(dto);
        Price existingPrice = new Price();

        when(priceRepository.findByRateAndDescription(200.0, "Special custom price"))
                .thenReturn(Optional.of(existingPrice));

        Set<Price> result = priceValidator.validateOrCreateCustomPricing(dtos);
        verify(priceRepository, never()).save(any(Price.class));
        assertTrue(result.contains(existingPrice));
    }

    @Test
    void testValidateOrCreatePricingEmptySetReturnsEmptySet() {
        Set<PriceDto> emptySet = Collections.emptySet();
        Set<Price> result = priceValidator.validateOrCreatePricing(emptySet);
        assertTrue(result.isEmpty());
    }

    @Test
    void testValidateOrCreatePricingMultipleEntries() {
        // Create two standard pricing DTOs with distinct rates.
        PriceDto dto1 = new PriceDto();
        dto1.setId(9L);
        dto1.setPeriod("ONE_HOUR");
        dto1.setRate(100.0);
        dto1.setStandardPricing(true);

        PriceDto dto2 = new PriceDto();
        dto2.setId(10L);
        dto2.setPeriod("ONE_HOUR");
        dto2.setRate(200.0);
        dto2.setStandardPricing(true);

        Set<PriceDto> dtos = Set.of(dto1, dto2);

        // Simulate that no existing prices are found.
        when(priceRepository.findByPeriodAndRate(Period.ONE_HOUR, 100.0))
                .thenReturn(Optional.empty());
        when(priceRepository.findByPeriodAndRate(Period.ONE_HOUR, 200.0))
                .thenReturn(Optional.empty());

        // Create unique saved Price objects for each DTO.
        Price savedPrice1 = new Price();
        savedPrice1.setId(101L);
        savedPrice1.setPeriod(Period.ONE_HOUR);
        savedPrice1.setRate(100.0);
        savedPrice1.setStandardPricing(true);

        Price savedPrice2 = new Price();
        savedPrice2.setId(102L);
        savedPrice2.setPeriod(Period.ONE_HOUR);
        savedPrice2.setRate(200.0);
        savedPrice2.setStandardPricing(true);

        // Stub the repository save method to return distinct prices on consecutive calls.
        when(priceRepository.save(any(Price.class))).thenReturn(savedPrice1, savedPrice2);

        Set<Price> result = priceValidator.validateOrCreatePricing(dtos);
        assertEquals(2, result.size());
        assertTrue(result.contains(savedPrice1));
        assertTrue(result.contains(savedPrice2));
    }
}

