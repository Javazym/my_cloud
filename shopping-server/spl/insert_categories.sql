-- ========================================
-- 商品分类数据插入脚本
-- 共30组分类数据(包含一级、二级、三级分类)
-- ========================================

-- 清空现有数据(可选)
-- TRUNCATE TABLE categories;

-- ========================================
-- 一级分类 (level=1, parent_id=NULL)
-- ========================================

-- 1. 数码电子
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '数码电子', 1, '/icons/digital.png', '/images/categories/digital.jpg', 1, 1, NOW(), NOW(), 0);

-- 2. 服饰服装
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '服饰服装', 1, '/icons/clothing.png', '/images/categories/clothing.jpg', 2, 1, NOW(), NOW(), 0);

-- 3. 家居生活
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '家居生活', 1, '/icons/home.png', '/images/categories/home.jpg', 3, 1, NOW(), NOW(), 0);

-- 4. 美妆护肤
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '美妆护肤', 1, '/icons/beauty.png', '/images/categories/beauty.jpg', 4, 1, NOW(), NOW(), 0);

-- 5. 食品饮料
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '食品饮料', 1, '/icons/food.png', '/images/categories/food.jpg', 5, 1, NOW(), NOW(), 0);

-- 6. 母婴用品
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '母婴用品', 1, '/icons/baby.png', '/images/categories/baby.jpg', 6, 1, NOW(), NOW(), 0);

-- 7. 运动户外
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '运动户外', 1, '/icons/sports.png', '/images/categories/sports.jpg', 7, 1, NOW(), NOW(), 0);

-- 8. 图书音像
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (NULL, '图书音像', 1, '/icons/books.png', '/images/categories/books.jpg', 8, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 数码电子 (parent_id=1)
-- ========================================

-- 9. 手机通讯
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (1, '手机通讯', 2, '/icons/phone.png', '/images/categories/phone.jpg', 1, 1, NOW(), NOW(), 0);

-- 10. 电脑办公
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (1, '电脑办公', 2, '/icons/computer.png', '/images/categories/computer.jpg', 2, 1, NOW(), NOW(), 0);

-- 11. 数码配件
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (1, '数码配件', 2, '/icons/accessories.png', '/images/categories/accessories.jpg', 3, 1, NOW(), NOW(), 0);

-- 12. 智能穿戴
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (1, '智能穿戴', 2, '/icons/wearable.png', '/images/categories/wearable.jpg', 4, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 服饰服装 (parent_id=2)
-- ========================================

-- 13. 男装
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (2, '男装', 2, '/icons/men.png', '/images/categories/men.jpg', 1, 1, NOW(), NOW(), 0);

-- 14. 女装
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (2, '女装', 2, '/icons/women.png', '/images/categories/women.jpg', 2, 1, NOW(), NOW(), 0);

-- 15. 鞋靴箱包
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (2, '鞋靴箱包', 2, '/icons/shoes.png', '/images/categories/shoes.jpg', 3, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 家居生活 (parent_id=3)
-- ========================================

-- 16. 家具家纺
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (3, '家具家纺', 2, '/icons/furniture.png', '/images/categories/furniture.jpg', 1, 1, NOW(), NOW(), 0);

-- 17. 厨具卫浴
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (3, '厨具卫浴', 2, '/icons/kitchen.png', '/images/categories/kitchen.jpg', 2, 1, NOW(), NOW(), 0);

-- 18. 生活用品
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (3, '生活用品', 2, '/icons/daily.png', '/images/categories/daily.jpg', 3, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 美妆护肤 (parent_id=4)
-- ========================================

-- 19. 面部护肤
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (4, '面部护肤', 2, '/icons/skincare.png', '/images/categories/skincare.jpg', 1, 1, NOW(), NOW(), 0);

-- 20. 彩妆香水
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (4, '彩妆香水', 2, '/icons/makeup.png', '/images/categories/makeup.jpg', 2, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 食品饮料 (parent_id=5)
-- ========================================

-- 21. 休闲零食
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (5, '休闲零食', 2, '/icons/snacks.png', '/images/categories/snacks.jpg', 1, 1, NOW(), NOW(), 0);

-- 22. 生鲜水果
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (5, '生鲜水果', 2, '/icons/fresh.png', '/images/categories/fresh.jpg', 2, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 母婴用品 (parent_id=6)
-- ========================================

-- 23. 奶粉辅食
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (6, '奶粉辅食', 2, '/icons/formula.png', '/images/categories/formula.jpg', 1, 1, NOW(), NOW(), 0);

-- 24. 玩具乐器
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (6, '玩具乐器', 2, '/icons/toys.png', '/images/categories/toys.jpg', 2, 1, NOW(), NOW(), 0);

-- ========================================
-- 二级分类 - 运动户外 (parent_id=7)
-- ========================================

-- 25. 运动装备
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (7, '运动装备', 2, '/icons/equipment.png', '/images/categories/equipment.jpg', 1, 1, NOW(), NOW(), 0);

-- 26. 户外用品
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (7, '户外用品', 2, '/icons/outdoor.png', '/images/categories/outdoor.jpg', 2, 1, NOW(), NOW(), 0);

-- ========================================
-- 三级分类 - 手机通讯 (parent_id=9)
-- ========================================

-- 27. 智能手机
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (9, '智能手机', 3, '/icons/smartphone.png', '/images/categories/smartphone.jpg', 1, 1, NOW(), NOW(), 0);

-- 28. 手机配件
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (9, '手机配件', 3, '/icons/phone-case.png', '/images/categories/phone-case.jpg', 2, 1, NOW(), NOW(), 0);

-- ========================================
-- 三级分类 - 电脑办公 (parent_id=10)
-- ========================================

-- 29. 笔记本电脑
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (10, '笔记本电脑', 3, '/icons/laptop.png', '/images/categories/laptop.jpg', 1, 1, NOW(), NOW(), 0);

-- 30. 台式机
INSERT INTO categories (parent_id, name, level, icon, image, sort, status, created_at, updated_at, is_deleted) 
VALUES (10, '台式机', 3, '/icons/desktop.png', '/images/categories/desktop.jpg', 2, 1, NOW(), NOW(), 0);

-- ========================================
-- 数据说明
-- ========================================
-- 一级分类: 8个 (ID: 1-8)
-- 二级分类: 18个 (ID: 9-26)
-- 三级分类: 4个 (ID: 27-30)
-- 总计: 30个分类
--
-- 分类层级结构:
-- 数码电子
--   ├─ 手机通讯
--   │    ├─ 智能手机
--   │    └─ 手机配件
--   ├─ 电脑办公
--   │    ├─ 笔记本电脑
--   │    └─ 台式机
--   ├─ 数码配件
--   └─ 智能穿戴
-- 服饰服装
--   ├─ 男装
--   ├─ 女装
--   └─ 鞋靴箱包
-- 家居生活
--   ├─ 家具家纺
--   ├─ 厨具卫浴
--   └─ 生活用品
-- 美妆护肤
--   ├─ 面部护肤
--   └─ 彩妆香水
-- 食品饮料
--   ├─ 休闲零食
--   └─ 生鲜水果
-- 母婴用品
--   ├─ 奶粉辅食
--   └─ 玩具乐器
-- 运动户外
--   ├─ 运动装备
--   └─ 户外用品
-- 图书音像
-- ========================================
