package org.example.authserver.util.config;

import org.springframework.amqp.core.*;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ==================== 订单相关 ====================
    public static final String AUTH_EXCHANGE = "auth_exchange";
    public static final String AUTH_QUEUE = "create_user_queue";
    public static final String AUTH_KEY = "user.create";

    // 商户相关
    public static final String MERCHANT_EXCHANGE = "merchant_exchange";
    public static final String MERCHANT_QUEUE = "merchant.queue";
    public static final String MERCHANT_KEY = "merchant.create";

    @Bean
    public DirectExchange merchantExchange() {
        return new DirectExchange(MERCHANT_EXCHANGE, true, false);
    }

    @Bean
    public Queue merchantQueue() {
        return QueueBuilder.durable(MERCHANT_QUEUE).build();
    }
    @Bean
    public Binding merchantBinding() {
        return BindingBuilder.bind(merchantQueue())
                .to(merchantExchange())
                .with(MERCHANT_KEY);
    }


    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
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
