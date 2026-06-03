-- ============================================
-- 插入20个测试用户数据
-- 包含两个用户表：users (shopping-server) 和 sys_user (auth-server)
-- 密码统一为: 123456 (已使用BCrypt加密)
-- BCrypt哈希值: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
-- ============================================

-- ============================================
-- 1. shopping-server 的 users 表 - 20条记录
-- ============================================
INSERT INTO users (id, username, password, nickname, avatar, mobile, email, gender, birthday, status, user_level, points, balance, created_at, updated_at, is_deleted)
VALUES
    -- 用户 6-25 (因为已有5个用户)
    ('6', 'user006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘芳', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/common.jpg', '13800138006', 'liufang@example.com', 2, '1993-05-12', 1, 'normal', 0, 1500.00, '2025-03-01 10:00:00', '2025-03-01 10:00:00', 0),
    ('7', 'user007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陈伟', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/4676.jpg_wh860%20%281%29.jpg', '13800138007', 'chenwei@example.com', 1, '1991-08-20', 1, 'normal', 0, 2800.00, '2025-03-02 11:00:00', '2025-03-02 11:00:00', 0),
    ('8', 'user008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '杨丽', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_2025-12-04_224407_588.jpg', '13800138008', 'yangli@example.com', 2, '1996-03-15', 1, 'normal', 0, 3200.00, '2025-03-03 14:00:00', '2025-03-03 14:00:00', 0),
    ('9', 'user009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '黄强', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202025-10-23%20234242.png', '13800138009', 'huangqiang@example.com', 1, '1989-11-08', 1, 'normal', 0, 4500.00, '2025-03-04 09:00:00', '2025-03-04 09:00:00', 0),
    ('10',  'user010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周敏', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/common.jpg', '13800138010', 'zhoumin@example.com', 2, '1994-07-25', 1, 'normal', 0, 1800.00, '2025-03-05 16:00:00', '2025-03-05 16:00:00', 0),
    ('11',  'user011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '吴涛', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/4676.jpg_wh860%20%281%29.jpg', '13800138011', 'wutao@example.com', 1, '1992-01-30', 1, 'normal', 0, 2200.00, '2025-03-06 10:00:00', '2025-03-06 10:00:00', 0),
    ('12',  'user012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '郑霞', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_2025-12-04_224407_588.jpg', '13800138012', 'zhengxia@example.com', 2, '1997-09-18', 1, 'normal', 0, 3600.00, '2025-03-07 13:00:00', '2025-03-07 13:00:00', 0),
    ('13',  'user013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王磊', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202025-10-23%20234242.png', '13800138013', 'wanglei@example.com', 1, '1990-04-22', 1, 'normal', 0, 5000.00, '2025-03-08 15:00:00', '2025-03-08 15:00:00', 0),
    ('14',  'user014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '冯静', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/common.jpg', '13800138014', 'fengjing@example.com', 2, '1995-12-05', 1, 'normal', 0, 2700.00, '2025-03-09 11:00:00', '2025-03-09 11:00:00', 0),
    ('15',  'user015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '褚勇', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/4676.jpg_wh860%20%281%29.jpg', '13800138015', 'chuyong@example.com', 1, '1988-06-14', 1, 'normal', 0, 4200.00, '2025-03-10 09:00:00', '2025-03-10 09:00:00', 0),
    ('16',  'user016', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '卫娜', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_2025-12-04_224407_588.jpg', '13800138016', 'weina@example.com', 2, '1993-10-28', 1, 'normal', 0, 1900.00, '2025-03-11 14:00:00', '2025-03-11 14:00:00', 0),
    ('17',  'user017', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '蒋超', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202025-10-23%20234242.png', '13800138017', 'jiangchao@example.com', 1, '1991-02-16', 1, 'normal', 0, 3100.00, '2025-03-12 10:00:00', '2025-03-12 10:00:00', 0),
    ('18',  'user018', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '沈艳', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/common.jpg', '13800138018', 'shenyan@example.com', 2, '1996-08-09', 1, 'normal', 0, 2500.00, '2025-03-13 16:00:00', '2025-03-13 16:00:00', 0),
    ('19',  'user019', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '韩军', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/4676.jpg_wh860%20%281%29.jpg', '13800138019', 'hanjun@example.com', 1, '1989-05-03', 1, 'normal', 0, 4800.00, '2025-03-14 12:00:00', '2025-03-14 12:00:00', 0),
    ('20',  'user020', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '杨秀英', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_2025-12-04_224407_588.jpg', '13800138020', 'yangxiuying@example.com', 2, '1994-11-21', 1, 'normal', 0, 3300.00, '2025-03-15 09:00:00', '2025-03-15 09:00:00', 0),
    ('21',  'user021', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '朱建国', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202025-10-23%20234242.png', '13800138021', 'zhujianguo@example.com', 1, '1992-07-17', 1, 'normal', 0, 2100.00, '2025-03-16 14:00:00', '2025-03-16 14:00:00', 0),
    ('22',  'user022', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '秦丽华', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/common.jpg', '13800138022', 'qinlihua@example.com', 2, '1997-03-26', 1, 'normal', 0, 3800.00, '2025-03-17 11:00:00', '2025-03-17 11:00:00', 0),
    ('23',  'user023', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '尤志强', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/4676.jpg_wh860%20%281%29.jpg', '13800138023', 'youzhiqiang@example.com', 1, '1990-09-11', 1, 'normal', 0, 4600.00, '2025-03-18 15:00:00', '2025-03-18 15:00:00', 0),
    ('24',  'user024', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '许美玲', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_2025-12-04_224407_588.jpg', '13800138024', 'xumeiling@example.com', 2, '1995-01-07', 1, 'normal', 0, 2900.00, '2025-03-19 10:00:00', '2025-03-19 10:00:00', 0),
    ('25',  'user025', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '何建华', 'https://zym-qlbm-2005.oss-cn-guangzhou.aliyuncs.com/%E5%B1%8F%E5%B9%95%E6%88%AA%E5%9B%BE%202025-10-23%20234242.png', '13800138025', 'hejianhua@example.com', 1, '1988-12-19', 1, 'normal', 0, 5200.00, '2025-03-20 13:00:00', '2025-03-20 13:00:00', 0);

-- ====='=, =====================================
-- 2. auth-server 的 sys_user 表 - 20条记录
-- 注意：sys_user 表的 id 是 String 类型，需要与 users 表的 ID 保持一致
-- ============================================
INSERT INTO sys_user (id, username, email, password, status, create_time, update_time)
VALUES
    ('6', 'user006', 'liufang@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-01 10:00:00', '2025-03-01 10:00:00'),
    ('7', 'user007', 'chenwei@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-02 11:00:00', '2025-03-02 11:00:00'),
    ('8', 'user008', 'yangli@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-03 14:00:00', '2025-03-03 14:00:00'),
    ('9', 'user009', 'huangqiang@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-04 09:00:00', '2025-03-04 09:00:00'),
    ('10', 'user010', 'zhoumin@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-05 16:00:00', '2025-03-05 16:00:00'),
    ('11', 'user011', 'wutao@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-06 10:00:00', '2025-03-06 10:00:00'),
    ('12', 'user012', 'zhengxia@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-07 13:00:00', '2025-03-07 13:00:00'),
    ('13', 'user013', 'wanglei@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-08 15:00:00', '2025-03-08 15:00:00'),
    ('14', 'user014', 'fengjing@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-09 11:00:00', '2025-03-09 11:00:00'),
    ('15', 'user015', 'chuyong@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-10 09:00:00', '2025-03-10 09:00:00'),
    ('16', 'user016', 'weina@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-11 14:00:00', '2025-03-11 14:00:00'),
    ('17', 'user017', 'jiangchao@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-12 10:00:00', '2025-03-12 10:00:00'),
    ('18', 'user018', 'shenyan@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-13 16:00:00', '2025-03-13 16:00:00'),
    ('19', 'user019', 'hanjun@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-14 12:00:00', '2025-03-14 12:00:00'),
    ('20', 'user020', 'yangxiuying@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-15 09:00:00', '2025-03-15 09:00:00'),
    ('21', 'user021', 'zhujianguo@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-16 14:00:00', '2025-03-16 14:00:00'),
    ('22', 'user022', 'qinlihua@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-17 11:00:00', '2025-03-17 11:00:00'),
    ('23', 'user023', 'youzhiqiang@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-18 15:00:00', '2025-03-18 15:00:00'),
    ('24', 'user024', 'xumeiling@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-19 10:00:00', '2025-03-19 10:00:00'),
    ('25', 'user025', 'hejianhua@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1, '2025-03-20 13:00:00', '2025-03-20 13:00:00');

-- ============================================
-- 数据说明：
-- 1. 用户名：user006 - user025（共20个用户）
-- 2. 密码：统一为 123456，已使用BCrypt加密
-- 3. BCrypt哈希值：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
-- 4. 头像：复用数据库中已有的图片URL
-- 5. 手机号：13800138006 - 13800138025
-- 6. 邮箱：用户名@example.com
-- 7. 性别：男女交替分配
-- 8. 生日：1988-1997年之间随机分布
-- 9. 余额：1500-5200元之间随机分配
-- 10. 状态：全部为正常状态(status=1)
-- 
-- 注意事项：
-- - shopping-server 的 users 表 ID 从 6 开始（因为已有5个用户）
-- - auth-server 的 sys_user 表 ID 与 users 表保持一致（字符串类型）
-- - 两个表的用户名和邮箱保持一致，方便后续关联
-- ============================================
