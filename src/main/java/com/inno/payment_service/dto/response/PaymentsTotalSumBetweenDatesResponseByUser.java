package com.inno.payment_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
public class PaymentsTotalSumBetweenDatesResponseByUser {
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer paymentsCount;
    private BigDecimal totalSum;

    public PaymentsTotalSumBetweenDatesResponseByUser(Long userId, LocalDateTime startDate, LocalDateTime endDate, Integer paymentsCount, BigDecimal totalSum) {
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentsCount = paymentsCount;
        this.totalSum = totalSum;
    }

    public void setTotalSum(BigDecimal totalSum) {
        this.totalSum = totalSum.setScale(2, RoundingMode.HALF_UP);
    }
}
