package com.inno.payment_service.it.config;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
public abstract class BaseKafkaIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName
                    .parse("apache/kafka:3.7.0")
    );

    @DynamicPropertySource
    static void overrideKafkaProps(DynamicPropertyRegistry registry) {
        System.out.println("Kafka BootstrapServers: " + kafkaContainer.getBootstrapServers());
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

}
