package com.froneus.dinosaur.service;

import com.froneus.dinosaur.dto.DinosaurStatusMessage;
import com.froneus.dinosaur.model.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQSenderService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Async
    public void sendStatusMessage(String dinosaurId, Status newStatus) {
        DinosaurStatusMessage message = DinosaurStatusMessage.builder()
                .dinosaurId(dinosaurId)
                .newStatus(newStatus)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            log.info("Attempting to send message to RabbitMQ: {}", message);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("Message successfully sent to RabbitMQ: {}", message);
        } catch (AmqpException e) {
            log.error("Failed to send message to RabbitMQ after retries for dinosaurId: {}. Error: {}", dinosaurId, e.getMessage());
        } catch (Exception e) {
            log.error("An unexpected error occurred while sending message to RabbitMQ for dinosaurId: {}. Error: {}", dinosaurId, e.getMessage());
        }
    }
}
