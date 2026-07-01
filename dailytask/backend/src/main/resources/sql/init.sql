-- ============================================================
-- 花之恋鲜花电商平台 - 数据库初始化脚本
-- 数据库：ecommerce
-- 字符集：utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS ecommerce DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE ecommerce;

-- -----------------------------------------------------------
-- 1. 用户表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`          INT PRIMARY KEY AUTO_INCREMENT  COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL UNIQUE     COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL            COMMENT '密码(MD5加密)',
    `phone`       VARCHAR(20)  DEFAULT NULL        COMMENT '手机号',
    `role`        INT          DEFAULT 0           COMMENT '角色：0普通用户，1管理员',
    `status`      INT          DEFAULT 1           COMMENT '状态：0禁用，1正常',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 商品分类表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id`          INT PRIMARY KEY AUTO_INCREMENT  COMMENT '分类ID',
    `name`        VARCHAR(50)  NOT NULL            COMMENT '分类名称',
    `sort_order`  INT          DEFAULT 0           COMMENT '排序号，越小越靠前',
    `status`      INT          DEFAULT 1           COMMENT '状态：1启用，0禁用',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- -----------------------------------------------------------
-- 3. 商品表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id`             INT PRIMARY KEY AUTO_INCREMENT  COMMENT '商品ID',
    `name`           VARCHAR(200) NOT NULL            COMMENT '商品名称',
    `price`          DECIMAL(10,2) NOT NULL           COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL       COMMENT '原价',
    `description`    TEXT         DEFAULT NULL         COMMENT '商品描述',
    `category`       VARCHAR(50)  DEFAULT NULL         COMMENT '分类：鲜花/永生花/花束/花篮/绿植/礼品花',
    `image`          VARCHAR(500) DEFAULT NULL         COMMENT '商品图片URL',
    `stock`          INT          DEFAULT 0            COMMENT '库存',
    `sales`          INT          DEFAULT 0            COMMENT '销量',
    `status`         INT          DEFAULT 1            COMMENT '状态：0下架，1上架',
    `create_time`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 给商品表添加全文索引（支持中文分词搜索）
ALTER TABLE `product` ADD FULLTEXT INDEX `ft_name_desc` (`name`, `description`) WITH PARSER ngram;

-- -----------------------------------------------------------
-- 4. 订单表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id`               INT PRIMARY KEY AUTO_INCREMENT  COMMENT '订单ID',
    `order_no`         VARCHAR(50)  NOT NULL UNIQUE     COMMENT '订单号',
    `user_id`          INT          NOT NULL            COMMENT '用户ID',
    `product_id`       INT          NOT NULL            COMMENT '商品ID',
    `product_name`     VARCHAR(200) DEFAULT NULL        COMMENT '商品名称(冗余)',
    `product_image`    VARCHAR(500) DEFAULT NULL        COMMENT '商品图片(冗余)',
    `product_price`    DECIMAL(10,2) DEFAULT NULL       COMMENT '商品单价(冗余)',
    `quantity`         INT          NOT NULL            COMMENT '购买数量',
    `total_price`      DECIMAL(10,2) NOT NULL           COMMENT '订单总价',
    `receiver_name`    VARCHAR(50)  DEFAULT NULL         COMMENT '收货人姓名',
    `receiver_phone`   VARCHAR(20)  DEFAULT NULL         COMMENT '收货人手机号',
    `receiver_address` VARCHAR(300) DEFAULT NULL         COMMENT '收货地址',
    `status`           INT          DEFAULT 0            COMMENT '订单状态：0待付款 1已付款 2已发货 3已完成 4已取消',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- -----------------------------------------------------------
-- 5. 购物车表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id`          INT PRIMARY KEY AUTO_INCREMENT  COMMENT '购物车ID',
    `user_id`     INT      NOT NULL               COMMENT '用户ID',
    `product_id`  INT      NOT NULL               COMMENT '商品ID',
    `quantity`    INT      DEFAULT 1              COMMENT '购买数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- -----------------------------------------------------------
-- 6. 商品评价表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
    `id`          INT PRIMARY KEY AUTO_INCREMENT  COMMENT '评价ID',
    `user_id`     INT          NOT NULL            COMMENT '用户ID',
    `product_id`  INT          NOT NULL            COMMENT '商品ID',
    `username`    VARCHAR(50)  DEFAULT NULL         COMMENT '用户名(冗余)',
    `content`     TEXT         NOT NULL             COMMENT '评价内容',
    `location`    VARCHAR(100) DEFAULT NULL         COMMENT '地区',
    `image`       VARCHAR(500) DEFAULT NULL         COMMENT '晒单图片URL',
    `rating`      INT          DEFAULT 5            COMMENT '评分(1-5)',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- ============================================================
-- 测试数据
-- ============================================================

-- 管理员账号：admin / 123456
-- 普通用户：   test  / 123456
INSERT INTO `user` (`username`, `password`, `phone`, `role`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '13800000000', 1, 1),
('test',  'e10adc3949ba59abbe56e057f20f883e', '13912345678', 0, 1),
('lily',  'e10adc3949ba59abbe56e057f20f883e', '13666666666', 0, 1);

-- 商品分类
INSERT INTO `category` (`name`, `sort_order`, `status`) VALUES
('鲜花',   1, 1),
('永生花', 2, 1),
('花束',   3, 1),
('花篮',   4, 1),
('绿植',   5, 1),
('礼品花', 6, 1);

-- 商品数据（10个鲜花商品）
INSERT INTO `product` (`name`, `price`, `original_price`, `description`, `category`, `image`, `stock`, `sales`, `status`) VALUES
('红玫瑰花束 - 热恋',       199.00, 299.00, '33朵红玫瑰，满天星搭配，韩式花束包装，适合表白、纪念日',     '花束', '/images/rose-red.jpg',       100, 520, 1),
('粉百合花篮 - 温馨祝福',   268.00, 368.00, '6朵粉百合配绿叶，精致花篮，适合探望、祝福',               '花篮', '/images/lily-pink.jpg',      80,  310, 1),
('向日葵花束 - 阳光灿烂',   158.00, 228.00, '6朵向日葵搭配尤加利叶，活力四射，适合生日、毕业',          '花束', '/images/sunflower.jpg',      120, 680, 1),
('永生玫瑰礼盒 - 永恒之爱', 399.00, 599.00, '进口永生玫瑰，高档礼盒装，不凋谢的浪漫，适合情人节',       '永生花', '/images/preserved-rose.jpg', 50,  200, 1),
('混合花束 - 缤纷时光',     188.00, 258.00, '玫瑰+百合+康乃馨混搭，色彩丰富，适合各种场合',            '花束', '/images/mixed-bouquet.jpg',  90,  450, 1),
('绿萝盆栽 - 清新空气',      39.00,  59.00, '大叶绿萝，好养易活，净化空气，适合办公室、客厅',           '绿植', '/images/green萝.jpg',        200, 1200, 1),
('康乃馨花束 - 感恩有你',   168.00, 238.00, '19朵粉色康乃馨，温馨花束，适合母亲节、感恩节',            '花束', '/images/carnation.jpg',      110, 560, 1),
('红掌花篮 - 鸿运当头',     288.00, 388.00, '3盆红掌搭配绿植，高档花篮，适合开业、乔迁',               '花篮', '/images/anthurium.jpg',      60,  150, 1),
('薰衣草干花 - 浪漫普罗旺斯', 88.00, 128.00, '天然薰衣草干花束，持久留香，适合家居装饰',                '礼品花', '/images/lavender.jpg',     150, 890, 1),
('多肉植物礼盒 - 萌趣组合',  128.00, 188.00, '6种精选多肉，精美礼盒装，适合送朋友、同事',               '礼品花', '/images/succulent.jpg',    130, 720, 1);

-- 示例评价
INSERT INTO `review` (`user_id`, `product_id`, `username`, `content`, `location`, `rating`) VALUES
(2, 1, 'test',  '花束非常漂亮，女朋友很喜欢！配送也很快，当天就到了。',     '北京朝阳', 5),
(3, 1, 'lily',  '玫瑰很新鲜，包装精美，性价比很高，下次还会回购。',        '上海浦东', 5),
(2, 3, 'test',  '向日葵开得正好，颜色鲜艳，送给朋友的生日礼物，很满意。',   '广州天河', 4),
(3, 4, 'lily',  '永生花做工精致，礼盒很高档，送人很有面子。',              '深圳南山', 5),
(2, 5, 'test',  '混搭花束颜色搭配很好看，就是花期稍微短了点。',            '杭州西湖', 4),
(3, 6, 'lily',  '绿萝养了一个月了，长势很好，放在办公桌上很清新。',         '成都锦江', 5),
(2, 7, 'test',  '母亲节送给妈妈的，妈妈很喜欢，花很新鲜很漂亮。',          '武汉洪山', 5),
(3, 9, 'lily',  '薰衣草干花味道很好闻，放在卧室里很舒服，推荐！',          '南京鼓楼', 5);

-- 示例订单
INSERT INTO `orders` (`order_no`, `user_id`, `product_id`, `product_name`, `product_image`, `product_price`, `quantity`, `total_price`, `receiver_name`, `receiver_phone`, `receiver_address`, `status`) VALUES
('ORD20260628001', 2, 1, '红玫瑰花束 - 热恋',   '/images/rose-red.jpg',  199.00, 1, 199.00, '张三', '13912345678', '北京市朝阳区xx路1号',  3),
('ORD20260628002', 2, 3, '向日葵花束 - 阳光灿烂', '/images/sunflower.jpg', 158.00, 2, 316.00, '张三', '13912345678', '北京市朝阳区xx路1号',  1),
('ORD20260629001', 3, 7, '康乃馨花束 - 感恩有你', '/images/carnation.jpg', 168.00, 1, 168.00, '李四', '13666666666', '上海市浦东新区yy路2号', 0),
('ORD20260630001', 2, 4, '永生玫瑰礼盒 - 永恒之爱', '/images/preserved-rose.jpg', 399.00, 1, 399.00, '张三', '13912345678', '北京市朝阳区xx路1号', 2);
