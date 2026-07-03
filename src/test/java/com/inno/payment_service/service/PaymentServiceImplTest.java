package com.inno.payment_service.service;

import com.inno.payment_service.dao.PaymentDao;
import com.inno.payment_service.dto.mapper.PaymentMapper;
import com.inno.payment_service.dto.request.CreatePaymentRequest;
import com.inno.payment_service.dto.response.PaymentResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponseByUser;
import com.inno.payment_service.jpa.entity.Payment;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import com.inno.payment_service.service.impl.PaymentServiceImpl;
import com.inno.payment_service.webClient.RandomWebClient;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentDao paymentDao;
    @Mock
    private PaymentMapper mapper;
    @Mock
    private RandomWebClient randomWebClient;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Nested
    class CreatePayment {

        @Test
        void createPayment_shouldSetStatusSuccess_whenRandomEven() {
            CreatePaymentRequest request = new CreatePaymentRequest();
            PaymentResponse response = new PaymentResponse();
            Payment entity = new Payment();

            when(mapper.toEntity(request)).thenReturn(entity);
            when(randomWebClient.getRandomNumber()).thenReturn(2);
            when(paymentDao.save(entity)).thenReturn(entity);
            when(mapper.toResponse(entity)).thenReturn(response);

            PaymentResponse result = paymentService.createPayment(request);

            assertEquals(PaymentStatus.SUCCESS, entity.getStatus());
            verify(paymentDao).save(entity);
            assertThat(result).isEqualTo(response);

        }

        @Test
        void createPayment_shouldSetStatusFailed_whenRandomOdd() {
            CreatePaymentRequest request = new CreatePaymentRequest();
            PaymentResponse response = new PaymentResponse();
            Payment entity = new Payment();

            when(mapper.toEntity(request)).thenReturn(entity);
            when(randomWebClient.getRandomNumber()).thenReturn(3);
            when(paymentDao.save(entity)).thenReturn(entity);
            when(mapper.toResponse(entity)).thenReturn(response);

            PaymentResponse result = paymentService.createPayment(request);


            assertEquals(PaymentStatus.FAILED, entity.getStatus());
            verify(paymentDao).save(entity);
            assertThat(result).isEqualTo(response);
        }
    }

    @Nested
    class FindAll {

        @Test
        void findAll_shouldReturnMappedList() {
            Payment entity = new Payment();
            PaymentResponse response = new PaymentResponse();

            when(paymentDao.findAll()).thenReturn(List.of(entity));
            when(mapper.toResponse(entity)).thenReturn(response);

            List<PaymentResponse> result = paymentService.findAll();

            assertThat(result).hasSize(1).contains(response);
        }
    }

    @Nested
    class FindAllByOrderId {

        @Test
        void findAllByOrderId_shouldReturnMappedList() {
            Long orderId = 1L;
            Payment entity = new Payment();
            PaymentResponse response = new PaymentResponse();

            when(paymentDao.findAllByOrderId(orderId)).thenReturn(List.of(entity));
            when(mapper.toResponse(entity)).thenReturn(response);

            List<PaymentResponse> result = paymentService.findAllByOrderId(orderId);

            assertThat(result).hasSize(1).contains(response);
        }
    }

    @Nested
    class FindAllByUserId {

        @Test
        void findAllByUserId_shouldReturnMappedList() {
            Long userId = 10L;
            Payment entity = new Payment();
            PaymentResponse response = new PaymentResponse();

            when(paymentDao.findAllByUserId(userId)).thenReturn(List.of(entity));
            when(mapper.toResponse(entity)).thenReturn(response);

            List<PaymentResponse> result = paymentService.findAllByUserId(userId);

            assertThat(result).hasSize(1).contains(response);
        }
    }

    @Nested
    class FindPaymentsByStatusIn {

        @Test
        void findPaymentsByStatusIn_shouldReturnMappedList() {
            List<PaymentStatus> statuses = List.of(PaymentStatus.SUCCESS);
            Payment entity = new Payment();
            PaymentResponse response = new PaymentResponse();

            when(paymentDao.findPaymentsByStatusIn(statuses)).thenReturn(List.of(entity));
            when(mapper.toResponse(entity)).thenReturn(response);

            List<PaymentResponse> result = paymentService.findPaymentsByStatusIn(statuses);

            assertThat(result).hasSize(1).contains(response);
        }
    }

    @Nested
    class GetTotalSumOfPaymentsBetweenDates {

        @Test
        void getTotalSumOfPaymentsBetweenDates_shouldReturnCorrectSum() {
            LocalDateTime start = LocalDateTime.now().minusDays(1);
            LocalDateTime end = LocalDateTime.now();

            Payment p1 = new Payment();
            p1.setPaymentAmount(BigDecimal.valueOf(100));

            Payment p2 = new Payment();
            p2.setPaymentAmount(BigDecimal.valueOf(200));

            when(paymentDao.findPaymentsBetween(start, end)).thenReturn(List.of(p1, p2));

            PaymentsTotalSumBetweenDatesResponse response = paymentService.getTotalSumOfPaymentsBetweenDates(start, end);

            assertThat(response.getTotalSum()).isEqualByComparingTo(BigDecimal.valueOf(300));
            assertThat(response.getPaymentsCount()).isEqualTo(2);
        }
    }

    @Nested
    class GetTotalSumOfPaymentsBetweenDatesByUser {

        @Test
        void getTotalSumOfPaymentsBetweenDatesByUser_shouldReturnCorrectSum() {
            Long userId = 1L;
            LocalDateTime start = LocalDateTime.now().minusDays(1);
            LocalDateTime end = LocalDateTime.now();

            Payment p1 = new Payment();
            p1.setPaymentAmount(BigDecimal.valueOf(150));

            Payment p2 = new Payment();
            p2.setPaymentAmount(BigDecimal.valueOf(250));

            when(paymentDao.findPaymentsByUserIdBetween(userId, start, end)).thenReturn(List.of(p1, p2));

            PaymentsTotalSumBetweenDatesResponseByUser response =
                    paymentService.getTotalSumOfPaymentsBetweenDatesByUser(userId, start, end);

            assertThat(response.getTotalSum()).isEqualByComparingTo(BigDecimal.valueOf(400));
            assertThat(response.getPaymentsCount()).isEqualTo(2);
            assertThat(response.getUserId()).isEqualTo(userId);
        }
    }
}