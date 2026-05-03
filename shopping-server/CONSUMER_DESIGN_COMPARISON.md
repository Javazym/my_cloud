# RabbitMQ 消费者设计对比

## 📊 两种设计方案对比

### 方案一：多个独立消费者（已删除）

```
OrderCreateConsumer.java    → 监听 order.create.queue
OrderCancelConsumer.java    → 监听 order.cancel.queue
OrderPayConsumer.java       → 监听 order.pay.queue
OrderCompleteConsumer.java  → 监听 order.complete.queue
```

### 方案二：统一消费者（当前采用）✅

```
OrderUnifiedConsumer.java
├── handleCreateOrder()      → 监听 order.create.queue
├── handleCancelOrder()      → 监听 order.cancel.queue
├── handlePayOrder()         → 监听 order.pay.queue
└── handleCompleteOrder()    → 监听 order.complete.queue
```

---

## 🔍 详细对比

| 维度 | 多个独立消费者 | 统一消费者 |
|------|--------------|-----------|
| **文件数量** | 4个文件 | 1个文件 |
| **代码重复** | 高（每个类都有相似的日志和异常处理） | 低（共用方法和依赖） |
| **维护成本** | 高（修改需改多个文件） | 低（只需修改一个文件） |
| **依赖注入** | 每个类都要注入相同的服务 | 一次注入，多处使用 |
| **扩展性** | 新增类型需创建新文件 | 新增类型只需添加方法 |
| **职责分离** | ✅ 非常清晰 | ✅ 通过方法名区分 |
| **故障隔离** | ✅ 一个失败不影响其他 | ⚠️ 需注意异常处理 |
| **适用场景** | 业务逻辑复杂、团队分工明确 | 业务逻辑相关、单人维护 |

---

## 💡 为什么选择统一消费者？

### 1. **减少代码重复**

❌ **多个消费者**：
```java
// OrderCreateConsumer.java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreateConsumer {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    
    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void handle(MessageWrapper<?> message) {
        log.info("收到消息: {}", message.getData());
        try {
            // 业务逻辑
        } catch (Exception e) {
            log.error("异常", e);
        }
    }
}

// OrderCancelConsumer.java - 重复的代码结构
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelConsumer {
    private final ObjectMapper objectMapper;  // 重复
    private final OrderService orderService;  // 重复
    private final InventoryService inventoryService;  // 重复
    
    @RabbitListener(queues = ORDER_CANCEL_QUEUE)
    public void handle(MessageWrapper<?> message) {
        log.info("收到消息: {}", message.getData());  // 重复
        try {
            // 业务逻辑
        } catch (Exception e) {
            log.error("异常", e);  // 重复
        }
    }
}
```

✅ **统一消费者**：
```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderUnifiedConsumer {
    // 依赖只注入一次
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    
    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void handleCreateOrder(MessageWrapper<?> message) {
        log.info("收到创建订单消息: {}", message.getData());
        try {
            processCreateOrder(message.getData());
        } catch (Exception e) {
            log.error("处理创建订单消息异常", e);
        }
    }
    
    @RabbitListener(queues = ORDER_CANCEL_QUEUE)
    public void handleCancelOrder(MessageWrapper<?> message) {
        log.info("收到取消订单消息: {}", message.getData());
        try {
            processCancelOrder(message.getData());
        } catch (Exception e) {
            log.error("处理取消订单消息异常", e);
        }
    }
    
    // 共享的业务方法
    private void processCreateOrder(Object data) { /* ... */ }
    private void processCancelOrder(Object data) { /* ... */ }
}
```

### 2. **便于共享服务**

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderUnifiedConsumer {
    
    // 所有业务方法共享这些服务
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void handleCreateOrder(MessageWrapper<?> message) {
        // 可以直接使用所有注入的服务
        OrderDTO order = (OrderDTO) message.getData();
        orderService.createOrder(order);
        inventoryService.deductStock(order.getItems());
        notificationService.sendNotification(order.getUserId());
    }
    
    @RabbitListener(queues = ORDER_CANCEL_QUEUE)
    public void handleCancelOrder(MessageWrapper<?> message) {
        // 同样可以使用所有服务
        Map<String, Object> data = (Map<String, Object>) message.getData();
        Long orderId = Long.valueOf(data.get("orderId").toString());
        orderService.cancelOrder(orderId);
        inventoryService.restoreStock(orderId);
        notificationService.sendCancelNotification(orderId);
    }
}
```

### 3. **统一的异常处理策略**

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderUnifiedConsumer {
    
    // 可以定义统一的异常处理方法
    private void handleMessage(String operation, MessageWrapper<?> message, Runnable businessLogic) {
        log.info("========== 收到{}消息 ==========", operation);
        log.info("消息ID: {}", message.getMessageId());
        log.info("数据内容: {}", message.getData());
        
        try {
            businessLogic.run();
            log.info("{}消息处理成功", operation);
        } catch (BusinessException e) {
            log.warn("{}业务异常: {}", operation, e.getMessage());
            // 业务异常不需要重试
        } catch (Exception e) {
            log.error("{}系统异常", operation, e);
            // 系统异常可以发送告警
            alertService.sendAlert(e);
        }
    }
    
    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void handleCreateOrder(MessageWrapper<?> message) {
        handleMessage("创建订单", message, () -> {
            processCreateOrder(message.getData());
        });
    }
    
    @RabbitListener(queues = ORDER_CANCEL_QUEUE)
    public void handleCancelOrder(MessageWrapper<?> message) {
        handleMessage("取消订单", message, () -> {
            processCancelOrder(message.getData());
        });
    }
}
```

---

## 🎯 何时选择哪种方案？

### 选择**多个独立消费者**的场景：

✅ 业务逻辑非常复杂，每个队列需要独立的异常处理策略  
✅ 团队成员分工明确，不同人负责不同的消息类型  
✅ 需要针对某个队列独立伸缩（增加消费者实例）  
✅ 不同消息类型的处理频率差异很大  

**示例**：
- 订单创建：每秒 1000 条消息 → 需要 10 个消费者实例
- 订单取消：每秒 10 条消息 → 只需要 1 个消费者实例

### 选择**统一消费者**的场景：

✅ 业务逻辑相关，都是订单相关的操作  
✅ 单人或小团队维护  
✅ 希望减少代码重复  
✅ 需要共享服务和工具方法  
✅ 消息量不大，不需要独立伸缩  

**你的项目**：订单的创建、取消、支付、完成都是相关业务，且由同一团队维护，**统一消费者更合适**！✅

---

## 📝 最佳实践建议

### 对于统一消费者

1. **方法命名清晰**：
   ```java
   handleCreateOrder()    // ✅ 清晰
   handle1()              // ❌ 不清晰
   ```

2. **异常处理要完善**：
   ```java
   try {
       processOrder(data);
   } catch (BusinessException e) {
       // 业务异常，确认消息
       channel.basicAck(tag, false);
   } catch (Exception e) {
       // 系统异常，拒绝消息
       channel.basicNack(tag, false, false);
   }
   ```

3. **业务方法私有化**：
   ```java
   @RabbitListener(...)
   public void handleCreateOrder(...) {  // 公开方法
       processCreateOrder(data);          // 调用私有方法
   }
   
   private void processCreateOrder(...) { // 私有业务方法
       // 实际业务逻辑
   }
   ```

4. **添加详细日志**：
   ```java
   log.info("收到{}消息: messageId={}, data={}", 
       operation, message.getMessageId(), message.getData());
   ```

---

## 🔄 如何切换？

如果以后发现需要独立伸缩某个队列的消费者，可以轻松拆分：

```java
// 从统一消费者中提取某个方法到独立类
@Component
@RequiredArgsConstructor
public class OrderCreateConsumer {
    
    private final OrderService orderService;
    
    @RabbitListener(queues = ORDER_CREATE_QUEUE)
    public void handleCreateOrder(MessageWrapper<?> message) {
        // 从 OrderUnifiedConsumer 中复制过来
        orderService.createOrder((OrderDTO) message.getData());
    }
}
```

**总结**：统一消费者更适合你的项目，简洁高效！🎉
