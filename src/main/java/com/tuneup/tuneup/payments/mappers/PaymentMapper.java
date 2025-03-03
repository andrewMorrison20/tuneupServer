package com.tuneup.tuneup.payments.mappers;

import com.tuneup.tuneup.payments.Payment;
import com.tuneup.tuneup.payments.PaymentDto;
import com.tuneup.tuneup.tuitions.TuitionMapper;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring", uses = {TuitionMapper.class} )
public interface PaymentMapper {

    PaymentDto toDto(Payment payment);

    Payment toEntity(PaymentDto paymentDto);


}
