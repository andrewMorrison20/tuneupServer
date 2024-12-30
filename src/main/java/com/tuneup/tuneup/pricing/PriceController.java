package com.tuneup.tuneup.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @PostMapping
    public ResponseEntity<PriceDto> createPrice(@RequestBody PriceDto priceDto) {
        PriceDto createdPrice = priceService.createPrice(priceDto);
        return ResponseEntity.ok(createdPrice);
    }

    @GetMapping("/findAll")
    public ResponseEntity<Set<PriceDto>> getAllPrices() {
        Set<PriceDto> priceDtos = priceService.getAllPrices();
        return ResponseEntity.ok(priceDtos);
    }

    @GetMapping("/standardPricing")
    public ResponseEntity<Set<PriceDto>> getStandardPrices() {
        Set<PriceDto> priceDtos = priceService.getStandardPrices();
        return ResponseEntity.ok(priceDtos);
    }

  @GetMapping("/{id}")
    public ResponseEntity<PriceDto> getPriceById(@PathVariable Long id) {
        PriceDto price = priceService.getById(id);
        return ResponseEntity.ok(price);
    }

    /*
    @PutMapping("/{id}")
    public ResponseEntity<PriceDto> updatePrice(@PathVariable Long id, @RequestBody PriceDto updatedPriceDto) {
        Optional<Price> existingPrice = priceService.getPriceById(id);
        if (existingPrice.isPresent()) {
            Price updatedPrice = priceMapper.toPrice(updatedPriceDto);
            updatedPrice.setId(id); // Ensure the ID remains unchanged
            Price savedPrice = priceService.savePrice(updatedPrice);
            return ResponseEntity.ok(priceMapper.toPriceDto(savedPrice));
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        if (priceService.deletePrice(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


