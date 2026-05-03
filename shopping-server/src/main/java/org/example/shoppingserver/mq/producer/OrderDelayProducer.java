package org.example.shoppingserver.mq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单延迟队列生产者
 * 用于发送订单超时取消的延迟消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDelayProducer {

    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 发送订单超时延迟消息
     * 
     * @param orderId 订单ID
     * @param timeoutMillis 超时时间（毫秒），默认30分钟
     */
    public void sendOrderTimeoutMessage(Long orderId, Long timeoutMillis) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("timeoutMillis", timeoutMillis != null ? timeoutMillis : 1800000L); // 默认30分钟
        
        MessageWrapper<Map<String, Object>> message = MessageWrapper.<Map<String, Object>>builder()
                .data(data)
                .sourceService("order-service")
                .targetService("order-timeout-consumer")
                .build();
        
        // 发送到延迟交换机，消息会在TTL过期后转发到死信队列
        rabbitTemplate.convertAndSend(
            RabbitConfig.ORDER_DELAY_EXCHANGE, 
            RabbitConfig.ORDER_DELAY_KEY, 
            message
        );
        
        log.info("发送订单超时延迟消息: orderId={}, timeout={}ms", orderId, data.get("timeoutMillis"));
    }
    
    /**
     * 发送订单超时延迟消息（使用默认30分钟超时）
     * 
     * @param orderId 订单ID
     */
    public void sendOrderTimeoutMessage(Long orderId) {
        sendOrderTimeoutMessage(orderId, null);
    }
}
