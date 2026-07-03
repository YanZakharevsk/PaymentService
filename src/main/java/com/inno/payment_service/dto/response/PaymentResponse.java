package com.inno.payment_service.dto.response;

import com.inno.payment_service.jpa.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private String id;
    private Long orderId;
    private Long userId;
    private PaymentStatus paymentStatus;
    private LocalDateTime timestamp;
    private BigDecimal paymentAmount;
}
