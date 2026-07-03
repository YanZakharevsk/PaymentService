package com.inno.payment_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "Payment ID can not be null")
    private Long orderId;

    @NotNull(message = "User ID can not be null")
    private Long userId;

    @NotNull(message = "Payment amount can not be null")
    @Positive(message = "Payment amount must be positive")
    @Min(0)
    private BigDecimal paymentAmount;

    public CreatePaymentRequest(Long orderId, Long userId, BigDecimal paymentAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.paymentAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
