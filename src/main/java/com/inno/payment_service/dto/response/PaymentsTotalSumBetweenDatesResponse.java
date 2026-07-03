package com.inno.payment_service.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentsTotalSumBetweenDatesResponse {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer paymentsCount;
    private BigDecimal totalSum;

    public PaymentsTotalSumBetweenDatesResponse(LocalDateTime startDate, LocalDateTime endDate, Integer paymentsCount, BigDecimal totalSum) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentsCount = paymentsCount;
        this.totalSum = totalSum.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum.setScale(2, RoundingMode.HALF_UP);
    }
}
