package org.example.shoppingserver.util.config;

import org.springframework.amqp.core.*;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ==================== 订单相关（一对多）====================
    public static final String ORDER_EXCHANGE = "order_exchange";
    
    // 创建订单
    public static final String ORDER_CREATE_QUEUE = "order.create.queue";
    public static final String ORDER_CREATE_KEY = "order.create";
    
    // 取消订单
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    public static final String ORDER_CANCEL_KEY = "order.cancel";
    
    // 支付订单
    public static final String ORDER_PAY_QUEUE = "order.pay.queue";
    public static final String ORDER_PAY_KEY = "order.pay";
    
    // 完成订单
    public static final String ORDER_COMPLETE_QUEUE = "order.complete.queue";
    public static final String ORDER_COMPLETE_KEY = "order.complete";
    // ==================== 订单延迟队列相关 ====================
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_KEY = "order.delay";

    public static final String ORDER_DEAD_LETTER_EXCHANGE = "order.dead.letter.exchange";
    public static final String ORDER_DEAD_LETTER_QUEUE = "order.dead.letter.queue";
    public static final String ORDER_DEAD_LETTER_KEY = "order.dead.letter";
    // ==================== 认证相关 ====================
    public static final String AUTH_EXCHANGE = "auth_exchange";
    public static final String AUTH_QUEUE = "create_user_queue";
    public static final String AUTH_KEY = "user.create";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 订单交换机（统一处理所有订单相关消息）
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * 创建订单队列
     */
    @Bean
    public Queue orderCreateQueue() {
        return QueueBuilder.durable(ORDER_CREATE_QUEUE).build();
    }

    /**
     * 创建订单队列绑定
     */
    @Bean
    public Binding orderCreateBinding() {
        return BindingBuilder.bind(orderCreateQueue())
                .to(orderExchange())
                .with(ORDER_CREATE_KEY);
    }
    
    /**
     * 取消订单队列
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable(ORDER_CANCEL_QUEUE).build();
    }

    /**
     * 取消订单队列绑定
     */
    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue())
                .to(orderExchange())
                .with(ORDER_CANCEL_KEY);
    }
    
    /**
     * 支付订单队列
     */
    @Bean
    public Queue orderPayQueue() {
        return QueueBuilder.durable(ORDER_PAY_QUEUE).build();
    }

    /**
     * 支付订单队列绑定
     */
    @Bean
    public Binding orderPayBinding() {
        return BindingBuilder.bind(orderPayQueue())
                .to(orderExchange())
                .with(ORDER_PAY_KEY);
    }
    
    /**
     * 完成订单队列
     */
    @Bean
    public Queue orderCompleteQueue() {
        return QueueBuilder.durable(ORDER_COMPLETE_QUEUE).build();
    }

    /**
     * 完成订单队列绑定
     */
    @Bean
    public Binding orderCompleteBinding() {
        return BindingBuilder.bind(orderCompleteQueue())
                .to(orderExchange())
                .with(ORDER_COMPLETE_KEY);
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


    /**
     * 死信交换机(实际处理订单的交换机)
     */
    @Bean
    public DirectExchange orderDeadLetterExchange() {
        return new DirectExchange(ORDER_DEAD_LETTER_EXCHANGE, true, false);
    }

    /**
     * 死信队列(实际消费订单超时的队列)
     */
    @Bean
    public Queue orderDeadLetterQueue() {
        return QueueBuilder.durable(ORDER_DEAD_LETTER_QUEUE).build();
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding orderDeadLetterBinding() {
        return BindingBuilder.bind(orderDeadLetterQueue())
                .to(orderDeadLetterExchange())
                .with(ORDER_DEAD_LETTER_KEY);
    }

    /**
     * 延迟交换机
     */
    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE, true, false);
    }

    /**
     * 延迟队列(设置死信交换机和TTL)
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(ORDER_DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_DEAD_LETTER_KEY)
                .withArgument("x-message-ttl", 1800000) // 默认30分钟超时,单位毫秒
                .build();
    }

    /**
     * 延迟队列绑定
     */
    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue())
                .to(orderDelayExchange())
                .with(ORDER_DELAY_KEY);
    }
}
