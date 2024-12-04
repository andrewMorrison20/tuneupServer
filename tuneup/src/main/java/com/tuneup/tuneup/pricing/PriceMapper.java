package com.tuneup.tuneup.pricing;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface PriceMapper {

    PriceDto toPriceDto(Price price);

    Price toPrice(PriceDto priceDto);
}
