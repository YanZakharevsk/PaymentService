package com.inno.payment_service.kafka.config;

import com.inno.payment_service.kafka.dto.OrderKafkaDto;
import com.inno.payment_service.kafka.dto.OrderKafkaDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConfigVars vars;

    @Bean
    public ConsumerFactory<String, OrderKafkaDto> orderConsumerFactory() {
        JsonDeserializer<OrderKafkaDto> deserializer = new JsonDeserializer<>(OrderKafkaDto.class);
        deserializer.addTrustedPackages(vars.getTrustedPackages());
        deserializer.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(
                commonConsumerProps(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderKafkaDto>
    orderCreatedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderKafkaDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());
        return factory;
    }

    private Map<String, Object> commonConsumerProps() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, vars.getBootstrapAddress(),
                ConsumerConfig.GROUP_ID_CONFIG, vars.getGroupId(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
        );
    }
}
