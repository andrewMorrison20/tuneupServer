package com.tuneup.tuneup.junit.controllers;



import com.tuneup.tuneup.pricing.controllers.PriceController;
import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.services.PriceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceControllerTests {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

    @Test
    void testCreatePriceReturnsDto() {
        PriceDto priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setDescription("test");
        priceDto.setRate(100.0);
        priceDto.setPeriod("one hour");
        when(priceService.createPrice(priceDto)).thenReturn(priceDto);

        ResponseEntity<PriceDto> response = priceController.createPrice(priceDto);
        assertNotNull(response.getBody());
        assertEquals(priceDto, response.getBody());
    }

    @Test
    void testGetAllPricesReturnsDtoSet() {
        PriceDto priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setDescription("test");
        priceDto.setRate(100.0);
        priceDto.setPeriod("one hour");
        Set<PriceDto> priceSet = Collections.singleton(priceDto);
        when(priceService.getAllPrices()).thenReturn(priceSet);

        ResponseEntity<Set<PriceDto>> response = priceController.getAllPrices();
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetStandardPricesReturnsDtoSet() {
        PriceDto priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setDescription("test");
        priceDto.setRate(100.0);
        priceDto.setPeriod("one hour");

        Set<PriceDto> priceSet = Collections.singleton(priceDto);

        Set<PriceDto> standardPrices = Collections.singleton(priceDto);
        when(priceService.getStandardPrices()).thenReturn(standardPrices);

        ResponseEntity<Set<PriceDto>> response = priceController.getStandardPrices();
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetPriceByIdReturnsDto() {
        PriceDto priceDto = new PriceDto();
        priceDto.setId(1L);
        priceDto.setDescription("test");
        priceDto.setRate(100.0);
        priceDto.setPeriod("one hour");

        when(priceService.getById(1L)).thenReturn(priceDto);

        ResponseEntity<PriceDto> response = priceController.getPriceById(1L);
        assertNotNull(response.getBody());
        assertEquals(priceDto, response.getBody());
    }

    @Test
    void testDeletePriceSuccess() {
        when(priceService.deletePrice(1L)).thenReturn(true);

        ResponseEntity<Void> response = priceController.deletePrice(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void testDeletePriceReturns404() {
        when(priceService.deletePrice(1L)).thenReturn(false);

        ResponseEntity<Void> response = priceController.deletePrice(1L);
        assertEquals(404, response.getStatusCode().value());
    }
}
