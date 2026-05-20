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
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
            log.info("========== 监听商户消息 ==========");
            log.info("消息ID: {}", message.getMessageProperties().getMessageId());
            log.info("数据来源: {}", message.getMessageProperties().getAppId());
            log.info("数据内容: {}", new String(message.getBody()));
            log.info("==================================");
            
            String body = new String(message.getBody());
            String merchantId = objectMapper.readValue(body, String.class);
            SysUser sysUser = authRepository.findById(merchantId).orElse(null);
            
            if (sysUser != null) {
                Role role = roleRepository.findById(2L).orElse(null);
                if (role != null) {
                    Set<Role> roles = sysUser.getRoles();
                    if (roles != null) {
                        roles.add(role);
                        sysUser.setRoles(roles);
                        authRepository.save(sysUser);
                        log.info("商户角色更新成功: merchantId={}", merchantId);
                    } else {
                        log.warn("用户角色集合为空: merchantId={}", merchantId);
                    }
                } else {
                    log.warn("未找到角色ID为2的角色");
                }
            } else {
                log.warn("未找到商户用户: merchantId={}", merchantId);
            }
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理商户消息时发生错误：{}", e.getMessage(), e);
            try {
                // 拒绝消息，不重新入队（避免无限重试）
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("消息确认失败", ioException);
            }
        }
    }

}
