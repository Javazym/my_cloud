package org.example.shoppingserver.mq.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.shoppingserver.common.MessageWrapper;
import org.example.shoppingserver.common.UserHolder;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送商户注册消息
     *
     * @param merchantId 商户ID
     */
    public void sendMerchantRegisterMessage(String merchantId) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.MERCHANT_EXCHANGE,
            RabbitConfig.MERCHANT_KEY,
            MessageWrapper.<String>builder()
                .sourceService("shopping-server")
                .targetService("auth-server")
                .data(merchantId)
                .build()
        );
    }
}
