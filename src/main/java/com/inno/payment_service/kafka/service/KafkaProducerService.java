package com.inno.payment_service.kafka.service;

import com.inno.payment_service.kafka.config.KafkaConfigVars;
import com.inno.payment_service.kafka.dto.OrderKafkaDto;
import com.inno.payment_service.kafka.dto.PaymentKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    private static final String PAYMENT_TOPIC = KafkaConfigVars.PAYMENT_TOPIC;

    private final KafkaTemplate<String, PaymentKafkaDto> kafkaTemplate;


    public void sendPayment(PaymentKafkaDto paymentKafkaDto) {
        kafkaTemplate.send(PAYMENT_TOPIC, paymentKafkaDto).whenComplete((result, ex) ->
               logKafkaResult(ex, PAYMENT_TOPIC, paymentKafkaDto, result));
    }

    private void logKafkaResult(Throwable throwable, String topic, PaymentKafkaDto message, SendResult<String, PaymentKafkaDto> result) {
        if(throwable == null){
            log.info(
                    "TOPIC: {}. Sent message=[{}] with offset={}",
                    topic,
                    message,
                    result.getRecordMetadata().offset()
            );
        }else{
            log.error(
                    "TOPIC: {}. Unable to send message=[{}] due to: {}",
                    topic,
                    message,
                    throwable.getMessage(),
                    throwable
            );
        }
    }
}
