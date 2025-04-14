package com.tuneup.tuneup.junit.services;

import com.tuneup.tuneup.pricing.repositories.PriceRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tuneup.tuneup.pricing.mappers.PriceMapper;
import com.tuneup.tuneup.pricing.services.PriceValidator;
import com.tuneup.tuneup.pricing.services.PriceService;
import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.entities.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;


import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTests {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceMapper priceMapper;

    @Mock
    private PriceValidator priceValidator;

    @InjectMocks
    private PriceService priceService;

    private PriceDto priceDto;
    private Price price;

    @BeforeEach
    void setUp() {
        priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setDescription("test");
        priceDto.setRate(100.0);
        price = new Price();
    }

    @Test
    void createPrice_ShouldReturnCreatedPriceDto() {
        when(priceMapper.toPrice(priceDto)).thenReturn(price);
        when(priceRepository.save(price)).thenReturn(price);
        when(priceMapper.toPriceDto(price)).thenReturn(priceDto);

        PriceDto result = priceService.createPrice(priceDto);
        assertNotNull(result);
        assertEquals(priceDto, result);
    }

    @Test
    void getAllPrices_ShouldReturnSetOfPriceDtos() {
        when(priceRepository.findAll()).thenReturn(Collections.singletonList(price));
        when(priceMapper.toPriceDto(price)).thenReturn(priceDto);

        Set<PriceDto> result = priceService.getAllPrices();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getStandardPrices_ShouldReturnSetOfStandardPrices() {
        when(priceRepository.findByStandardPricingTrue()).thenReturn(Collections.singleton(price));
        when(priceMapper.toPriceDto(price)).thenReturn(priceDto);

        Set<PriceDto> result = priceService.getStandardPrices();
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
