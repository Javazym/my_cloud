---
AIGC:
    ContentProducer: Minimax Agent AI
    ContentPropagator: Minimax Agent AI
    Label: AIGC
    ProduceID: "00000000000000000000000000000000"
    PropagateID: "00000000000000000000000000000000"
    ReservedCode1: 3044022021e48e8ff323ed57d2ff9d7292a067018fe21dbaf5f769bbafb0c85b76d0f57902206c64322f67a2a72344a8d11e04471510c53f8f53463b1dc92226cfa69cd7c9e5
    ReservedCode2: 304402200119462bfd70cb251499ad30cd404f393d888c05ecb5754b6cf28e098ba5d9fd02206f035c20c041da99e192d1c789868136d435c69a36426ef601062c4229ff0123
---

# 电商平台数据库表结构说明文档

## 一、数据库概述

本文档详细说明电商平台数据库的表结构设计，包含用户、商品、订单、购物车、商家、优惠券、收藏、评价、管理员、运营等核心模块的数据库表设计。数据库采用 MySQL 8.0 及以上版本，存储引擎为 InnoDB，字符集为 utf8mb4。

## 二、表结构总览

本项目共设计 30 张核心数据表，涵盖电商平台的全部业务功能。具体包括用户模块 2 张表、商品模块 6 张表、购物车模块 1 张表、订单模块 5 张表、商家模块 2 张表、优惠券模块 2 张表、收藏模块 1 张表、评价模块 2 张表、管理员模块 5 张表、运营模块 2 张表、财务模块 2 张表、营销模块 1 张表。

## 三、详细表结构说明

### 3.1 用户模块

#### 3.1.1 用户表（users）

用户表是平台的核心表之一，存储所有用户的基本信息。每个用户拥有唯一的用户名、手机号和邮箱地址，用于登录认证和个人信息关联。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 用户ID，唯一标识 |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名，用于登录 |
| password | VARCHAR(255) | NOT NULL | 加密后的密码 |
| nickname | VARCHAR(50) | NULL | 用户昵称 |
| avatar | VARCHAR(255) | NULL | 头像URL地址 |
| mobile | VARCHAR(20) | NULL, UNIQUE | 手机号 |
| email | VARCHAR(100) | NULL, UNIQUE | 电子邮箱 |
| gender | TINYINT(1) | DEFAULT 0 | 性别：0-未知，1-男，2-女 |
| birthday | DATE | NULL | 出生日期 |
| status | TINYINT(1) | DEFAULT 1 | 账户状态：0-禁用，1-正常 |
| user_level | VARCHAR(20) | DEFAULT 'normal' | 用户等级 |
| points | INT | DEFAULT 0 | 积分余额 |
| balance | DECIMAL(10,2) | DEFAULT 0.00 | 账户余额 |
| last_login_time | DATETIME | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(50) | NULL | 最后登录IP |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

该表建立了多个索引以提升查询性能：username、mobile、email 建立了唯一索引确保数据唯一性；status 和 created_at 建立了普通索引用于常见查询条件。

#### 3.1.2 收货地址表（user_addresses）

收货地址表存储用户的收货地址信息，支持多个收货地址管理。每个用户可以创建多个收货地址，并可设置其中一个为默认地址。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 地址ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 关联用户ID |
| receiver_name | VARCHAR(50) | NOT NULL | 收货人姓名 |
| receiver_phone | VARCHAR(20) | NOT NULL | 收货人电话 |
| province | VARCHAR(50) | NOT NULL | 省份 |
| city | VARCHAR(50) | NOT NULL | 城市 |
| district | VARCHAR(50) | NOT NULL | 区县 |
| detail_address | VARCHAR(255) | NOT NULL | 详细地址 |
| postal_code | VARCHAR(10) | NULL | 邮政编码 |
| is_default | TINYINT(1) | DEFAULT 0 | 是否默认：0-否，1-是 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

该表通过 user_id 建立索引关联用户，通过 is_default 索引快速查询默认地址。

### 3.2 商品模块

#### 3.2.1 商品分类表（categories）

商品分类表采用树形结构设计，支持多级分类。parent_id 字段为 0 表示顶级分类，通过 level 字段标识分类层级。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 分类ID |
| parent_id | BIGINT UNSIGNED | DEFAULT 0 | 父分类ID，0为顶级分类 |
| name | VARCHAR(50) | NOT NULL | 分类名称 |
| level | TINYINT(1) | NOT NULL DEFAULT 1 | 层级：1-一级，2-二级，3-三级 |
| icon | VARCHAR(255) | NULL | 分类图标 |
| image | VARCHAR(255) | NULL | 分类图片 |
| sort | INT | DEFAULT 0 | 排序值 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.2.2 品牌表（brands）

品牌表存储商品品牌信息，支持品牌 LOGO 和描述信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 品牌ID |
| name | VARCHAR(100) | NOT NULL, UNIQUE | 品牌名称 |
| logo | VARCHAR(255) | NULL | 品牌LOGO |
| description | TEXT | NULL | 品牌描述 |
| first_letter | CHAR(1) | NULL | 首字母，用于索引 |
| sort | INT | DEFAULT 0 | 排序值 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.2.3 商品表（products）

商品表是商品模块的核心表，存储商品的基本信息、主图、价格、库存等数据。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 商品ID |
| merchant_id | BIGINT UNSIGNED | NOT NULL | 商家ID |
| category_id | BIGINT UNSIGNED | NOT NULL | 分类ID |
| brand_id | BIGINT UNSIGNED | NULL | 品牌ID |
| name | VARCHAR(200) | NOT NULL | 商品名称 |
| sub_name | VARCHAR(200) | NULL | 副标题 |
| image | VARCHAR(255) | NULL | 主图 |
| images | TEXT | NULL | 图册，JSON数组格式 |
| price | DECIMAL(10,2) | NOT NULL | 销售价格 |
| original_price | DECIMAL(10,2) | NULL | 原价 |
| cost_price | DECIMAL(10,2) | NULL | 成本价 |
| stock | INT | DEFAULT 0 | 库存数量 |
| sold_count | INT | DEFAULT 0 | 销量 |
| review_count | INT | DEFAULT 0 | 评价数 |
| favorite_count | INT | DEFAULT 0 | 收藏数 |
| rating | DECIMAL(2,1) | DEFAULT 5.0 | 商品评分 |
| description | TEXT | NULL | 商品简单描述 |
| detail | LONGTEXT | NULL | 商品详情HTML |
| is_hot | TINYINT(1) | DEFAULT 0 | 是否热卖：0-否，1-是 |
| is_featured | TINYINT(1) | DEFAULT 0 | 是否精选：0-否，1-是 |
| is_new | TINYINT(1) | DEFAULT 0 | 是否新品：0-否，1-是 |
| publish_status | TINYINT(1) | DEFAULT 0 | 上架状态：0-下架，1-上架 |
| tags | VARCHAR(500) | NULL | 标签，JSON数组格式 |
| keywords | VARCHAR(255) | NULL | 搜索关键词 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

该表建立了多个业务索引：merchant_id 用于商家商品查询；category_id 用于分类筛选；is_hot、is_featured、publish_status 用于首页推荐和筛选。

#### 3.2.4 商品规格表（product_specs）

商品规格表用于定义商品的规格属性，如颜色、尺寸、版本等。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 规格ID |
| product_id | BIGINT UNSIGNED | NOT NULL | 关联商品ID |
| name | VARCHAR(50) | NOT NULL | 规格名称 |
| sort | INT | DEFAULT 0 | 排序值 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.2.5 商品规格值表（product_spec_values）

商品规格值表存储规格的具体可选值，如红色的具体 RGB 值、尺寸的 S/M/L 等。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 规格值ID |
| spec_id | BIGINT UNSIGNED | NOT NULL | 关联规格ID |
| value | VARCHAR(50) | NOT NULL | 规格值 |
| sort | INT | DEFAULT 0 | 排序值 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.2.6 商品SKU表（product_skus）

商品 SKU 表存储商品的 SKU（Stock Keeping Unit）信息，每个 SKU 代表一个具体的商品规格组合，拥有独立的价格和库存。这是实现多规格商品管理的核心表。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | SKU ID |
| product_id | BIGINT UNSIGNED | NOT NULL | 关联商品ID |
| sku_code | VARCHAR(64) | NOT NULL, UNIQUE | SKU编码 |
| specs | JSON | NULL | 规格组合，JSON格式 |
| price | DECIMAL(10,2) | NOT NULL | 销售价 |
| original_price | DECIMAL(10,2) | NULL | 原价 |
| stock | INT | DEFAULT 0 | 库存数量 |
| low_stock | INT | DEFAULT 10 | 库存预警值 |
| image | VARCHAR(255) | NULL | SKU图片 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.3 购物车模块

#### 3.3.1 购物车表（cart_items）

购物车表存储用户添加到购物车的商品信息，支持多商品、多规格管理。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 购物车ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| product_id | BIGINT UNSIGNED | NOT NULL | 商品ID |
| sku_id | BIGINT UNSIGNED | NULL | SKU ID |
| quantity | INT | NOT NULL DEFAULT 1 | 购买数量 |
| checked | TINYINT(1) | DEFAULT 1 | 是否选中：0-否，1-是 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.4 订单模块

#### 3.4.1 订单表（orders）

订单表是交易模块的核心表，存储订单的主信息。订单号（order_no）全局唯一，用于订单追溯。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 订单ID |
| order_no | VARCHAR(64) | NOT NULL, UNIQUE | 订单号 |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| merchant_id | BIGINT UNSIGNED | NOT NULL | 商家ID |
| total_amount | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 | 订单总额 |
| discount_amount | DECIMAL(10,2) | DEFAULT 0.00 | 优惠金额 |
| coupon_amount | DECIMAL(10,2) | DEFAULT 0.00 | 优惠券金额 |
| freight_amount | DECIMAL(10,2) | DEFAULT 0.00 | 运费金额 |
| pay_amount | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 | 实付金额 |
| points | INT | DEFAULT 0 | 消耗积分 |
| status | TINYINT(2) | NOT NULL DEFAULT 0 | 订单状态 |
| pay_time | DATETIME | NULL | 支付时间 |
| ship_time | DATETIME | NULL | 发货时间 |
| receive_time | DATETIME | NULL | 收货时间 |
| finish_time | DATETIME | NULL | 完成时间 |
| receiver_name | VARCHAR(50) | NOT NULL | 收货人 |
| receiver_phone | VARCHAR(20) | NOT NULL | 收货电话 |
| receiver_address | VARCHAR(255) | NOT NULL | 收货地址 |
| remark | VARCHAR(500) | NULL | 订单备注 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

订单状态说明：0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-已退款。

#### 3.4.2 订单商品表（order_items）

订单商品表存储订单中的商品明细，采用快照方式存储商品信息，确保订单创建后商品信息变更不影响历史订单。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 订单商品ID |
| order_id | BIGINT UNSIGNED | NOT NULL | 订单ID |
| order_no | VARCHAR(64) | NOT NULL | 订单号 |
| product_id | BIGINT UNSIGNED | NOT NULL | 商品ID |
| sku_id | BIGINT UNSIGNED | NULL | SKU ID |
| product_name | VARCHAR(200) | NOT NULL | 商品名称快照 |
| product_image | VARCHAR(255) | NULL | 商品图片快照 |
| sku_specs | VARCHAR(255) | NULL | SKU规格快照 |
| product_price | DECIMAL(10,2) | NOT NULL | 商品单价快照 |
| quantity | INT | NOT NULL | 购买数量 |
| total_price | DECIMAL(10,2) | NOT NULL | 总价 |
| review_status | TINYINT(1) | DEFAULT 0 | 评价状态：0-未评价，1-已评价 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.4.3 物流信息表（order_logistics）

物流信息表存储订单的物流跟踪信息，包含物流公司和单号。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 物流ID |
| order_id | BIGINT UNSIGNED | NOT NULL | 订单ID |
| logistics_company | VARCHAR(50) | NULL | 物流公司 |
| tracking_number | VARCHAR(64) | NULL | 物流单号 |
| current_status | VARCHAR(50) | NULL | 当前状态 |
| traces | JSON | NULL | 物流轨迹 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.4.4 退款/售后表（order_refunds）

退款/售后表存储用户的退款和退货申请信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 退款ID |
| refund_no | VARCHAR(64) | NOT NULL, UNIQUE | 退款单号 |
| order_id | BIGINT UNSIGNED | NOT NULL | 订单ID |
| order_item_id | BIGINT UNSIGNED | NULL | 订单商品ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| merchant_id | BIGINT UNSIGNED | NOT NULL | 商家ID |
| type | TINYINT(1) | NOT NULL DEFAULT 0 | 类型：0-退款，1-退货退款 |
| amount | DECIMAL(10,2) | NOT NULL | 退款金额 |
| reason | VARCHAR(200) | NULL | 退款原因 |
| description | VARCHAR(500) | NULL | 退款说明 |
| images | TEXT | NULL | 图片凭证，JSON数组 |
| status | TINYINT(1) | NOT NULL DEFAULT 0 | 状态 |
| reject_reason | VARCHAR(255) | NULL | 拒绝原因 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.4.5 发票表（invoices）

发票表存储用户申请的开票信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 发票ID |
| order_id | BIGINT UNSIGNED | NOT NULL | 订单ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| type | TINYINT(1) | NOT NULL DEFAULT 0 | 类型：0-个人，1-企业 |
| title | VARCHAR(100) | NULL | 发票抬头 |
| tax_number | VARCHAR(50) | NULL | 税号 |
| content | VARCHAR(200) | NULL | 发票内容 |
| status | TINYINT(1) | DEFAULT 0 | 状态：0-未开，1-已开 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.5 商家模块

#### 3.5.1 商家表（merchants）

商家表存储商家的店铺信息，包括店铺名称、联系方式、评分等。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 商家ID |
| user_id | BIGINT UNSIGNED | NOT NULL, UNIQUE | 关联用户ID |
| store_name | VARCHAR(100) | NOT NULL | 店铺名称 |
| store_logo | VARCHAR(255) | NULL | 店铺LOGO |
| store_banner | VARCHAR(255) | NULL | 店铺横幅 |
| store_description | TEXT | NULL | 店铺简介 |
| store_type | VARCHAR(20) | DEFAULT 'normal' | 店铺类型 |
| category | VARCHAR(50) | NULL | 主营类目 |
| contact_name | VARCHAR(50) | NULL | 联系人 |
| contact_phone | VARCHAR(20) | NULL | 联系电话 |
| contact_email | VARCHAR(100) | NULL | 联系邮箱 |
| rating | DECIMAL(2,1) | DEFAULT 5.0 | 店铺评分 |
| sales | BIGINT | DEFAULT 0 | 总销售额 |
| followers | INT | DEFAULT 0 | 关注数 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-正常 |
| audit_status | TINYINT(1) | DEFAULT 0 | 审核状态 |
| audit_time | DATETIME | NULL | 审核时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.5.2 商家入驻申请表（merchant_applications）

商家入驻申请表存储商家提交的入驻申请信息，用于平台审核。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 申请ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| store_name | VARCHAR(100) | NOT NULL | 店铺名称 |
| store_type | VARCHAR(20) | DEFAULT 'normal' | 店铺类型 |
| category | VARCHAR(50) | NULL | 主营类目 |
| contact_name | VARCHAR(50) | NOT NULL | 联系人 |
| contact_phone | VARCHAR(20) | NOT NULL | 联系电话 |
| contact_email | VARCHAR(100) | NULL | 联系邮箱 |
| business_license | VARCHAR(255) | NULL | 营业执照 |
| id_card_front | VARCHAR(255) | NULL | 身份证正面 |
| id_card_back | VARCHAR(255) | NULL | 身份证背面 |
| status | TINYINT(1) | DEFAULT 0 | 审核状态 |
| remark | VARCHAR(255) | NULL | 审核备注 |
| apply_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 申请时间 |
| audit_time | DATETIME | NULL | 审核时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.6 优惠券模块

#### 3.6.1 优惠券表（coupons）

优惠券表存储优惠券模板信息，支持满减券和折扣券两种类型。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 优惠券ID |
| merchant_id | BIGINT UNSIGNED | DEFAULT 0 | 商家ID：0表示平台券 |
| name | VARCHAR(100) | NOT NULL | 优惠券名称 |
| type | TINYINT(1) | NOT NULL DEFAULT 0 | 类型：0-满减，1-折扣 |
| value | DECIMAL(10,2) | NOT NULL | 面值或折扣值 |
| min_amount | DECIMAL(10,2) | DEFAULT 0.00 | 最低消费金额 |
| max_discount | DECIMAL(10,2) | NULL | 最高优惠金额 |
| total_count | INT | DEFAULT 0 | 发放总量 |
| receive_count | INT | DEFAULT 0 | 已领取数量 |
| used_count | INT | DEFAULT 0 | 已使用数量 |
| limit_per_user | INT | DEFAULT 1 | 每人限领数量 |
| start_time | DATETIME | NOT NULL | 开始时间 |
| end_time | DATETIME | NOT NULL | 结束时间 |
| valid_days | INT | NULL | 领取后天数 |
| scope | VARCHAR(20) | DEFAULT 'all' | 使用范围 |
| category_ids | VARCHAR(500) | NULL | 分类ID，JSON数组 |
| product_ids | VARCHAR(500) | NULL | 商品ID，JSON数组 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.6.2 用户优惠券表（user_coupons）

用户优惠券表存储用户领取的优惠券信息，记录优惠券的领取和使用状态。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 用户优惠券ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| coupon_id | BIGINT UNSIGNED | NOT NULL | 优惠券ID |
| coupon_name | VARCHAR(100) | NOT NULL | 优惠券名称 |
| coupon_type | TINYINT(1) | NOT NULL DEFAULT 0 | 优惠券类型 |
| value | DECIMAL(10,2) | NOT NULL | 面值或折扣值 |
| min_amount | DECIMAL(10,2) | DEFAULT 0.00 | 最低消费 |
| max_discount | DECIMAL(10,2) | NULL | 最高优惠 |
| order_id | BIGINT UNSIGNED | NULL | 使用的订单ID |
| status | TINYINT(1) | DEFAULT 0 | 状态 |
| receive_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 领取时间 |
| use_time | DATETIME | NULL | 使用时间 |
| expire_time | DATETIME | NULL | 过期时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.7 收藏模块

#### 3.7.1 收藏表（favorites）

收藏表存储用户的商品收藏信息，采用联合唯一索引防止重复收藏。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 收藏ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| product_id | BIGINT UNSIGNED | NOT NULL | 商品ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

该表建立了 user_id 和 product_id 的联合唯一索引，确保同一用户不能重复收藏同一商品。

### 3.8 评价模块

#### 3.8.1 商品评价表（product_reviews）

商品评价表存储用户对商品的评价信息，支持评分、文字评价和图片评价。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 评价ID |
| order_id | BIGINT UNSIGNED | NOT NULL | 订单ID |
| order_item_id | BIGINT UNSIGNED | NOT NULL | 订单商品ID |
| product_id | BIGINT UNSIGNED | NOT NULL | 商品ID |
| user_id | BIGINT UNSIGNED | NOT NULL | 用户ID |
| user_name | VARCHAR(50) | NULL | 用户名（脱敏） |
| user_avatar | VARCHAR(255) | NULL | 用户头像 |
| rating | TINYINT(1) | NOT NULL DEFAULT 5 | 评分：1-5 |
| content | TEXT | NULL | 评价内容 |
| images | TEXT | NULL | 评价图片，JSON数组 |
| anonymous | TINYINT(1) | DEFAULT 0 | 是否匿名：0-否，1-是 |
| like_count | INT | DEFAULT 0 | 点赞数 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-隐藏，1-显示 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.8.2 评价回复表（review_replies）

评价回复表存储商家对用户评价的回复信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 回复ID |
| review_id | BIGINT UNSIGNED | NOT NULL | 评价ID |
| merchant_id | BIGINT UNSIGNED | NOT NULL | 商家ID |
| content | VARCHAR(500) | NOT NULL | 回复内容 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.9 管理员模块

#### 3.9.1 管理员表（admins）

管理员表存储平台管理员账户信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 管理员ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码 |
| nickname | VARCHAR(50) | NULL | 昵称 |
| avatar | VARCHAR(255) | NULL | 头像 |
| role_id | BIGINT UNSIGNED | NULL | 角色ID |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-正常 |
| last_login_time | DATETIME | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(50) | NULL | 最后登录IP |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.9.2 角色表（roles）

角色表存储系统角色信息，用于权限管理。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 角色ID |
| name | VARCHAR(50) | NOT NULL | 角色名称 |
| code | VARCHAR(50) | NOT NULL, UNIQUE | 角色编码 |
| description | VARCHAR(255) | NULL | 描述 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.9.3 权限表（permissions）

权限表存储系统的权限信息，支持菜单和按钮级别的权限控制。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 权限ID |
| name | VARCHAR(50) | NOT NULL | 权限名称 |
| code | VARCHAR(50) | NOT NULL, UNIQUE | 权限编码 |
| description | VARCHAR(255) | NULL | 描述 |
| type | VARCHAR(20) | NULL | 类型：menu-菜单，button-按钮 |
| parent_id | BIGINT UNSIGNED | DEFAULT 0 | 父权限ID |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.9.4 角色权限关联表（role_permissions）

角色权限关联表实现角色与权限的多对多关系。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | ID |
| role_id | BIGINT UNSIGNED | NOT NULL | 角色ID |
| permission_id | BIGINT UNSIGNED | NOT NULL | 权限ID |

### 3.10 运营模块

#### 3.10.1 轮播图表（banners）

轮播图表存储首页和分类页的轮播图配置信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 轮播图ID |
| title | VARCHAR(100) | NOT NULL | 标题 |
| image | VARCHAR(255) | NOT NULL | 图片URL |
| link | VARCHAR(255) | NULL | 链接地址 |
| link_type | VARCHAR(20) | NULL | 链接类型 |
| position | TINYINT(1) | DEFAULT 0 | 位置：0-首页，1-分类页 |
| sort | INT | DEFAULT 0 | 排序 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| start_time | DATETIME | NULL | 开始时间 |
| end_time | DATETIME | NULL | 结束时间 |
| click_count | INT | DEFAULT 0 | 点击次数 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

#### 3.10.2 公告表（announcements）

公告表存储平台公告信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 公告ID |
| title | VARCHAR(100) | NOT NULL | 标题 |
| content | TEXT | NOT NULL | 内容 |
| type | TINYINT(1) | DEFAULT 0 | 类型：0-系统，1-活动 |
| status | TINYINT(1) | DEFAULT 1 | 状态：0-禁用，1-启用 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.11 财务模块

#### 3.11.1 商家账户表（merchant_accounts）

商家账户表存储商家的账户财务信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 账户ID |
| merchant_id | BIGINT UNSIGNED | NOT NULL, UNIQUE | 商家ID |
| total_income | DECIMAL(12,2) | DEFAULT 0.00 | 总收入 |
| withdrawable | DECIMAL(12,2) | DEFAULT 0.00 | 可提现金额 |
| withdrawn | DECIMAL(12,2) | DEFAULT 0.00 | 已提现金额 |
| pending_withdraw | DECIMAL(12,2) | DEFAULT 0.00 | 待提现金额 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |

#### 3.11.2 提现记录表（withdraw_records）

提现记录表存储商家的提现申请记录。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 提现ID |
| merchant_id | BIGINT UNSIGNED | NOT NULL | 商家ID |
| amount | DECIMAL(10,2) | NOT NULL | 提现金额 |
| fee | DECIMAL(10,2) | DEFAULT 0.00 | 手续费 |
| actual_amount | DECIMAL(10,2) | NOT NULL | 实际到账金额 |
| bank_name | VARCHAR(50) | NULL | 银行名称 |
| bank_account | VARCHAR(50) | NULL | 银行账号 |
| account_name | VARCHAR(50) | NULL | 账户姓名 |
| status | TINYINT(1) | DEFAULT 0 | 状态 |
| remark | VARCHAR(255) | NULL | 备注 |
| apply_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 申请时间 |
| process_time | DATETIME | NULL | 处理时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

### 3.12 营销模块

#### 3.12.1 营销活动表（marketing_activities）

营销活动表存储平台的营销活动配置信息。表结构设计如下：

| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | BIGINT UNSIGNED | PRIMARY KEY, AUTO_INCREMENT | 活动ID |
| merchant_id | BIGINT UNSIGNED | DEFAULT 0 | 商家ID：0表示平台活动 |
| name | VARCHAR(100) | NOT NULL | 活动名称 |
| type | VARCHAR(20) | NOT NULL | 活动类型 |
| description | TEXT | NULL | 活动描述 |
| start_time | DATETIME | NOT NULL | 开始时间 |
| end_time | DATETIME | NOT NULL | 结束时间 |
| status | TINYINT(1) | DEFAULT 0 | 状态 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| is_deleted | TINYINT(1) | DEFAULT 0 | 逻辑删除标志 |

## 四、通用字段说明

本项目所有业务表均采用统一的字段规范，确保数据一致性和可维护性。

### 4.1 审计字段

所有表均包含以下审计字段：created_at 表示记录创建时间，默认值为当前时间；updated_at 表示记录更新时间，支持自动更新；is_deleted 表示逻辑删除标志，0 表示正常，1 表示已删除。

### 4.2 主键规范

所有表的主键均采用 BIGINT UNSIGNED 类型，使用 AUTO_INCREMENT 自增策略，确保主键的唯一性和递增性。

### 4.3 金额字段规范

所有涉及金额的字段均采用 DECIMAL 数据类型，避免浮点数精度丢失问题。金额精度统一为 DECIMAL(10,2)，即最多 10 位数字，其中 2 位为小数。财务相关字段采用 DECIMAL(12,2)，支持更大的金额范围。

### 4.4 状态字段规范

状态字段统一采用 TINYINT(1) 类型，通过数值编码表示不同的状态。具体的状态含义在各个表中单独定义。

## 五、索引设计说明

### 5.1 主键索引

每个表的主键字段自动建立主键索引，确保数据唯一性和记录定位的高效性。

### 5.2 唯一索引

对于需要保证数据唯一性的字段，建立唯一索引。包括 users 表的 username、mobile、email；orders 表的 order_no；product_skus 表的 sku_code；brands 表的 name 等。

### 5.3 普通索引

对于查询频率较高的筛选字段，建立普通索引。包括 user_id、merchant_id、product_id、order_id 等外键字段；status、created_at 等常用查询条件字段。

### 5.4 复合索引

对于多条件组合查询的场景，考虑建立复合索引以提升查询性能。

## 六、表关系说明

### 6.1 用户相关表关系

users 表与 user_addresses 表为一对多关系，一个用户可以拥有多个收货地址。users 表与 cart_items 表为一对多关系。users 表与 orders 表为一对多关系。users 表与 favorites 表为一对多关系。users 表与 user_coupons 表为一对多关系。

### 6.2 商品相关表关系

categories 表为自关联表，通过 parent_id 实现多级分类。products 表与 categories 表为多对一关系。products 表与 brands 表为多对一关系。products 表与 merchants 表为多对一关系。products 表与 product_skus 表为一对多关系。product_specs 表与 products 表为多对一关系。product_spec_values 表与 product_specs 表为多对一关系。

### 6.3 订单相关表关系

orders 表与 users 表为多对一关系。orders 表与 merchants 表为多对一关系。order_items 表与 orders 表为多对一关系。order_items 表与 products 表为多对一关系。order_logistics 表与 orders 表为一对一关系。order_refunds 表与 orders 表为多对一关系。invoices 表与 orders 表为多对一关系。

### 6.4 商家相关表关系

merchants 表与 users 表为一对一关系。merchant_applications 表与 users 表为多对一关系。

### 6.5 评价相关表关系

product_reviews 表与 products 表为多对一关系。product_reviews 表与 users 表为多对一关系。review_replies 表与 product_reviews 表为多对一关系。

## 七、初始化数据

项目初始化时将插入以下基础数据：超级管理员账户，用户名 admin，密码 admin123；系统角色，包括超级管理员、运营人员、财务人员；基础商品分类，包括数码电子、服饰服装、家居生活；基础品牌信息，包括 Apple、Nike、Adidas、戴森、小米、华为、三星、索尼等。

## 八、使用说明

本数据库表结构设计遵循电商平台的最佳实践，支持完整的电商业务流程。开发人员在使用时应注意以下几点：所有涉及金额的计算应在服务端完成，数据库仅做存储；订单创建时应将商品信息快照到 order_items 表，避免后续商品信息变更影响历史订单；敏感信息如密码应加密存储，身份证号等敏感字段应脱敏处理；涉及财务的业务操作应添加事务控制确保数据一致性。

## 九、API 文档

### 9.1 Smart-doc API 文档

本项目使用 **smart-doc** 生成 API 接口文档，基于 JavaDoc 注释自动生成，无需额外注解。

#### 生成文档

```bash
# 在项目根目录执行
mvn clean compile
```

编译完成后，API 文档会自动生成到 `src/main/resources/static/doc` 目录。

#### 查看文档

启动应用后，在浏览器中访问：

```
http://localhost:8080/doc/index.html
```

#### 文档特性

- ✅ 基于 JavaDoc 注释，代码即文档
- ✅ 支持在线调试功能
- ✅ 自动生成请求/响应示例
- ✅ 支持参数说明和返回值说明
- ✅ 美观的 UI 界面

#### 编写规范

为了让 smart-doc 生成更好的文档，请遵循以下规范：

1. **Controller 类注释**：
```java
/**
 * 商品控制器
 */
@RestController
@RequestMapping("/products")
public class ProductController {
```

2. **方法注释**：
```java
/**
 * 分页查询商品
 *
 * @param queryDTO 查询条件
 * @return 商品分页结果
 */
@GetMapping
public ResponseResult<Page<ProductVO>> getProducts(ProductQueryDTO queryDTO) {
```

3. **DTO/VO 字段注释**：
```java
@Data
public class ProductVO {
    /**
     * 商品ID
     */
    private Long id;
    
    /**
     * 商品名称
     */
    private String name;
}
```
