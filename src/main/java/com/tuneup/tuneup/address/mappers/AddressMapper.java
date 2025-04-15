package com.tuneup.tuneup.address.mappers;

import com.tuneup.tuneup.address.dtos.AddressDto;
import com.tuneup.tuneup.address.entities.Address;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDto toDto(Address address);

    Address toAddress(AddressDto addressDto);

}
