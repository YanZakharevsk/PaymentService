package com.inno.payment_service.controller;

import com.inno.payment_service.dto.request.CreatePaymentRequest;
import com.inno.payment_service.dto.response.PaymentResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponseByUser;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import com.inno.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> addPayment (@Valid @RequestBody CreatePaymentRequest paymentRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(paymentRequest));
    }

    @GetMapping("/order-id/{orderId}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @paymentServiceImpl.isOrderOwner(#orderId, authentication.principal)")
    public ResponseEntity<List<PaymentResponse>> findAllByOrderId (@PathVariable Long orderId) {
        List<PaymentResponse> paymentResponses = paymentService.findAllByOrderId(orderId);

        if(paymentResponses.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponses);
    }

    @GetMapping("/user-id/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or #userId == authentication.principal")
    public ResponseEntity<List<PaymentResponse>> findAllByUserId (@PathVariable Long userId) {
        List<PaymentResponse> paymentResponses = paymentService.findAllByUserId(userId);

        if(paymentResponses.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.status(HttpStatus.OK).body(paymentResponses);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> findAll(@RequestParam(name = "statuses", required = false) List<PaymentStatus> statuses) {
        if (statuses == null){
            return ResponseEntity.status(HttpStatus.OK).body(paymentService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(paymentService.findPaymentsByStatusIn(statuses));
        }
    }

    @GetMapping("/amount")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PaymentsTotalSumBetweenDatesResponse> getTotalSumOfPaymentsBetween (
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate) {
        if(startDate == null){
            startDate = LocalDateTime.now();
        }
        if(endDate == null){
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.status(HttpStatus.OK).body(paymentService.getTotalSumOfPaymentsBetweenDates(startDate, endDate));
    }

    @GetMapping("/amount/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<PaymentsTotalSumBetweenDatesResponseByUser> getTotalSumOfPaymentsBetweenByUser
            (@AuthenticationPrincipal Long userId,
                    @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate) {

        if(startDate == null){
            startDate = LocalDateTime.now();
        }
        if(endDate == null){
            endDate = LocalDateTime.now();
        }

        return ResponseEntity.status(HttpStatus.OK).body(paymentService.getTotalSumOfPaymentsBetweenDatesByUser(userId, startDate, endDate));
    }

}
