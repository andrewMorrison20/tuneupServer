package com.tuneup.tuneup.pricing;

import aj.org.objectweb.asm.commons.Remapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    @Transactional
    public PriceDto createPrice(PriceDto priceDto) {
        priceValidator.validatePriceDto(priceDto);
        Price persistedPrice = priceRepository.save(priceMapper.toPrice(priceDto));
        return priceMapper.toPriceDto(persistedPrice);
    }

    public Set<PriceDto> getAllPrices() {
        List<Price> allPrices = priceRepository.findAll();
        return allPrices.stream()
                .map(priceMapper::toPriceDto)
                .collect(Collectors.toSet());
    }

    public Set<PriceDto> getStandardPrices() {
        Set<Price> allPrices = priceRepository.findByStandardPricingTrue();
        return allPrices.stream()
                .map(priceMapper::toPriceDto)
                .collect(Collectors.toSet());
    }


    public boolean deletePrice(Long id) {
        Price price = priceValidator.fetchAndValidateById(id);
        priceRepository.delete(price);
        return true;
    }

    public PriceDto getById(Long id) {
      return priceMapper.toPriceDto(priceValidator.fetchAndValidateById(id));
    }

}
