package org.example.authserver.mq;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.authserver.model.entity.Role;
import org.example.authserver.model.entity.SysUser;
import org.example.authserver.repository.AuthRepository;
import org.example.authserver.repository.RoleRepository;
import org.example.authserver.util.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantConsumer {
    private final ObjectMapper objectMapper;
    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;

    @RabbitListener(queues = RabbitConfig.MERCHANT_QUEUE)
    public void handleMerchantMessage(Message message, Channel channel) {
        try {
            log.info("========== 监听商户消息 ==========");
            log.info("消息ID: {}", message.getMessageProperties().getMessageId());
            log.info("数据来源: {}", message.getMessageProperties().getAppId());
            log.info("数据内容: {}", new String(message.getBody()));
            log.info("==================================");
            String body = new String(message.getBody());
            String merchantId = objectMapper.readValue(body, String.class);
            SysUser sysUser = authRepository.findById(merchantId).orElse(null);
            Role role = roleRepository.findById(2L).orElse(null);
            Set<Role> roles = sysUser.getRoles();
            roles.add(role);
            sysUser.setRoles(roles);
            authRepository.save(sysUser);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
