# RabbitMQ 订单消息一对多配置说明

## 📋 架构概览

```
                    ┌─────────────────────┐
                    │  order_exchange     │
                    │  (订单统一交换机)     │
                    └──────────┬──────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
   order.create        order.cancel          order.pay
          │                    │                    │
          ▼                    ▼                    ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│order.create     │ │order.cancel     │ │order.pay        │
│.queue           │ │.queue           │ │.queue           │
└────────┬────────┘ └────────┬────────┘ └────────┬────────┘
         │                   │                   │
         ▼                   ▼                   ▼
  OrderCreateConsumer OrderCancelConsumer OrderPayConsumer
```

## 🎯 配置说明

### 1. 交换机与队列映射

| 路由键 (Routing Key) | 队列名称 (Queue) | 消费者类 | 用途 |
|---------------------|-----------------|---------|------|
| `order.create` | `order.create.queue` | `OrderCreateConsumer` | 创建订单 |
| `order.cancel` | `order.cancel.queue` | `OrderCancelConsumer` | 取消订单 |
| `order.pay` | `order.pay.queue` | `OrderPayConsumer` | 支付订单 |
| `order.complete` | `order.complete.queue` | `OrderCompleteConsumer` | 完成订单 |

### 2. 消费者设计（统一消费者）

采用**一个统一消费者类**处理所有订单消息，通过不同的监听方法区分消息类型：

| 路由键 (Routing Key) | 队列名称 (Queue) | 监听方法 | 用途 |
|---------------------|-----------------|---------|------|
| `order.create` | `order.create.queue` | `handleCreateOrder()` | 创建订单 |
| `order.cancel` | `order.cancel.queue` | `handleCancelOrder()` | 取消订单 |
| `order.pay` | `order.pay.queue` | `handlePayOrder()` | 支付订单 |
| `order.complete` | `order.complete.queue` | `handleCompleteOrder()` | 完成订单 |

**消费者类**: `OrderUnifiedConsumer`

所有常量都在 `RabbitConfig` 中定义：

```java
// 交换机
public static final String ORDER_EXCHANGE = "order_exchange";

// 创建订单
public static final String ORDER_CREATE_QUEUE = "order.create.queue";
public static final String ORDER_CREATE_KEY = "order.create";

// 取消订单
public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
public static final String ORDER_CANCEL_KEY = "order.cancel";

// 支付订单
public static final String ORDER_PAY_QUEUE = "order.pay.queue";
public static final String ORDER_PAY_KEY = "order.pay";

// 完成订单
public static final String ORDER_COMPLETE_QUEUE = "order.complete.queue";
public static final String ORDER_COMPLETE_KEY = "order.complete";
```

## 💻 使用示例

### 发送消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderProducer orderProducer;
    
    /**
     * 创建订单时发送消息
     */
    public void createOrder(OrderDTO orderDTO) {
        // 业务逻辑...
        
        // 构建消息
        MessageWrapper<OrderDTO> message = MessageWrapper.<OrderDTO>builder()
                .data(orderDTO)
                .sourceService("order-service")
                .targetService("order-consumer")
                .build();
        
        // 发送到创建订单队列
        orderProducer.sendCreateOrderMessage(message);
    }
}
```

### 接收消息（统一消费者）

所有订单消息都由 `OrderUnifiedConsumer` 统一处理：

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderUnifiedConsumer {

    /**
     * 处理创建订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_CREATE_QUEUE)
    public void handleCreateOrder(MessageWrapper<?> message) {
        log.info("收到创建订单消息: {}", message.getData());
        
        // TODO: 处理业务逻辑
        OrderDTO orderDTO = (OrderDTO) message.getData();
        // 1. 验证库存
        // 2. 创建订单项
        // 3. 发送通知
    }

    /**
     * 处理取消订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_CANCEL_QUEUE)
    public void handleCancelOrder(MessageWrapper<?> message) {
        log.info("收到取消订单消息: {}", message.getData());
        
        // TODO: 处理取消订单逻辑
        Map<String, Object> data = (Map<String, Object>) message.getData();
        Long orderId = Long.valueOf(data.get("orderId").toString());
        // 1. 释放库存
        // 2. 退款
        // 3. 发送通知
    }

    /**
     * 处理支付订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_PAY_QUEUE)
    public void handlePayOrder(MessageWrapper<?> message) {
        log.info("收到支付订单消息: {}", message.getData());
        
        // TODO: 处理支付逻辑
        // 1. 更新订单状态
        // 2. 扣减库存
        // 3. 发放积分
    }

    /**
     * 处理完成订单消息
     */
    @RabbitListener(queues = RabbitConfig.ORDER_COMPLETE_QUEUE)
    public void handleCompleteOrder(MessageWrapper<?> message) {
        log.info("收到完成订单消息: {}", message.getData());
        
        // TODO: 处理完成逻辑
        // 1. 更新订单状态
        // 2. 发放优惠券
        // 3. 邀请评价
    }
}
```

## 🔧 扩展指南

### 添加新的订单消息类型

假设要添加"退款订单"消息类型：

#### 1. 在 RabbitConfig 中添加常量

```java
// 退款订单
public static final String ORDER_REFUND_QUEUE = "order.refund.queue";
public static final String ORDER_REFUND_KEY = "order.refund";
```

#### 2. 添加队列和绑定

```java
@Bean
public Queue orderRefundQueue() {
    return QueueBuilder.durable(ORDER_REFUND_QUEUE).build();
}

@Bean
public Binding orderRefundBinding() {
    return BindingBuilder.bind(orderRefundQueue())
            .to(orderExchange())
            .with(ORDER_REFUND_KEY);
}
```

#### 3. 在 OrderProducer 中添加方法

```java
public void sendRefundOrderMessage(MessageWrapper<?> msg) {
    rabbitTemplate.convertAndSend(
        RabbitConfig.ORDER_EXCHANGE, 
        RabbitConfig.ORDER_REFUND_KEY, 
        msg
    );
    log.info("发送退款订单消息: messageId={}", msg.getMessageId());
}
```

#### 4. 在 OrderUnifiedConsumer 中添加监听方法

```java
@RabbitListener(queues = RabbitConfig.ORDER_REFUND_QUEUE)
public void handleRefundOrder(MessageWrapper<?> message) {
    log.info("收到退款订单消息: {}", message.getData());
    
    // TODO: 处理退款逻辑
    processRefundOrder(message.getData());
}

private void processRefundOrder(Object data) {
    log.info("执行退款订单业务逻辑: {}", data);
    // 实际业务逻辑...
}
```

## ✅ 优势总结

### 统一消费者的优点

1. **集中管理**：所有订单消息处理逻辑在一个类中，便于维护
2. **代码复用**：可以共享依赖注入的服务和工具方法
3. **统一异常处理**：可以在一个地方定义通用的异常处理策略
4. **易于扩展**：新增消息类型只需添加一个监听方法
5. **职责清晰**：通过方法名和路由键明确区分不同业务

### 与延迟队列配合

延迟队列仍然独立存在，用于订单超时自动取消：

```
order.delay.exchange → order.delay.queue (TTL: 30分钟)
                                    ↓ (过期后)
                    order.dead.letter.exchange → order.dead.letter.queue
                                                          ↓
                                                OrderDelayConsumer
                                                (检查状态后取消订单)
```

## 📝 注意事项

1. **重启应用**：修改配置后需要重启 Spring Boot 应用，RabbitMQ 会自动创建新的交换机和队列
2. **查看管理界面**：访问 `http://localhost:15672` 查看交换机和队列是否正确创建
3. **消息持久化**：所有队列都设置为 `durable`，重启 RabbitMQ 后消息不会丢失
4. **手动确认**：消费者需要手动调用 `channel.basicAck()` 确认消息

## 🚀 快速测试

启动应用后，可以在日志中看到类似输出：

```
Sending message to exchange: order_exchange, routing key: order.create
发送创建订单消息: messageId=abc-123-def
========== 收到创建订单消息 ==========
消息ID: abc-123-def
数据来源: order-service
数据内容: {...}
=====================================
```

---

**配置完成！** 现在你的订单系统已经支持一对多的消息路由了！🎉
