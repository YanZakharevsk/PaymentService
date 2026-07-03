package com.inno.payment_service.kafka.events;

import com.inno.payment_service.dto.response.PaymentResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCreatedEvent {
    private final PaymentResponse paymentResponse;
}
