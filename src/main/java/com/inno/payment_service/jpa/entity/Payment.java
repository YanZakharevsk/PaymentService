package com.inno.payment_service.jpa.entity;

import com.inno.payment_service.jpa.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "payments")
@NoArgsConstructor
@Getter
@Setter
public class Payment{

    @Id
    private String id;

    @Field("order_id")
    @Indexed
    private Long orderId;

    @Field("user_id")
    @Indexed
    private Long userId;

    @Indexed
    private PaymentStatus status;

    private LocalDateTime timestamp;

    @Field("payment_amount")
    private BigDecimal paymentAmount;

    public Payment(String id, Long orderId, Long userId, PaymentStatus status, BigDecimal paymentAmount, LocalDateTime timestamp) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.paymentAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
        this.timestamp = Objects.requireNonNullElseGet(timestamp, LocalDateTime::now);
    }

    public void setTimestamp() {
        this.timestamp = Objects.requireNonNullElseGet(timestamp, LocalDateTime::now);
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
