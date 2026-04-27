package org.example.shoppingserver.util.config;

import org.springframework.amqp.core.*;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ==================== 订单相关 ====================
    public static final String ORDER_EXCHANGE = "order_exchange";
    public static final String ORDER_QUEUE = "create_order_queue";
    public static final String ORDER_KEY = "order.create";

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_KEY);
    }

//    // ==================== 用户相关 ====================
//    public static final String USER_EXCHANGE = "user.exchange";
//    public static final String USER_QUEUE = "user.queue";
//    public static final String USER_KEY = "user.key";
//
//    @Bean
//    public DirectExchange userExchange() {
//        return new DirectExchange(USER_EXCHANGE, true, false);
//    }
//
//    @Bean
//    public Queue userQueue() {
//        return QueueBuilder.durable(USER_QUEUE).build();
//    }
//
//    @Bean
//    public Binding userBinding() {
//        return BindingBuilder.bind(userQueue())
//                .to(userExchange())
//                .with(USER_KEY);
//    }
}
