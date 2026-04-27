-- =============================================
-- 电商平台数据库表结构
-- 数据库名: ecommerce
-- 字符集: utf8mb4
-- =============================================

-- 开启外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 1. 用户模块
-- =============================================

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `gender` TINYINT(1) DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `user_level` VARCHAR(20) DEFAULT 'normal' COMMENT '用户等级',
    `points` INT DEFAULT 0 COMMENT '积分',
    `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 收货地址表
CREATE TABLE IF NOT EXISTS `user_addresses` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省份',
    `city` VARCHAR(50) NOT NULL COMMENT '城市',
    `district` VARCHAR(50) NOT NULL COMMENT '区县',
    `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `postal_code` VARCHAR(10) DEFAULT NULL COMMENT '邮政编码',
    `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- =============================================
-- 2. 商品模块
-- =============================================

-- 商品分类表
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父分类ID: NULL-顶级分类',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `level` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '层级: 1-一级, 2-二级, 3-三级',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '分类图片',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 品牌表
CREATE TABLE IF NOT EXISTS `brands` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
    `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
    `logo` VARCHAR(255) DEFAULT NULL COMMENT '品牌Logo',
    `description` TEXT COMMENT '品牌描述',
    `first_letter` CHAR(1) DEFAULT NULL COMMENT '首字母',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_first_letter` (`first_letter`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品牌表';

-- 商品表
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
    `category_id` BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `brand_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '品牌ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `sub_name` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '主图',
    `images` TEXT COMMENT '图册(JSON数组)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `cost_price` DECIMAL(10,2) DEFAULT NULL COMMENT '成本价',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `sold_count` INT DEFAULT 0 COMMENT '销量',
    `review_count` INT DEFAULT 0 COMMENT '评价数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '评分',
    `description` TEXT COMMENT '商品描述',
    `detail` LONGTEXT COMMENT '商品详情(HTML)',
    `is_hot` TINYINT(1) DEFAULT 0 COMMENT '是否热卖: 0-否, 1-是',
    `is_featured` TINYINT(1) DEFAULT 0 COMMENT '是否精选: 0-否, 1-是',
    `is_new` TINYINT(1) DEFAULT 0 COMMENT '是否新品: 0-否, 1-是',
    `publish_status` TINYINT(1) DEFAULT 0 COMMENT '上架状态: 0-下架, 1-上架',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签(JSON数组)',
    `keywords` VARCHAR(255) DEFAULT NULL COMMENT '关键词',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`),
    KEY `idx_is_hot` (`is_hot`),
    KEY `idx_is_featured` (`is_featured`),
    KEY `idx_publish_status` (`publish_status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 商品规格表
CREATE TABLE IF NOT EXISTS `product_specs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '规格ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `name` VARCHAR(50) NOT NULL COMMENT '规格名称(如: 颜色, 尺寸)',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格表';

-- 商品规格值表
CREATE TABLE IF NOT EXISTS `product_spec_values` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '规格值ID',
    `spec_id` BIGINT UNSIGNED NOT NULL COMMENT '规格ID',
    `value` VARCHAR(50) NOT NULL COMMENT '规格值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_spec_id` (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品规格值表';

-- 商品SKU表
CREATE TABLE IF NOT EXISTS `product_skus` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `sku_code` VARCHAR(64) NOT NULL COMMENT 'SKU编码',
    `specs` JSON COMMENT '规格组合(JSON)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '销售价',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `low_stock` INT DEFAULT 10 COMMENT '库存预警值',
    `image` VARCHAR(255) DEFAULT NULL COMMENT 'SKU图片',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sku_code` (`sku_code`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_stock` (`stock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品SKU表';

-- =============================================
-- 3. 购物车模块
-- =============================================

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT UNSIGNED DEFAULT NULL COMMENT 'SKU ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `checked` TINYINT(1) DEFAULT 1 COMMENT '是否选中: 0-否, 1-是',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- =============================================
-- 4. 订单模块
-- =============================================

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
    `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
    `coupon_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠券金额',
    `freight_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    `pay_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    `points` INT DEFAULT 0 COMMENT '消耗积分',
    `status` TINYINT(2) NOT NULL DEFAULT 0 COMMENT '订单状态: 0-待付款, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-已退款',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间',
    `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人',
    `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货电话',
    `receiver_address` VARCHAR(255) NOT NULL COMMENT '收货地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单商品表
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `sku_id` BIGINT UNSIGNED DEFAULT NULL COMMENT 'SKU ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    `sku_specs` VARCHAR(255) DEFAULT NULL COMMENT 'SKU规格',
    `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '总价',
    `review_status` TINYINT(1) DEFAULT 0 COMMENT '评价状态: 0-未评价, 1-已评价',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- 物流信息表
CREATE TABLE IF NOT EXISTS `order_logistics` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '物流ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `logistics_company` VARCHAR(50) DEFAULT NULL COMMENT '物流公司',
    `tracking_number` VARCHAR(64) DEFAULT NULL COMMENT '物流单号',
    `current_status` VARCHAR(50) DEFAULT NULL COMMENT '当前状态',
    `traces` JSON DEFAULT NULL COMMENT '物流轨迹(JSON)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流信息表';

-- 退款/售后表
CREATE TABLE IF NOT EXISTS `order_refunds` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    `refund_no` VARCHAR(64) NOT NULL COMMENT '退款单号',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_item_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '订单商品ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
    `type` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '类型: 0-退款, 1-退货退款',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `reason` VARCHAR(200) DEFAULT NULL COMMENT '退款原因',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '退款说明',
    `images` TEXT COMMENT '图片凭证(JSON数组)',
    `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态: 0-申请中, 1-同意, 2-拒绝, 3-已退款',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_refund_no` (`refund_no`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款/售后表';

-- 发票表
CREATE TABLE IF NOT EXISTS `invoices` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '发票ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `type` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '类型: 0-个人, 1-企业',
    `title` VARCHAR(100) DEFAULT NULL COMMENT '发票抬头',
    `tax_number` VARCHAR(50) DEFAULT NULL COMMENT '税号',
    `content` VARCHAR(200) DEFAULT NULL COMMENT '发票内容',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态: 0-未开, 1-已开',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发票表';

-- =============================================
-- 5. 商家模块
-- =============================================

-- 商家表
CREATE TABLE IF NOT EXISTS `merchants` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '商家ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户ID',
    `store_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `store_logo` VARCHAR(255) DEFAULT NULL COMMENT '店铺Logo',
    `store_banner` VARCHAR(255) DEFAULT NULL COMMENT '店铺横幅',
    `store_description` TEXT COMMENT '店铺简介',
    `store_type` VARCHAR(20) DEFAULT 'normal' COMMENT '店铺类型: normal-普通, flagship-旗舰店',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '主营类目',
    `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    `rating` DECIMAL(2,1) DEFAULT 5.0 COMMENT '店铺评分',
    `sales` BIGINT DEFAULT 0 COMMENT '总销售额',
    `followers` INT DEFAULT 0 COMMENT '关注数',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `audit_status` TINYINT(1) DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-通过, 2-驳回',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_store_name` (`store_name`),
    KEY `idx_status` (`status`),
    KEY `idx_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家表';

-- 商家入驻申请表
CREATE TABLE IF NOT EXISTS `merchant_applications` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `store_name` VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `store_type` VARCHAR(20) DEFAULT 'normal' COMMENT '店铺类型',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '主营类目',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    `business_license` VARCHAR(255) DEFAULT NULL COMMENT '营业执照',
    `id_card_front` VARCHAR(255) DEFAULT NULL COMMENT '身份证正面',
    `id_card_back` VARCHAR(255) DEFAULT NULL COMMENT '身份证背面',
    `status` TINYINT(1) DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-通过, 2-驳回',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家入驻申请表';

-- =============================================
-- 6. 优惠券模块
-- =============================================

-- 优惠券表
CREATE TABLE IF NOT EXISTS `coupons` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `merchant_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '商家ID: 0-平台券',
    `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    `type` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '类型: 0-满减, 1-折扣',
    `value` DECIMAL(10,2) NOT NULL COMMENT '面值/折扣',
    `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费',
    `max_discount` DECIMAL(10,2) DEFAULT NULL COMMENT '最高优惠',
    `total_count` INT DEFAULT 0 COMMENT '发放总量',
    `receive_count` INT DEFAULT 0 COMMENT '已领取数量',
    `used_count` INT DEFAULT 0 COMMENT '已使用数量',
    `limit_per_user` INT DEFAULT 1 COMMENT '每人限领',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `valid_days` INT DEFAULT NULL COMMENT '领取后天数',
    `scope` VARCHAR(20) DEFAULT 'all' COMMENT '使用范围: all-全场, category-分类, product-商品',
    `category_ids` VARCHAR(500) DEFAULT NULL COMMENT '分类ID(JSON数组)',
    `product_ids` VARCHAR(500) DEFAULT NULL COMMENT '商品ID(JSON数组)',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='优惠券表';

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS `user_coupons` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户优惠券ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `coupon_id` BIGINT UNSIGNED NOT NULL COMMENT '优惠券ID',
    `coupon_name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    `coupon_type` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '优惠券类型',
    `value` DECIMAL(10,2) NOT NULL COMMENT '面值/折扣',
    `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费',
    `max_discount` DECIMAL(10,2) DEFAULT NULL COMMENT '最高优惠',
    `order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '使用的订单ID',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态: 0-未使用, 1-已使用, 2-已过期',
    `receive_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    `use_time` DATETIME DEFAULT NULL COMMENT '使用时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_coupon_id` (`coupon_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';

-- =============================================
-- 7. 收藏模块
-- =============================================

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorites` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =============================================
-- 8. 评价模块
-- =============================================

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_reviews` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    `order_item_id` BIGINT UNSIGNED NOT NULL COMMENT '订单商品ID',
    `product_id` BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) DEFAULT NULL COMMENT '用户名(脱敏)',
    `user_avatar` VARCHAR(255) DEFAULT NULL COMMENT '用户头像',
    `rating` TINYINT(1) NOT NULL DEFAULT 5 COMMENT '评分: 1-5',
    `content` TEXT COMMENT '评价内容',
    `images` TEXT COMMENT '评价图片(JSON数组)',
    `anonymous` TINYINT(1) DEFAULT 0 COMMENT '是否匿名: 0-否, 1-是',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品评价表';

-- 评价回复表
CREATE TABLE IF NOT EXISTS `review_replies` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `review_id` BIGINT UNSIGNED NOT NULL COMMENT '评价ID',
    `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
    `content` VARCHAR(500) NOT NULL COMMENT '回复内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_review_id` (`review_id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价回复表';

-- =============================================
-- 9. 管理员模块
-- =============================================

-- 管理员表
CREATE TABLE IF NOT EXISTS `admins` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `role_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '角色ID',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 角色表
CREATE TABLE IF NOT EXISTS `roles` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permissions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `code` VARCHAR(50) NOT NULL COMMENT '权限编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `type` VARCHAR(20) DEFAULT NULL COMMENT '类型: menu-菜单, button-按钮',
    `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父权限ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permissions` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =============================================
-- 10. 运营模块
-- =============================================

-- 轮播图表
CREATE TABLE IF NOT EXISTS `banners` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `image` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `link` VARCHAR(255) DEFAULT NULL COMMENT '链接地址',
    `link_type` VARCHAR(20) DEFAULT NULL COMMENT '链接类型: product-商品, category-分类, url-网页',
    `position` TINYINT(1) DEFAULT 0 COMMENT '位置: 0-首页, 1-分类页',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `click_count` INT DEFAULT 0 COMMENT '点击次数',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_position` (`position`),
    KEY `idx_status` (`status`),
    KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 公告表
CREATE TABLE IF NOT EXISTS `announcements` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `type` TINYINT(1) DEFAULT 0 COMMENT '类型: 0-系统, 1-活动',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- =============================================
-- 11. 财务模块
-- =============================================

-- 商家账户表
CREATE TABLE IF NOT EXISTS `merchant_accounts` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '账户ID',
    `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
    `total_income` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总收入',
    `withdrawable` DECIMAL(12,2) DEFAULT 0.00 COMMENT '可提现',
    `withdrawn` DECIMAL(12,2) DEFAULT 0.00 COMMENT '已提现',
    `pending_withdraw` DECIMAL(12,2) DEFAULT 0.00 COMMENT '待提现',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商家账户表';

-- 提现记录表
CREATE TABLE IF NOT EXISTS `withdraw_records` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '提现ID',
    `merchant_id` BIGINT UNSIGNED NOT NULL COMMENT '商家ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '提现金额',
    `fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '手续费',
    `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实际到账',
    `bank_name` VARCHAR(50) DEFAULT NULL COMMENT '银行名称',
    `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    `account_name` VARCHAR(50) DEFAULT NULL COMMENT '账户姓名',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待审核, 1-审核通过, 2-已打款, 3-拒绝',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `process_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- =============================================
-- 12. 营销活动模块
-- =============================================

-- 营销活动表
CREATE TABLE IF NOT EXISTS `marketing_activities` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `merchant_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '商家ID: 0-平台活动',
    `name` VARCHAR(100) NOT NULL COMMENT '活动名称',
    `type` VARCHAR(20) NOT NULL COMMENT '活动类型: seckill-秒杀, group-拼团, discount-折扣',
    `description` TEXT COMMENT '活动描述',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态: 0-未开始, 1-进行中, 2-已结束',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='营销活动表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化超级管理员 (密码: admin123)
INSERT INTO `admins` (`username`, `password`, `nickname`, `role_id`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 1, 1);

-- 初始化角色
INSERT INTO `roles` (`name`, `code`, `description`, `status`) VALUES
('超级管理员', 'super_admin', '拥有所有权限', 1),
('运营人员', 'operator', '负责运营管理', 1),
('财务人员', 'finance', '负责财务管理', 1);

-- 初始化分类
INSERT INTO `categories` (`name`, `parent_id`, `level`, `sort`, `status`) VALUES
('数码电子', 0, 1, 1, 1),
('服饰服装', 0, 1, 2, 1),
('家居生活', 0, 1, 3, 1);

-- 初始化品牌
INSERT INTO `brands` (`name`, `first_letter`, `sort`, `status`) VALUES
('Apple', 'A', 1, 1),
('Nike', 'N', 2, 1),
('Adidas', 'A', 3, 1),
('戴森', 'D', 4, 1),
('小米', 'X', 5, 1),
('华为', 'H', 6, 1),
('三星', 'S', 7, 1),
('索尼', 'S', 8, 1);
