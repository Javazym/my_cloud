package org.example.shoppingserver.mq.consumer;


import com.rabbitmq.client.Channel;
import org.example.shoppingserver.util.config.RabbitConfig;
import org.springframework.amqp.core.Message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class OrderConsumer {

    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void consume(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println("收到Direct：" + msg);
            // 业务处理
            channel.basicAck(tag, false); // 成功确认
        } catch (Exception e) {
            // 失败：重回队列或丢弃
            channel.basicNack(tag, false, true);
        }
    }
}
