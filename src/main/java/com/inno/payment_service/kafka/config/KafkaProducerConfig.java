package com.inno.payment_service.kafka.config;

import com.inno.payment_service.kafka.dto.PaymentKafkaDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaConfigVars vars;

    @Bean
    public ProducerFactory<String, PaymentKafkaDto> paymentProducerFactory() {
        return createProducerFactory();
    }

    @Bean
    public KafkaTemplate<String, PaymentKafkaDto> paymentKafkaTemplate() {
        return new KafkaTemplate<>(paymentProducerFactory());
    }

    private <T> ProducerFactory<String, T> createProducerFactory() {
        Map<String, Object> configProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, vars.getBootstrapAddress(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
