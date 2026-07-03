package com.inno.payment_service.it;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.inno.payment_service.controller.PaymentController;
import com.inno.payment_service.dto.request.CreatePaymentRequest;
import com.inno.payment_service.dto.response.PaymentResponse;
import com.inno.payment_service.dto.response.PaymentsTotalSumBetweenDatesResponse;
import com.inno.payment_service.it.config.BaseKafkaIntegrationTest;
import com.inno.payment_service.it.config.ConsumerConfigTest;
import com.inno.payment_service.it.config.ProducerConfigTest;
import com.inno.payment_service.jpa.enums.PaymentStatus;
import com.inno.payment_service.jpa.repository.PaymentRepository;
import com.inno.payment_service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock(
        @ConfigureWireMock(name = "random-number-api", port = 8087)
)
@Testcontainers
@Import({ProducerConfigTest.class, ConsumerConfigTest.class})
public class PaymentControllerIntegrationTest extends BaseKafkaIntegrationTest {
    private static final String MIN = "1";
    private static final String MAX = "50";
    private static final String COUNT = "1";
    private static final Long ORDER_ID = 100L;
    private static final Long USER_ID = 100L;
    private static final BigDecimal PAYMENT_AMOUNT = BigDecimal.valueOf(120);
    private static final Random RANDOM = new Random();

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentController paymentController;
    @MockitoSpyBean
    private PaymentRepository paymentRepository;

    @InjectWireMock("random-number-api")
    WireMockServer mockRandomNumberApi;


    @Container
    @ServiceConnection
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:6.0.15");

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("random.number.api.url", () -> "http://localhost:8087/api/v1.0/random");
        registry.add("random.number.api.param.min", () -> MIN);
        registry.add("random.number.api.param.max", () -> MAX);
        registry.add("random.number.api.param.count", () -> COUNT);
    }

    @BeforeEach
    void setUp () {
        paymentRepository.deleteAll();
        mockRandomNumber(RANDOM.nextBoolean());
    }

    void mockRandomNumber(Boolean evenNumber) {
        mockRandomNumberApi.stubFor(get(urlPathEqualTo("/api/v1.0/random"))

                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(evenNumber ? "4" : "3")
                )
        );
    }


    @Nested
    class AddPayment {
        @Test
        void evenNumber_shouldSetSuccessStatus() {
            mockRandomNumber(true);
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(ORDER_ID);
            post.setUserId(USER_ID);
            post.setPaymentAmount(PAYMENT_AMOUNT);

            ResponseEntity<PaymentResponse> response =
                    paymentController.addPayment(post);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getOrderId()).isEqualTo(ORDER_ID);
            assertThat(response.getBody().getUserId()).isEqualTo(USER_ID);
            assertThat(response.getBody().getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(paymentRepository.findAll().size()).isEqualTo(1);
        }
        @Test
        void oddNumber_shouldSetSuccessStatus() {
            mockRandomNumber(false);
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(ORDER_ID);
            post.setUserId(USER_ID);
            post.setPaymentAmount(PAYMENT_AMOUNT);

            ResponseEntity<PaymentResponse> response =
                    paymentController.addPayment(post);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getOrderId()).isEqualTo(ORDER_ID);
            assertThat(response.getBody().getUserId()).isEqualTo(USER_ID);
            assertThat(response.getBody().getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
            assertThat(paymentRepository.findAll().size()).isEqualTo(1);
        }
    }


    @Nested
    class FindAllByOrderId {
        @Test
        void shouldFindAllByOrderId() {
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(ORDER_ID);
            post.setUserId(USER_ID);
            post.setPaymentAmount(PAYMENT_AMOUNT);
            paymentService.createPayment(post);

            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAllByOrderId(ORDER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
            assertThat(response.getBody().get(0).getOrderId()).isEqualTo(ORDER_ID);
        }
        @Test
        void shouldReturn204_whenEmpty() {
            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAllByOrderId(ORDER_ID);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isEmpty();
        }
    }


    @Nested
    class FindAllByUserId {
        @Test
        void shouldFindAllByUserId() {
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(50L);
            post.setUserId(USER_ID);
            post.setPaymentAmount(PAYMENT_AMOUNT);
            paymentService.createPayment(post);

            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAllByUserId(USER_ID);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
            assertThat(response.getBody().get(0).getUserId()).isEqualTo(USER_ID);
        }
        @Test
        void shouldReturn204_whenEmpty() {
            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAllByUserId(USER_ID);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isEmpty();
        }
    }


    @Nested
    class FindAll {
        @Test
        void shouldFindAllPayments() {
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(44L);
            post.setUserId(44L);
            post.setPaymentAmount(PAYMENT_AMOUNT);
            paymentService.createPayment(post);

            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAll(null);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotEmpty();
        }
        @Test
        void shouldFindPaymentsByStatusIn() {
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(33L);
            post.setUserId(33L);
            post.setPaymentAmount(PAYMENT_AMOUNT);
            paymentService.createPayment(post);

            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAll(List.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED));

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
            assertThat(response.getBody()).isNotEmpty();
            assertThat(response.getBody().get(0).getPaymentStatus())
                    .isIn(PaymentStatus.SUCCESS, PaymentStatus.FAILED);
        }
        @Test
        void shouldReturn204_whenEmpty() {
            ResponseEntity<List<PaymentResponse>> response =
                    paymentController.findAll(null);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEmpty();
        }
    }


    @Nested
    class GetTotalSumOfPaymentsBetween {
        @Test
        void shouldGetTotalSumOfPaymentsBetween() {
            CreatePaymentRequest post = new CreatePaymentRequest();
            post.setOrderId(22L);
            post.setUserId(22L);
            post.setPaymentAmount(PAYMENT_AMOUNT);
            paymentService.createPayment(post);

            LocalDateTime start = LocalDateTime.now().minusMinutes(5);
            LocalDateTime end = LocalDateTime.now().plusMinutes(5);

            ResponseEntity<PaymentsTotalSumBetweenDatesResponse> response =
                    paymentController.getTotalSumOfPaymentsBetween(start, end);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTotalSum()).isGreaterThan(BigDecimal.ZERO);
            verify(paymentRepository, times(1)).findPaymentsBetween(any(), any());
        }
    }

}
