package org.example.shoppingserver.mq.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.model.entity.user.User;

import org.example.shoppingserver.model.vo.user.UserVO;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.impl.UserServiceImpl;
import org.example.shoppingserver.util.IdempotentUtil;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthConsumer {
    private static final Logger log = LoggerFactory.getLogger(AuthConsumer.class);
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final IdempotentUtil idempotentUtil;


    @RabbitListener(queues = RabbitConfig.AUTH_QUEUE)
    public void consume(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            String body = new String(message.getBody());
            MessageWrapper<?> wrapper = objectMapper.readValue(body, MessageWrapper.class);
            
            String messageId = wrapper.getMessageId();
            log.info("========== 收到用户创建消息 ==========" );
            log.info("消息ID: {}", messageId);
            log.info("数据来源: {}", wrapper.getSourceService());
            
            // 幂等性校验：防止重复消费
            if (!idempotentUtil.tryProcess(messageId)) {
                log.warn("消息已处理，跳过: messageId={}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }
            
            System.out.println("Consumed: " + wrapper);

            UserVO userVO = objectMapper.convertValue(wrapper.getData(), UserVO.class);
            User user = new User();
            user.setId(userVO.getId());
            user.setNickname(userVO.getNickname());
            user.setUsername(userVO.getUsername());
            user.setEmail(userVO.getEmail());
            user.setStatus(userVO.getStatus());
            userRepository.save(user);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("用户创建消息处理成功: userId={}, messageId={}", userVO.getId(), messageId);
        } catch (Exception e) {
            log.error("处理用户创建消息时发生错误：{}", e.getMessage(), e);
            try {
                // 拒绝消息，不重新入队（避免无限重试）
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }
}
