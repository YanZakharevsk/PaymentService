package com.inno.payment_service.jpa.repository;

import com.inno.payment_service.jpa.entity.Payment;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findAllByOrderId(Long orderId);

    List<Payment> findAllByUserId(Long userId);

    List<Payment> findPaymentsByStatusIn(List<PaymentStatus> statuses);

    @Query(
            value = "{ 'timestamp': { $gte: ?0, $lte: ?1 } }"
    )
    List<Payment> findPaymentsBetween(LocalDateTime startDate, LocalDateTime endDate);


    @Query(
            value = "{ 'userId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }"
    )
    List<Payment> findPaymentsByUserIdBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
