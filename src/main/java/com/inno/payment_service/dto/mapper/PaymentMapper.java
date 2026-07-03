package com.inno.payment_service.dto.mapper;

import com.inno.payment_service.dto.request.CreatePaymentRequest;
import com.inno.payment_service.dto.response.PaymentResponse;
import com.inno.payment_service.jpa.entity.Payment;
import com.inno.payment_service.kafka.dto.OrderKafkaDto;
import com.inno.payment_service.kafka.dto.PaymentKafkaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Payment toEntity(CreatePaymentRequest request);

    @Mapping(target = "paymentStatus", source = "status")
    PaymentResponse toResponse(Payment payment);

    @Mapping(source = "id", target = "paymentId")
    PaymentKafkaDto responseToKafkaDto(PaymentResponse response);

    CreatePaymentRequest kafkaToCreatePayment(OrderKafkaDto orderKafkaDto);
}
