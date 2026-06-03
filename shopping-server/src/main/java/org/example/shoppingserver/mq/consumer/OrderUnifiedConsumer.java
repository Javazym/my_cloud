package org.example.shoppingserver.mq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.model.dto.order.CreateOrderDTO;
import org.example.shoppingserver.mq.producer.OrderDelayProducer;
import org.example.shoppingserver.service.impl.OrderServiceImpl;
import org.example.shoppingserver.util.IdempotentUtil;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 统一订单消费者
 * 监听 order_exchange 交换机的所有订单相关队列
 * 
 * 通过路由键区分不同的订单操作类型：
 * - order.create: 创建订单
 * - order.cancel: 取消订单
 * - order.pay: 支付订单
 * - order.complete: 完成订单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderUnifiedConsumer {

    private final ObjectMapper objectMapper;
    private final OrderServiceImpl orderServiceImpl;
    private final IdempotentUtil idempotentUtil;

    /**
     * 处理创建订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_CREATE_QUEUE)
    public void handleCreateOrder(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            String body = new String(message.getBody());
            MessageWrapper<CreateOrderDTO> wrapper = objectMapper.readValue(body, 
                objectMapper.getTypeFactory().constructParametricType(MessageWrapper.class, CreateOrderDTO.class));
            
            String messageId = wrapper.getMessageId();
            log.info("========== 收到创建订单消息 ==========" );
            log.info("消息ID: {}", messageId);
            log.info("数据来源: {}", wrapper.getSourceService());
            
            // 幂等性校验：防止重复消费
            if (!idempotentUtil.tryProcess(messageId)) {
                log.warn("消息已处理，跳过: messageId={}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }
            
            // 处理业务逻辑
            CreateOrderDTO createOrderDTO = wrapper.getData();
            log.info("数据内容: {}", createOrderDTO);
            
            // 从 DTO 中获取 userId（MQ 环境中 UserHolder 不可用）
            String userId = createOrderDTO.getUserId();
            if (userId == null) {
                // 兼容普通订单：从 UserHolder 获取
                userId = UserHolder.getCurrentUserId();
            }
            
            orderServiceImpl.createOrder(userId, createOrderDTO);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("创建订单消息处理成功: messageId={}", messageId);
            
        } catch (Exception e) {
            log.error("处理创建订单消息异常", e);
            try {
                // 拒绝消息，不重新入队（避免无限重试）
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

    /**
     * 处理取消订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_CANCEL_QUEUE)
    public void handleCancelOrder(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
            
        try {
            String body = new String(message.getBody());
            MessageWrapper<?> wrapper = objectMapper.readValue(body, MessageWrapper.class);
                
            String messageId = wrapper.getMessageId();
            log.info("========== 收到取消订单消息 ==========" );
            log.info("消息ID: {}", messageId);
            log.info("路由键: {}", RabbitConfig.ORDER_CANCEL_KEY);
            log.info("数据来源: {}", wrapper.getSourceService());
            log.info("数据内容: {}", wrapper.getData());
            log.info("=====================================");
                
            // 幂等性校验：防止重复消费
            if (!idempotentUtil.tryProcess(messageId)) {
                log.warn("消息已处理，跳过: messageId={}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }
                
            // 处理业务逻辑
            processCancelOrder(wrapper.getData());
                
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("取消订单消息处理成功: messageId={}", messageId);
                
        } catch (Exception e) {
            log.error("处理取消订单消息异常", e);
            try {
                // 拒绝消息，不重新入队
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

    /**
     * 处理支付订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_PAY_QUEUE)
    public void handlePayOrder(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
            
        try {
            String body = new String(message.getBody());
            MessageWrapper<?> wrapper = objectMapper.readValue(body, MessageWrapper.class);
                
            String messageId = wrapper.getMessageId();
            log.info("========== 收到支付订单消息 ==========" );
            log.info("消息ID: {}", messageId);
            log.info("路由键: {}", RabbitConfig.ORDER_PAY_KEY);
            log.info("数据来源: {}", wrapper.getSourceService());
            log.info("数据内容: {}", wrapper.getData());
            log.info("=====================================");
                
            // 幂等性校验：防止重复消费
            if (!idempotentUtil.tryProcess(messageId)) {
                log.warn("消息已处理，跳过: messageId={}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }
                
            // 处理业务逻辑
            processPayOrder(wrapper.getData());
                
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("支付订单消息处理成功: messageId={}", messageId);
                
        } catch (Exception e) {
            log.error("处理支付订单消息异常", e);
            try {
                // 拒绝消息，不重新入队
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

    /**
     * 处理完成订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_COMPLETE_QUEUE)
    public void handleCompleteOrder(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
            
        try {
            String body = new String(message.getBody());
            MessageWrapper<?> wrapper = objectMapper.readValue(body, MessageWrapper.class);
                
            String messageId = wrapper.getMessageId();
            log.info("========== 收到完成订单消息 ==========" );
            log.info("消息ID: {}", messageId);
            log.info("路由键: {}", RabbitConfig.ORDER_COMPLETE_KEY);
            log.info("数据来源: {}", wrapper.getSourceService());
            log.info("数据内容: {}", wrapper.getData());
            log.info("=====================================");
                
            // 幂等性校验：防止重复消费
            if (!idempotentUtil.tryProcess(messageId)) {
                log.warn("消息已处理，跳过: messageId={}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }
                
            // 处理业务逻辑
            processCompleteOrder(wrapper.getData());
                
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("完成订单消息处理成功: messageId={}", messageId);
                
        } catch (Exception e) {
            log.error("处理完成订单消息异常", e);
            try {
                // 拒绝消息，不重新入队
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

    // ==================== 业务处理方法 ====================

    /**
     * 处理取消订单
     */
    private void processCancelOrder(Object data) {
        log.info("执行取消订单业务逻辑: {}", data);
        // 实际业务逻辑...
    }

    /**
     * 处理支付订单
     */
    private void processPayOrder(Object data) {
        log.info("执行支付订单业务逻辑: {}", data);
        // 实际业务逻辑...
    }

    /**
     * 处理完成订单
     */
    private void processCompleteOrder(Object data) {
        log.info("执行完成订单业务逻辑: {}", data);
        // 实际业务逻辑...
    }
}
