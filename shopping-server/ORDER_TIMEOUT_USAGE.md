# 订单超时自动取消功能使用说明

## 📋 功能概述

本功能利用 RabbitMQ 的**延迟队列**和**死信队列**机制，实现订单超时自动取消。

### 工作流程

```
用户下单 
  ↓
创建订单（状态：待付款）
  ↓
发送延迟消息到 order.delay.exchange
  ↓
消息在延迟队列中等待 30 分钟（TTL）
  ↓
消息过期后转发到死信交换机 order.dead.letter.exchange
  ↓
进入死信队列 order.dead.letter.queue
  ↓
OrderTimeoutConsumer 消费消息
  ↓
检查订单状态，如果仍是"待付款"则自动取消
```

---

## 🏗️ 架构组件

### 1. 配置类：RabbitConfig.java

已配置的队列和交换机：

```java
// 延迟交换机和队列
ORDER_DELAY_EXCHANGE = "order.delay.exchange"
ORDER_DELAY_QUEUE = "order.delay.queue"
ORDER_DELAY_KEY = "order.delay"

// 死信交换机和队列
ORDER_DEAD_LETTER_EXCHANGE = "order.dead.letter.exchange"
ORDER_DEAD_LETTER_QUEUE = "order.dead.letter.queue"
ORDER_DEAD_LETTER_KEY = "order.dead.letter"

// TTL: 30分钟（1800000毫秒）
```

### 2. 生产者：OrderDelayProducer.java

**位置**: `org.example.shoppingserver.mq.producer.OrderDelayProducer`

**主要方法**:
```java
// 发送订单超时延迟消息（默认30分钟）
public void sendOrderTimeoutMessage(Long orderId)

// 发送订单超时延迟消息（自定义超时时间）
public void sendOrderTimeoutMessage(Long orderId, Long timeoutMillis)
```

**使用示例**:
```java
@Autowired
private OrderDelayProducer orderDelayProducer;

// 在创建订单后调用
orderDelayProducer.sendOrderTimeoutMessage(orderId);
```

### 3. 消费者：OrderTimeoutConsumer.java

**位置**: `org.example.shoppingserver.mq.consumer.OrderTimeoutConsumer`

**监听队列**: `order.dead.letter.queue`

**处理逻辑**:
1. 解析消息，获取订单ID
2. 查询订单
3. 检查订单状态是否为"待付款"
4. 如果是，则取消订单
5. 如果不是（已支付/已取消），则忽略

---

## 🔧 集成到订单服务

已在 `OrderServiceImpl.createOrder()` 中自动集成：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public OrderDTO createOrder(String userId, CreateOrderDTO dto) {
    // ... 创建订单逻辑
    
    Order savedOrder = orderRepository.save(order);
    
    // 发送订单超时延迟消息（30分钟后自动取消）
    orderDelayProducer.sendOrderTimeoutMessage(savedOrder.getId());
    
    return convertToDTO(savedOrder);
}
```

---

## 🧪 测试方法

### 方法一：修改 TTL 为短时间测试

在 `RabbitConfig.java` 中临时修改：

```java
@Bean
public Queue orderDelayQueue() {
    return QueueBuilder.durable(ORDER_DELAY_QUEUE)
            .withArgument("x-dead-letter-exchange", ORDER_DEAD_LETTER_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", ORDER_DEAD_LETTER_KEY)
            .withArgument("x-message-ttl", 60000) // 改为1分钟测试
            .build();
}
```

### 方法二：手动发送测试消息

```java
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private OrderDelayProducer orderDelayProducer;
    
    @GetMapping("/timeout/{orderId}")
    public String testTimeout(@PathVariable Long orderId) {
        orderDelayProducer.sendOrderTimeoutMessage(orderId);
        return "延迟消息已发送";
    }
}
```

### 方法三：查看日志

启动应用后，观察日志输出：

```
发送订单超时延迟消息: orderId=123, timeout=1800000ms
... (等待30分钟) ...
========== 收到订单超时消息 ==========
消息ID: xxx-xxx-xxx
数据来源: order-service
数据内容: {orderId=123, timeoutMillis=1800000}
=====================================
开始处理订单超时: orderId=123
订单已自动取消: orderId=123, orderNo=O1234567890
```

---

## ⚙️ 自定义超时时间

如果需要不同的超时时间（如秒杀订单5分钟超时）：

```java
// 5分钟超时
orderDelayProducer.sendOrderTimeoutMessage(orderId, 300000L);

// 1小时超时
orderDelayProducer.sendOrderTimeoutMessage(orderId, 3600000L);
```

---

## 🔍 注意事项

### 1. 幂等性保证
消费者会检查订单状态，只有"待付款"的订单才会被取消，避免重复取消。

### 2. 消息可靠性
- 队列设置为持久化（durable）
- 消费者使用手动确认（basicAck）
- 异常时拒绝消息但不重新入队（避免无限重试）

### 3. 库存回滚
当前实现只取消订单，如果需要释放库存，请在 `OrderTimeoutConsumer.processOrderTimeout()` 中添加：

```java
// TODO: 释放库存
for (OrderItem item : order.getItems()) {
    productSkuRepository.addStock(item.getSku().getId(), item.getQuantity());
}
```

### 4. 通知用户
可以添加短信/邮件通知：

```java
// TODO: 发送通知给用户
emailService.sendOrderCancelledNotification(order.getUser().getEmail(), order.getOrderNo());
```

---

## 🐛 常见问题

### Q1: 消息没有按时消费？
**A**: 检查 RabbitMQ 管理界面，确认：
- 延迟队列中有消息
- 消息的 TTL 设置正确
- 死信队列绑定正确

### Q2: 订单已支付但仍被取消？
**A**: 检查 `OrderTimeoutConsumer` 中的状态判断逻辑，确保只在 `PENDING_PAYMENT` 状态下取消。

### Q3: 如何取消延迟消息？
**A**: RabbitMQ 不支持直接取消已发送的消息。可以在消费者中增加一个"取消标记"：
```java
// 在订单支付时，设置一个 Redis 标记
redisTemplate.opsForValue().set("order:paid:" + orderId, "1", 30, TimeUnit.MINUTES);

// 在消费者中检查
if (redisTemplate.hasKey("order:paid:" + orderId)) {
    log.info("订单已支付，跳过取消");
    return;
}
```

---

## 📊 监控建议

1. **监控死信队列长度**：如果队列堆积，说明消费者处理慢或有异常
2. **记录取消订单数量**：统计超时取消率，优化业务流程
3. **告警机制**：当取消率异常升高时触发告警

---

## 🚀 扩展功能

### 1. 动态超时时间
根据商品类型、用户等级设置不同的超时时间。

### 2. 多次提醒
在超时前5分钟发送提醒消息，促使用户完成支付。

### 3. 数据分析
记录订单从创建到取消的时间分布，优化超时策略。
