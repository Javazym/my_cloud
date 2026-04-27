package org.example.authserver.mq;

import org.example.authserver.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void send(Message message) {
        rabbitTemplate.convertAndSend(RabbitConfig.AUTH_QUEUE, RabbitConfig.AUTH_KEY, message);
    }
}
