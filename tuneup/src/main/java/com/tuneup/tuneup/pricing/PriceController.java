package com.tuneup.tuneup.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @Autowired
    private PriceMapper priceMapper;

    @PostMapping
    public ResponseEntity<PriceDto> createPrice(@RequestBody PriceDto priceDto) {
        PriceDto createdPrice = priceService.createPrice(priceDto);
        return ResponseEntity.ok(createdPrice);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<PriceDto>> getAllPrices() {
        List<Price> prices = priceService.getAllPrices();
        List<PriceDto> priceDTOs = prices.stream()
                .map(priceMapper::toPriceDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(priceDTOs);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PriceDto> getPriceById(@PathVariable Long id) {
        return priceService.getPriceById(id)
                .map(price -> ResponseEntity.ok(priceMapper.toPriceDto(price)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


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
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        if (priceService.deletePrice(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


