package com.grupp3.weather.service;

import com.grupp3.weather.config.RabbitMQConfig;
import com.grupp3.weather.dto.EmailVerificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailPublisher {
    private static final Logger log = LoggerFactory.getLogger(EmailPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EmailPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishVerificationEmail(String email, String username, String token) {
        EmailVerificationEvent event = new EmailVerificationEvent(email, username, token);
        
        log.info("Publishing verification email event for user: {}", username);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EMAIL_EXCHANGE,
            RabbitMQConfig.EMAIL_ROUTING_KEY,
            event
        );
        
        log.info("Verification email event published successfully");
    }
}
