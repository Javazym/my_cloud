-- 商家营销中心数据库迁移脚本

-- 1. 秒杀活动表
CREATE TABLE IF NOT EXISTS seckill_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT COMMENT 'SKU ID',
    name VARCHAR(100) NOT NULL COMMENT '活动名称',
    seckill_price DECIMAL(10, 2) NOT NULL COMMENT '秒杀价格',
    original_price DECIMAL(10, 2) NOT NULL COMMENT '原价',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    sold_count INT NOT NULL DEFAULT 0 COMMENT '已抢购数量',
    limit_per_user INT DEFAULT 1 COMMENT '每人限购数量',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已取消',
    sort INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- 2. 满减活动表
CREATE TABLE IF NOT EXISTS discount_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(100) NOT NULL COMMENT '活动名称',
    description TEXT COMMENT '活动描述',
    discount_type TINYINT NOT NULL COMMENT '满减类型：1-满件减，2-满额减',
    condition_value DECIMAL(10, 2) NOT NULL COMMENT '满足条件（件数或金额）',
    discount_amount DECIMAL(10, 2) NOT NULL COMMENT '优惠金额',
    max_discount DECIMAL(10, 2) COMMENT '最大优惠金额（封顶）',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未开始，1-进行中，2-已结束，3-已取消',
    scope_type VARCHAR(20) DEFAULT 'all' COMMENT '适用范围：all-全场商品，category-指定分类，product-指定商品',
    scope_ids TEXT COMMENT '适用范围ID列表（JSON格式）',
    limit_per_user INT COMMENT '每人限用次数',
    used_count INT NOT NULL DEFAULT 0 COMMENT '总使用次数',
    sort INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='满减活动表';

-- 3. 为优惠券表添加商家ID索引（如果不存在）
ALTER TABLE coupons ADD INDEX IF NOT EXISTS idx_merchant_id (merchant_id);

-- 4. 插入测试数据 - 秒杀活动
INSERT INTO seckill_activities (merchant_id, product_id, sku_id, name, seckill_price, original_price, stock, sold_count, limit_per_user, start_time, end_time, status, sort) VALUES
(1, 1, 1, 'iPhone 15 限时秒杀', 6999.00, 7999.00, 100, 0, 2, '2026-04-28 10:00:00', '2026-04-28 22:00:00', 1, 1),
(1, 2, 2, 'MacBook Pro 特价秒杀', 12999.00, 14999.00, 50, 0, 1, '2026-04-29 10:00:00', '2026-04-29 22:00:00', 0, 2);

-- 5. 插入测试数据 - 满减活动
INSERT INTO discount_activities (merchant_id, name, description, discount_type, condition_value, discount_amount, max_discount, start_time, end_time, status, scope_type, limit_per_user, used_count, sort) VALUES
(1, '满200减30', '全场商品满200元减30元', 2, 200.00, 30.00, 30.00, '2026-04-28 00:00:00', '2026-05-28 23:59:59', 1, 'all', NULL, 0, 1),
(1, '满3件减50', '指定商品满3件减50元', 1, 3.00, 50.00, 50.00, '2026-04-28 00:00:00', '2026-05-28 23:59:59', 1, 'product', NULL, 0, 2),
(1, '满500减100封顶', '全场满500减100，最高优惠100元', 2, 500.00, 100.00, 100.00, '2026-04-28 00:00:00', '2026-05-28 23:59:59', 1, 'all', 5, 0, 3);

-- 说明：
-- 1. seckill_activities 表用于存储秒杀活动信息
-- 2. discount_activities 表用于存储满减活动信息
-- 3. 优惠券管理复用现有的 coupons 表，通过 merchant_id 区分商家
-- 4. 所有活动都支持状态管理：0-未开始，1-进行中，2-已结束，3-已取消
-- 5. 建议配合定时任务自动更新活动状态
