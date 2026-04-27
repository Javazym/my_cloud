package org.example.authserver.mq;


import com.rabbitmq.client.Channel;
import org.example.authserver.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthConsumer {
    @RabbitListener(queues = RabbitConfig.AUTH_QUEUE)
    public void consume(Message message, Channel channel) throws IOException {
        System.out.println("Consumed: " + message);
        try {
            System.out.println("收到Direct：" + message);
            // 业务处理
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 成功确认
        } catch (Exception e) {
            // 失败：重回队列或丢弃
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
