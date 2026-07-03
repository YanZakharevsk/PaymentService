package com.inno.payment_service.service;

import com.inno.payment_service.dto.request.CreatePaymentRequest;
import com.inno.payment_service.dto.response.PaymentResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponseByUser;
import com.inno.payment_service.jpa.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    List<PaymentResponse> findAll();

    List<PaymentResponse> findAllByOrderId(Long orderId);

    List<PaymentResponse> findAllByUserId(Long userId);

    List<PaymentResponse> findPaymentsByStatusIn(List<PaymentStatus> statuses);

    PaymentsTotalSumBetweenDatesResponse getTotalSumOfPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    PaymentsTotalSumBetweenDatesResponseByUser getTotalSumOfPaymentsBetweenDatesByUser(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
