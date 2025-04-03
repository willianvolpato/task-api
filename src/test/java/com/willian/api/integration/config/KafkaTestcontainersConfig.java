package com.willian.api.integration.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
@Testcontainers
public class KafkaTestcontainersConfig {

    private static KafkaContainer KAFKA_CONTAINER;

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.replication.factor:1}")
    private int replicationFactor;

    @Value("${spring.kafka.partition.number:1}")
    private int partitionNumber;

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("apache/kafka:latest"));
        return KAFKA_CONTAINER;
    }

    @Bean
    public NewTopic createNewTopic() {
        return new NewTopic(topic, partitionNumber, (short) replicationFactor);
    }
}
