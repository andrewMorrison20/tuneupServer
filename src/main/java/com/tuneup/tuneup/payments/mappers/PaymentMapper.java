package com.tuneup.tuneup.payments.mappers;

import com.tuneup.tuneup.payments.entities.Payment;
import com.tuneup.tuneup.payments.dtos.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "tuition.id", target = "tuitionId")
    @Mapping(source = "lesson.id", target = "lessonId")
    @Mapping(source= "lesson.availability.startTime",target = "lessonDate")
    PaymentDto toDto(Payment payment);


    Payment toEntity(PaymentDto paymentDto);


}
