package com.inno.payment_service.dao.impl;

import com.inno.payment_service.dao.PaymentDao;
import com.inno.payment_service.jpa.entity.Payment;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import com.inno.payment_service.jpa.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentDaoImpl implements PaymentDao {

    private final PaymentRepository paymentRepository;

    public PaymentDaoImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> findAllByOrderId(Long orderId) {
        return paymentRepository.findAllByOrderId(orderId);
    }

    @Override
    public List<Payment> findAllByUserId(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }

    @Override
    public List<Payment> findPaymentsByStatusIn(List<PaymentStatus> statuses) {
        return paymentRepository.findPaymentsByStatusIn(statuses);
    }

    @Override
    public List<Payment> findPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findPaymentsBetween(startDate, endDate);
    }

    @Override
    public List<Payment> findPaymentsByUserIdBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findPaymentsByUserIdBetween(userId, startDate, endDate);
    }
}
