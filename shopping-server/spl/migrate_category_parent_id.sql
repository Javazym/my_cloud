-- ========================================
-- 分类表 parent_id 字段迁移脚本
-- 将 parent_id = 0 的记录改为 NULL
-- ========================================

-- 1. 修改表结构，允许 parent_id 为 NULL
ALTER TABLE `categories` 
MODIFY COLUMN `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父分类ID: NULL-顶级分类';

-- 2. 将所有 parent_id = 0 的记录更新为 NULL
UPDATE `categories` 
SET `parent_id` = NULL 
WHERE `parent_id` = 0;

-- 3. 验证结果
SELECT 
    id, 
    name, 
    parent_id, 
    level,
    CASE 
        WHEN parent_id IS NULL THEN '顶级分类'
        ELSE '子分类'
    END AS category_type
FROM `categories`
ORDER BY level, sort;

-- ========================================
-- 说明：
-- 1. 此脚本用于将现有的 parent_id=0 的顶级分类改为 parent_id=NULL
-- 2. 这样可以避免 JPA 外键约束错误
-- 3. 执行前请备份数据
-- ========================================
