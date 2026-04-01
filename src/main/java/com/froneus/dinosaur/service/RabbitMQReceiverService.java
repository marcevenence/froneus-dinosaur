package com.froneus.dinosaur.service;

import com.froneus.dinosaur.dto.DinosaurStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQReceiverService {

    //@RabbitListener(queues = "${rabbitmq.queue}")
    public void consumeMessage(DinosaurStatusMessage message) {
        log.info("Received message from RabbitMQ: {}", message);
        // Process the message (e.g., logging, notification, etc.)
    }
}
