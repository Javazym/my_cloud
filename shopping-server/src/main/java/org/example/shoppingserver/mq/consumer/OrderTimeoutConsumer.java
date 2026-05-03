package org.example.shoppingserver.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.model.entity.Order;
import org.example.shoppingserver.model.entity.OrderStatus;
import org.example.shoppingserver.repository.OrderRepository;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 订单超时消费者
 * 监听死信队列，处理订单超时自动取消逻辑
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutConsumer {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    /**
     * 处理订单超时消息（从死信队列消费）
     * 
     * @param message RabbitMQ消息
     * @param channel 通道
     */
    @RabbitListener(queues = RabbitConfig.ORDER_DEAD_LETTER_QUEUE)
    public void handleOrderTimeout(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            // 1. 解析消息
            String body = new String(message.getBody());
            MessageWrapper<?> wrapper = objectMapper.readValue(body, MessageWrapper.class);
            
            log.info("========== 收到订单超时消息 ==========");
            log.info("消息ID: {}", wrapper.getMessageId());
            log.info("数据来源: {}", wrapper.getSourceService());
            log.info("数据内容: {}", wrapper.getData());
            log.info("=====================================");
            
            // 2. 提取订单ID
            Map<String, Object> data = (Map<String, Object>) wrapper.getData();
            Long orderId = Long.valueOf(data.get("orderId").toString());
            
            // 3. 处理订单超时逻辑
            processOrderTimeout(orderId);
            
            // 4. 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("订单超时消息处理成功: orderId={}", orderId);
            
        } catch (Exception e) {
            log.error("处理订单超时消息异常", e);
            try {
                // 拒绝消息，不重新入队（避免无限重试）
                channel.basicNack(deliveryTag, false, false);
                log.error("订单超时消息处理失败，已拒绝");
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

    /**
     * 处理订单超时业务逻辑
     * 
     * @param orderId 订单ID
     */
    private void processOrderTimeout(Long orderId) {
        log.info("开始处理订单超时: orderId={}", orderId);
        
        // 1. 查询订单
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.warn("订单不存在，无需处理: orderId={}", orderId);
            return;
        }
        
        // 2. 检查订单状态（只有待付款的订单才能取消）
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            log.info("订单状态不是待付款，无需取消: orderId={}, status={}", 
                    orderId, order.getStatus());
            return;
        }
        
        // 3. 取消订单
        order.cancel();
        orderRepository.save(order);
        
        log.info("订单已自动取消: orderId={}, orderNo={}", orderId, order.getOrderNo());
        
        // 4. TODO: 可以在这里添加后续逻辑
        // - 释放库存（如果之前预扣减了）
        // - 发送通知给用户
        // - 记录日志
    }
}
