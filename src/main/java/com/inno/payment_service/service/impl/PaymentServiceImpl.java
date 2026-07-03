package com.inno.payment_service.service.impl;

import com.inno.payment_service.dao.PaymentDao;
import com.inno.payment_service.dto.mapper.PaymentMapper;
import com.inno.payment_service.dto.request.CreatePaymentRequest;
import com.inno.payment_service.dto.response.PaymentResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponseByUser;
import com.inno.payment_service.exception.OrderNotFoundException;
import com.inno.payment_service.jpa.entity.Payment;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import com.inno.payment_service.jpa.repository.PaymentRepository;
import com.inno.payment_service.kafka.events.PaymentCreatedEvent;
import com.inno.payment_service.service.PaymentService;
import com.inno.payment_service.webClient.RandomWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;
    private final PaymentMapper mapper;
    private final RandomWebClient randomWebClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentServiceImpl(PaymentDao paymentDao, PaymentMapper mapper, RandomWebClient randomWebClient, ApplicationEventPublisher applicationEventPublisher) {
        this.paymentDao = paymentDao;
        this.mapper = mapper;
        this.randomWebClient = randomWebClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = mapper.toEntity(request);
        payment.setTimestamp(LocalDateTime.now());
         if(randomWebClient.getRandomNumber()%2 == 0){
             payment.setStatus(PaymentStatus.SUCCESS);
         }else{
             payment.setStatus(PaymentStatus.FAILED);
         }
         Payment savedPayment = paymentDao.save(payment);
         PaymentResponse response = mapper.toResponse(savedPayment);

         applicationEventPublisher.publishEvent(new PaymentCreatedEvent(response));

         return response;
    }

    @Override
    public List<PaymentResponse> findAll() {
        return paymentDao.findAll().stream().map(mapper:: toResponse).toList();
    }

    @Override
    public List<PaymentResponse> findAllByOrderId(Long orderId) {
        List<PaymentResponse> responses = paymentDao.findAllByOrderId(orderId).stream().map(mapper:: toResponse).toList();
        if(responses.isEmpty()) throw new OrderNotFoundException("Order with id " + orderId + " not found");
        return responses;
    }

    @Override
    public List<PaymentResponse> findAllByUserId(Long userId) {
        return paymentDao.findAllByUserId(userId).stream().map(mapper:: toResponse).toList();
    }

    @Override
    public List<PaymentResponse> findPaymentsByStatusIn(List<PaymentStatus> statuses) {
        return paymentDao.findPaymentsByStatusIn(statuses).stream().map(mapper::toResponse).toList();
    }

    @Override
    public PaymentsTotalSumBetweenDatesResponse getTotalSumOfPaymentsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = paymentDao.findPaymentsBetween(startDate, endDate);

        BigDecimal totalSum = payments.stream().map(Payment ::getPaymentAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentsTotalSumBetweenDatesResponse(startDate, endDate, payments.size(), totalSum);
    }

    @Override
    public PaymentsTotalSumBetweenDatesResponseByUser getTotalSumOfPaymentsBetweenDatesByUser(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = paymentDao.findPaymentsByUserIdBetween(userId, startDate, endDate);

        BigDecimal totalSum = payments.stream().map(Payment ::getPaymentAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentsTotalSumBetweenDatesResponseByUser(userId, startDate, endDate, payments.size(), totalSum);
    }

    @Transactional
    public boolean isOrderOwner(Long orderId, Long userId){
        List<Payment> userPayments = paymentDao.findAllByUserId(userId);
        for (int i = 0; i < userPayments.size(); i++) {
            if(userPayments.get(i).getOrderId() == orderId){
                return true;
            }
        }
        return false;
    }
}
