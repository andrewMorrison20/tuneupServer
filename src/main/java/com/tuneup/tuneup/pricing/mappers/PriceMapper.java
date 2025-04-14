package com.tuneup.tuneup.pricing.mappers;

import com.tuneup.tuneup.pricing.PriceDto;
import com.tuneup.tuneup.pricing.entities.Price;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring" )
public interface PriceMapper {

    PriceDto toPriceDto(Price price);

    Price toPrice(PriceDto priceDto);
}
