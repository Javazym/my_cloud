# 商家营销中心 API 文档

## 概述

商家营销中心提供了完整的营销活动管理功能，包括：
- 🔥 **秒杀活动管理** - 创建和管理限时秒杀活动
- 💰 **满减活动管理** - 设置满件减、满额减等优惠活动
- 🎫 **优惠券管理** - 商家端优惠券的创建和统计

## 基础路径

```
/merchant/marketing
```

---

## 1. 秒杀活动管理

### 1.1 创建秒杀活动

**接口**: `POST /merchant/marketing/seckill`

**查询参数**:
- `merchantId`: 商家ID（必填）

**请求体**:
```json
{
  "name": "iPhone 15 限时秒杀",
  "productId": 1,
  "skuId": 1,
  "seckillPrice": 6999.00,
  "originalPrice": 7999.00,
  "stock": 100,
  "limitPerUser": 2,
  "startTime": "2026-04-28T10:00:00",
  "endTime": "2026-04-28T22:00:00",
  "sort": 1
}
```

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": 1
}
```

### 1.2 更新秒杀活动

**接口**: `PUT /merchant/marketing/seckill/{activityId}`

**请求体**: 同创建接口

### 1.3 删除秒杀活动

**接口**: `DELETE /merchant/marketing/seckill/{activityId}`

### 1.4 获取秒杀活动列表

**接口**: `GET /merchant/marketing/seckill`

**查询参数**:
- `merchantId`: 商家ID（必填）
- `status`: 活动状态（可选）- 0:未开始, 1:进行中, 2:已结束, 3:已取消
- `pageNum`: 页码，默认 1
- `pageSize`: 每页数量，默认 10

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [...],
    "totalElements": 10,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

### 1.5 获取秒杀活动详情

**接口**: `GET /merchant/marketing/seckill/{activityId}`

**查询参数**:
- `merchantId`: 商家ID（必填）

**路径参数**:
- `activityId`: 活动ID

### 1.6 更新秒杀活动状态

**接口**: `PUT /merchant/marketing/seckill/{activityId}/status`

**查询参数**:
- `merchantId`: 商家ID（必填）
- `activityId`: 活动ID（路径参数）
- `status`: 目标状态

---

## 2. 满减活动管理

### 2.1 创建满减活动

**接口**: `POST /merchant/marketing/discount`

**查询参数**:
- `merchantId`: 商家ID（必填）

**请求体**:
```json
{
  "name": "满200减30",
  "description": "全场商品满200元减30元",
  "discountType": 2,
  "conditionValue": 200.00,
  "discountAmount": 30.00,
  "maxDiscount": 30.00,
  "startTime": "2026-04-28T00:00:00",
  "endTime": "2026-05-28T23:59:59",
  "scopeType": "all",
  "scopeIds": null,
  "limitPerUser": null,
  "sort": 1
}
```

**字段说明**:
- `discountType`: 满减类型
  - `1`: 满件减（按商品数量）
  - `2`: 满额减（按订单金额）
- `conditionValue`: 满足条件（件数或金额）
- `discountAmount`: 优惠金额
- `maxDiscount`: 最大优惠金额（封顶）
- `scopeType`: 适用范围
  - `"all"`: 全场商品
  - `"category"`: 指定分类
  - `"product"`: 指定商品
- `scopeIds`: 适用范围ID列表（JSON数组字符串），当 scopeType 不是 "all" 时使用

### 2.2 更新满减活动

**接口**: `PUT /merchant/marketing/discount/{activityId}`

### 2.3 删除满减活动

**接口**: `DELETE /merchant/marketing/discount/{activityId}`

### 2.4 获取满减活动列表

**接口**: `GET /merchant/marketing/discount`

**查询参数**:
- `merchantId`: 商家ID（必填）
- `status`: 活动状态（可选）
- `pageNum`: 页码
- `pageSize`: 每页数量

### 2.5 获取满减活动详情

**接口**: `GET /merchant/marketing/discount/{activityId}`

**查询参数**:
- `merchantId`: 商家ID（必填）

### 2.6 更新满减活动状态

**接口**: `PUT /merchant/marketing/discount/{activityId}/status?status=1`

---

## 3. 优惠券管理（商家端）

### 3.1 创建优惠券

**接口**: `POST /merchant/marketing/coupon`

**查询参数**:
- `merchantId`: 商家ID（必填）

**请求体**:
```json
{
  "name": "新用户专享券",
  "type": 1,
  "value": 20.00,
  "minAmount": 100.00,
  "maxDiscount": 20.00,
  "totalStock": 1000,
  "limitPerUser": 1,
  "validDays": 30,
  "startTime": "2026-04-28T00:00:00",
  "endTime": "2026-05-28T23:59:59"
}
```

**字段说明**:
- `type`: 优惠券类型
  - `1`: 满减券
  - `2`: 折扣券
- `value`: 优惠值（金额或折扣率）
- `minAmount`: 最低消费金额
- `maxDiscount`: 最大优惠金额
- `totalStock`: 总库存
- `limitPerUser`: 每人限领数量
- `validDays`: 有效天数

### 3.2 更新优惠券

**接口**: `PUT /merchant/marketing/coupon/{couponId}`

**请求体**:
```json
{
  "name": "更新后的优惠券名称",
  "value": 25.00,
  "minAmount": 100.00,
  "maxDiscount": 25.00,
  "totalStock": 1500,
  "limitPerUser": 2,
  "endTime": "2026-06-28T23:59:59",
  "status": 1
}
```

### 3.3 删除优惠券

**接口**: `DELETE /merchant/marketing/coupon/{couponId}`

### 3.4 获取商家优惠券列表

**接口**: `GET /merchant/marketing/coupon`

**查询参数**:
- `merchantId`: 商家ID（必填）
- `status`: 优惠券状态（可选）
- `pageNum`: 页码
- `pageSize`: 每页数量

### 3.5 获取优惠券统计

**接口**: `GET /merchant/marketing/coupon/statistics`

**查询参数**:
- `merchantId`: 商家ID（必填）

**响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCoupons": 10,
    "activeCoupons": 5,
    "totalReceived": 500,
    "totalUsed": 200
  }
}
```

**字段说明**:
- `totalCoupons`: 总优惠券数量
- `activeCoupons`: 进行中的优惠券数量
- `totalReceived`: 总领取次数
- `totalUsed`: 总使用次数

---

## 状态码说明

### 活动状态
- `0`: 未开始
- `1`: 进行中
- `2`: 已结束
- `3`: 已取消

### 优惠券状态
- `1`: 可用
- `2`: 已停用

---

## 数据库表结构

### seckill_activities (秒杀活动表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商家ID |
| product_id | BIGINT | 商品ID |
| sku_id | BIGINT | SKU ID |
| name | VARCHAR(100) | 活动名称 |
| seckill_price | DECIMAL(10,2) | 秒杀价格 |
| original_price | DECIMAL(10,2) | 原价 |
| stock | INT | 库存数量 |
| sold_count | INT | 已抢购数量 |
| limit_per_user | INT | 每人限购数量 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| status | TINYINT | 状态 |
| sort | INT | 排序 |

### discount_activities (满减活动表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商家ID |
| name | VARCHAR(100) | 活动名称 |
| description | TEXT | 活动描述 |
| discount_type | TINYINT | 满减类型：1-满件减，2-满额减 |
| condition_value | DECIMAL(10,2) | 满足条件 |
| discount_amount | DECIMAL(10,2) | 优惠金额 |
| max_discount | DECIMAL(10,2) | 最大优惠金额 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| status | TINYINT | 状态 |
| scope_type | VARCHAR(20) | 适用范围 |
| scope_ids | TEXT | 适用范围ID列表 |
| limit_per_user | INT | 每人限用次数 |
| used_count | INT | 总使用次数 |
| sort | INT | 排序 |

---

## 使用示例

### 示例 1: 创建秒杀活动

```bash
curl -X POST "http://localhost:8901/merchant/marketing/seckill?merchantId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 限时秒杀",
    "productId": 1,
    "skuId": 1,
    "seckillPrice": 6999.00,
    "originalPrice": 7999.00,
    "stock": 100,
    "limitPerUser": 2,
    "startTime": "2026-04-28T10:00:00",
    "endTime": "2026-04-28T22:00:00",
    "sort": 1
  }'
```

### 示例 2: 创建满减活动

```bash
curl -X POST "http://localhost:8901/merchant/marketing/discount?merchantId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "满200减30",
    "description": "全场商品满200元减30元",
    "discountType": 2,
    "conditionValue": 200.00,
    "discountAmount": 30.00,
    "maxDiscount": 30.00,
    "startTime": "2026-04-28T00:00:00",
    "endTime": "2026-05-28T23:59:59",
    "scopeType": "all",
    "sort": 1
  }'
```

### 示例 3: 查询进行中的秒杀活动

```bash
curl -X GET "http://localhost:8901/merchant/marketing/seckill?merchantId=1&status=1&pageNum=1&pageSize=10"
```

---

## 注意事项

1. **权限控制**: 所有接口都需要商家登录，并且只能操作自己店铺的活动
2. **时间验证**: 开始时间必须早于结束时间
3. **价格验证**: 秒杀价格必须低于原价
4. **库存管理**: 秒杀活动需要合理设置库存，避免超卖
5. **状态自动更新**: 建议配置定时任务自动更新活动状态
6. **并发控制**: 秒杀抢购需要使用分布式锁或乐观锁防止超卖

---

## 后续优化建议

1. **添加缓存**: 对热门秒杀活动进行 Redis 缓存
2. **限流保护**: 对秒杀接口添加限流机制
3. **消息队列**: 使用 MQ 异步处理秒杀订单
4. **数据统计**: 添加活动效果分析报表
5. **活动模板**: 提供常用活动模板快速创建
6. **批量操作**: 支持批量启用/停用活动
