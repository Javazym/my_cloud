package org.example.shoppingserver.mq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 发送创建订单消息
     *
     * @param msg 消息包装器
     */
    public void sendCreateOrderMessage(MessageWrapper<?> msg) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.ORDER_EXCHANGE, 
            RabbitConfig.ORDER_CREATE_KEY, 
            msg
        );
        log.info("发送创建订单消息: messageId={}", msg.getMessageId());
    }
    
    /**
     * 发送取消订单消息
     *
     * @param msg 消息包装器
     */
    public void sendCancelOrderMessage(MessageWrapper<?> msg) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.ORDER_EXCHANGE, 
            RabbitConfig.ORDER_CANCEL_KEY, 
            msg
        );
        log.info("发送取消订单消息: messageId={}", msg.getMessageId());
    }
    
    /**
     * 发送支付订单消息
     *
     * @param msg 消息包装器
     */
    public void sendPayOrderMessage(MessageWrapper<?> msg) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.ORDER_EXCHANGE, 
            RabbitConfig.ORDER_PAY_KEY, 
            msg
        );
        log.info("发送支付订单消息: messageId={}", msg.getMessageId());
    }
    
    /**
     * 发送完成订单消息
     *
     * @param msg 消息包装器
     */
    public void sendCompleteOrderMessage(MessageWrapper<?> msg) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.ORDER_EXCHANGE, 
            RabbitConfig.ORDER_COMPLETE_KEY, 
            msg
        );
        log.info("发送完成订单消息: messageId={}", msg.getMessageId());
    }
}
