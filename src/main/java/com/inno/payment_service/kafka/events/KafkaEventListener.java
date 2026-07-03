package com.inno.payment_service.kafka.events;


import com.inno.payment_service.dto.mapper.PaymentMapper;
import com.inno.payment_service.jpa.repository.PaymentRepository;
import com.inno.payment_service.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {
    private final PaymentMapper mapper;
    private final KafkaProducerService kafkaProducerService;
    private final PaymentRepository paymentRepository;

    @EventListener
    public void sendPaymentToKafka(PaymentCreatedEvent event) {
        kafkaProducerService.sendPayment(
                mapper.responseToKafkaDto(event.getPaymentResponse())
        );
    }
}
