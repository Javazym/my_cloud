package org.example.shoppingserver.mq.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.model.entity.User;

import org.example.shoppingserver.model.vo.user.UserVO;
import org.example.shoppingserver.repository.UserRepository;
import org.example.shoppingserver.service.impl.UserServiceImpl;
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


    @RabbitListener(queues = RabbitConfig.AUTH_QUEUE)
    public void consume(MessageWrapper<?> message) throws IOException {
        System.out.println("Consumed: " + message);

        try {
            UserVO userVO = objectMapper.convertValue(message.getData(), UserVO.class);
            User user = new User();
            user.setId(userVO.getId());
            user.setNickname(userVO.getNickname());
            user.setUsername(userVO.getUsername());
            user.setEmail(userVO.getEmail());
            user.setStatus(userVO.getStatus());
            user.setUsername(userVO.getUsername());
            userRepository.save(user);
        } catch (Exception e) {
            log.error("处理消息时发生错误：{}", e.getMessage(), e);
        }
    }
}
