package com.inno.payment_service.dao;

import com.inno.payment_service.jpa.entity.Payment;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentDao {

    Payment save(Payment payment);

    List<Payment> findAll();

    List<Payment> findAllByOrderId(Long orderId);

    List<Payment> findAllByUserId(Long userId);

    List<Payment> findPaymentsByStatusIn(List<PaymentStatus> statuses);

    List<Payment> findPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> findPaymentsByUserIdBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
