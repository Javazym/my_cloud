package org.example.authserver.mq;

import org.example.authserver.common.MessageWrapper;
import org.example.authserver.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    public void send(MessageWrapper<?> message) {
        rabbitTemplate.convertAndSend(RabbitConfig.AUTH_EXCHANGE, RabbitConfig.AUTH_KEY, message);
    }
}
