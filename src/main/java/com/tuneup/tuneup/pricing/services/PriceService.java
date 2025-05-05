package com.tuneup.tuneup.pricing.services;

import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.entities.Price;
import com.tuneup.tuneup.pricing.mappers.PriceMapper;
import com.tuneup.tuneup.pricing.repositories.PriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final PriceMapper priceMapper;
    private final PriceValidator priceValidator;

    public PriceService(PriceRepository priceRepository, PriceMapper priceMapper, PriceValidator priceValidator) {
        this.priceRepository = priceRepository;
        this.priceMapper = priceMapper;
        this.priceValidator = priceValidator;
    }

    /**
     * Create a new price
     * @param priceDto price to create
     * @return PriceDto newly created price aas Dto
     */
    @Transactional
    public PriceDto createPrice(PriceDto priceDto) {
        priceValidator.validatePriceDto(priceDto);
        Price persistedPrice = priceRepository.save(priceMapper.toPrice(priceDto));
        return priceMapper.toPriceDto(persistedPrice);
    }

    /**
     * Retrieve all existing prices from the database
     * @return Set PriceDto the set of existing price dtos
     */
    public Set<PriceDto> getAllPrices() {
        List<Price> allPrices = priceRepository.findAll();
        return allPrices.stream()
                .map(priceMapper::toPriceDto)
                .collect(Collectors.toSet());
    }

    /**
     * Retrieve all standard pricing from the database
     * @return Set priceDto - the set of existing standard pricing
     */
    public Set<PriceDto> getStandardPrices() {
        Set<Price> allPrices = priceRepository.findByStandardPricingTrue();
        return allPrices.stream()
                .map(priceMapper::toPriceDto)
                .collect(Collectors.toSet());
    }

    /**
     * Delete a price by its id
     * @param id price to delete
     * @return boolean result of operation ( true if successful)
     */
    public boolean deletePrice(Long id) {
        Price price = priceValidator.fetchAndValidateById(id);
        priceRepository.delete(price);
        return true;
    }

    /**
     * Retrieve a price by its id
     * @param id price to retrieve
     * @return existing price
     */
    public PriceDto getById(Long id) {
      return priceMapper.toPriceDto(priceValidator.fetchAndValidateById(id));
    }

}
