# 优惠券清理功能使用说明

## 📋 功能概述

本功能用于定期清理已过期的用户优惠券，将状态从"未使用"更新为"已过期"。

---

## 🏗️ 实现方式

### 1. Service 层方法

**接口**: `CouponService.cleanExpiredCoupons()`

**实现**: `CouponServiceImpl.cleanExpiredCoupons()`

```java
@Override
@Transactional
@CacheEvict(value = {"userCoupons", "availableCoupons"}, allEntries = true)
public int cleanExpiredCoupons() {
    LocalDateTime now = LocalDateTime.now();
    int count = userCouponRepository.updateExpiredStatus(now);
    
    if (count > 0) {
        log.info("清理过期优惠券完成，共更新 {} 张优惠券状态为已过期", count);
    }
    
    return count;
}
```

**功能**：
- 查询所有 `status = 0`（未使用）且 `expireTime < 当前时间` 的优惠券
- 批量更新状态为 `status = 2`（已过期）
- 清除相关缓存
- 返回更新的优惠券数量

---

### 2. 定时任务

**位置**: `org.example.shoppingserver.task.CouponTask`

**执行时间**: 每天凌晨 2:00

```java
@Scheduled(cron = "0 0 2 * * ?")
public void cleanExpiredCoupons() {
    log.info("========== 开始执行优惠券清理任务 ==========");
    
    try {
        int count = couponService.cleanExpiredCoupons();
        log.info("优惠券清理任务执行完成，共清理 {} 张过期优惠券", count);
    } catch (Exception e) {
        log.error("优惠券清理任务执行失败", e);
    }
    
    log.info("========== 优惠券清理任务结束 ==========");
}
```

---

### 3. Repository 层 SQL

**位置**: `UserCouponRepository.updateExpiredStatus()`

```java
@Modifying
@Query("UPDATE UserCoupon u SET u.status = 2 WHERE u.status = 0 AND u.expireTime < :now")
int updateExpiredStatus(@Param("now") LocalDateTime now);
```

**说明**：
- 使用 JPQL 批量更新，性能优于逐条更新
- 返回值是受影响的行数

---

## 🔧 手动调用

如果需要立即清理过期优惠券，可以在 Controller 中添加测试接口：

```java
@RestController
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class CouponAdminController {
    
    private final CouponService couponService;
    
    /**
     * 手动清理过期优惠券
     */
    @PostMapping("/clean-expired")
    public ResponseResult<Integer> cleanExpiredCoupons() {
        int count = couponService.cleanExpiredCoupons();
        return ResponseResult.success(count);
    }
}
```

---

## ⚙️ 自定义执行时间

修改 `CouponTask.java` 中的 cron 表达式：

```java
// 每小时执行一次
@Scheduled(cron = "0 0 * * * ?")

// 每30分钟执行一次
@Scheduled(cron = "0 */30 * * * ?")

// 每天凌晨3点执行
@Scheduled(cron = "0 0 3 * * ?")

// 每周一凌晨1点执行
@Scheduled(cron = "0 0 1 ? * MON")
```

**Cron 表达式格式**：
```
秒 分 时 日 月 周
```

**常用示例**：
- `0 0 2 * * ?` - 每天凌晨2点
- `0 */5 * * * ?` - 每5分钟
- `0 0 */2 * * ?` - 每2小时
- `0 0 0 1 * ?` - 每月1号凌晨

---

## 📊 监控建议

### 1. 日志监控

查看定时任务执行日志：

```
========== 开始执行优惠券清理任务 ==========
清理过期优惠券完成，共更新 123 张优惠券状态为已过期
========== 优惠券清理任务结束 ==========
```

### 2. 数据库监控

定期检查过期优惠券数量：

```sql
-- 查询即将过期的优惠券（7天内）
SELECT COUNT(*) FROM user_coupons 
WHERE status = 0 
  AND expire_time > NOW() 
  AND expire_time < DATE_ADD(NOW(), INTERVAL 7 DAY);

-- 查询已过期但未清理的优惠券
SELECT COUNT(*) FROM user_coupons 
WHERE status = 0 
  AND expire_time < NOW();
```

### 3. 告警机制

如果清理数量异常（如突然清理了上万张），可能说明：
- 系统时间错误
- 定时任务之前一直未执行
- 业务逻辑有问题

---

## 🔍 注意事项

### 1. 事务管理
方法上有 `@Transactional` 注解，确保批量更新的原子性。

### 2. 缓存清理
使用 `@CacheEvict` 清除相关缓存，保证数据一致性：
- `userCoupons` - 用户优惠券列表缓存
- `availableCoupons` - 可用优惠券缓存

### 3. 性能优化
- 使用批量更新而非逐条更新
- 建议在低峰期执行（如凌晨2点）
- 如果数据量特别大（百万级），可以分批处理

### 4. 幂等性
定时任务可以重复执行，不会影响最终结果：
- 已更新的优惠券不会再次更新
- 多次执行结果一致

---

## 🧪 测试方法

### 方法一：修改测试数据

在数据库中手动修改一些优惠券的过期时间：

```sql
-- 将某些优惠券设置为已过期
UPDATE user_coupons 
SET expire_time = DATE_SUB(NOW(), INTERVAL 1 DAY), 
    status = 0 
WHERE id IN (1, 2, 3);
```

然后等待定时任务执行，或手动调用清理方法。

### 方法二：临时修改 cron 表达式

改为每分钟执行一次进行测试：

```java
@Scheduled(cron = "0 */1 * * * ?")
```

测试完成后记得改回来。

### 方法三：单元测试

```java
@Test
public void testCleanExpiredCoupons() {
    // 准备测试数据
    // ...
    
    int count = couponService.cleanExpiredCoupons();
    
    // 验证结果
    Assertions.assertTrue(count >= 0);
}
```

---

## 🚀 扩展功能

### 1. 发送过期提醒

在清理前，先发送通知给用户：

```java
List<UserCoupon> expiredCoupons = userCouponRepository.findExpiredCoupons(LocalDateTime.now());

for (UserCoupon uc : expiredCoupons) {
    // 发送短信/邮件通知
    notificationService.sendExpiryNotification(uc.getUser(), uc);
}

// 然后更新状态
userCouponRepository.updateExpiredStatus(LocalDateTime.now());
```

### 2. 分批处理

如果数据量很大，可以分批处理：

```java
public int cleanExpiredCoupons() {
    int totalCount = 0;
    int batchSize = 1000;
    
    while (true) {
        int count = userCouponRepository.updateExpiredStatusBatch(LocalDateTime.now(), batchSize);
        totalCount += count;
        
        if (count == 0) break;
        
        // 避免长时间持有锁
        Thread.sleep(100);
    }
    
    return totalCount;
}
```

### 3. 统计报表

记录每次清理的数据，生成报表：

```java
// 保存到统计表
CouponCleanupLog log = new CouponCleanupLog();
log.setCleanupTime(LocalDateTime.now());
log.setCleanedCount(count);
cleanupLogRepository.save(log);
```

---

## 📝 总结

✅ **已实现功能**：
1. Service 层清理方法 `cleanExpiredCoupons()`
2. 定时任务每天凌晨2点自动执行
3. 批量更新，性能优化
4. 缓存清理，保证一致性
5. 详细日志记录

✅ **启动类已添加** `@EnableScheduling` 注解

✅ **可直接使用**，无需额外配置
