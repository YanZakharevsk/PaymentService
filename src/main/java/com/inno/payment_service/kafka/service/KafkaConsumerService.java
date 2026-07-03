package com.inno.payment_service.kafka.service;

import com.inno.payment_service.dto.mapper.PaymentMapper;
import com.inno.payment_service.kafka.config.KafkaConfigVars;
import com.inno.payment_service.kafka.config.KafkaTopicConfig;
import com.inno.payment_service.kafka.dto.OrderKafkaDto;
import com.inno.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private static final String ORDER_TOPIC = KafkaConfigVars.ORDER_TOPIC;

    private final PaymentService paymentService;
    private final PaymentMapper mapper;

    @KafkaListener(
            topics = ORDER_TOPIC,
            autoStartup = "true",
            containerFactory = "orderCreatedListenerContainerFactory"
    )
    public void listenOrder(
            @Payload OrderKafkaDto orderKafkaDto,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("TOPIC: {}. Received message=[{}] from partition {}",
                ORDER_TOPIC, orderKafkaDto, partition);

        paymentService.createPayment(mapper.kafkaToCreatePayment(orderKafkaDto));
    }
}
