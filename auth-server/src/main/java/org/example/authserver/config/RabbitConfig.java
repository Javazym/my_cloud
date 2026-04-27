package org.example.authserver.config;

import org.springframework.amqp.core.*;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ==================== 订单相关 ====================
    public static final String AUTH_EXCHANGE = "auth_exchange";
    public static final String AUTH_QUEUE = "create_user_queue";
    public static final String AUTH_KEY = "user.create";

    @Bean
    public DirectExchange authExchange() {
        return new DirectExchange(AUTH_EXCHANGE, true, false);
    }

    @Bean
    public Queue authQueue() {
        return QueueBuilder.durable(AUTH_QUEUE).build();
    }

    @Bean
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue())
                .to(authExchange())
                .with(AUTH_KEY);
    }
}
