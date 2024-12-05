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
        //priceValidator.validatePriceDto(priceDto);
        Price persistedPrice = priceRepository.save(priceMapper.toPrice(priceDto));
        return priceMapper.toPriceDto(persistedPrice);
    }

    public Set<PriceDto> getAllPrices() {
        List<Price> allPrices = priceRepository.findAll();
        return allPrices.stream()
                .map(priceMapper::toPriceDto)
                .collect(Collectors.toSet());
    }
    public Remapper getPriceById(Long id) {
        return null;
    }

    public Price savePrice(Price updatedPrice) {
        return null;
    }

    public boolean deletePrice(Long id) {
        return false;
    }

    public PriceDto getById(Long id) {
       Price price  = priceRepository.findById(id).orElseThrow();
       return priceMapper.toPriceDto(price);
    }
}
