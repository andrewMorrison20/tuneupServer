package com.tuneup.tuneup.payments.mappers;

import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "tuition.id", target = "tuitionId")
    PaymentDto toDto(Payment payment);

    Payment toEntity(PaymentDto paymentDto);


}
