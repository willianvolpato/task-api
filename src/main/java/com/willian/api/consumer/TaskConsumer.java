package com.willian.api.consumer;

import com.willian.api.model.TaskModelRequest;
import com.willian.api.service.TaskService;

import lombok.RequiredArgsConstructor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final TaskService taskService;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, TaskModelRequest> message) {
        taskService.create(message.value());
    }
}
