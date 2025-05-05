package com.tuneup.tuneup.pricing.controllers;

import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    /**
     * Create a new price
     * @param priceDto the price to create
     * @return the newly created price
     */
    @PostMapping
    public ResponseEntity<PriceDto> createPrice(@RequestBody PriceDto priceDto) {
        PriceDto createdPrice = priceService.createPrice(priceDto);
        return ResponseEntity.ok(createdPrice);
    }

    /**
     * Retrieve all prices from the DB
     * @return the set of prices in the db
     */
    @GetMapping("/findAll")
    public ResponseEntity<Set<PriceDto>> getAllPrices() {
        Set<PriceDto> priceDtos = priceService.getAllPrices();
        return ResponseEntity.ok(priceDtos);
    }

    /**
     * Get all standard pricing from the db
     * @return the set of standard pricing
     */
    @GetMapping("/standardPricing")
    public ResponseEntity<Set<PriceDto>> getStandardPrices() {
        Set<PriceDto> priceDtos = priceService.getStandardPrices();
        return ResponseEntity.ok(priceDtos);
    }

    /**
     * Get a price from the db by its Id
     * @param id id of the price to retrieve
     * @return the price as a dto
     */
  @GetMapping("/{id}")
    public ResponseEntity<PriceDto> getPriceById(@PathVariable Long id) {
        PriceDto price = priceService.getById(id);
        return ResponseEntity.ok(price);
    }


    /**
     * Delete a price by id
     * @param id price to delete
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrice(@PathVariable Long id) {
        if (priceService.deletePrice(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


