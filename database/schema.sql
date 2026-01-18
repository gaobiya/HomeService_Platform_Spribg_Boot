-- 家政服务预约与派单平台数据库表结构
-- 数据库名称: homeservice_db
-- MySQL版本: 8.0+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS homeservice_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE homeservice_db;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户昵称',
    `role` ENUM('customer', 'worker', 'dispatcher') NOT NULL COMMENT '用户角色：customer-客户, worker-服务员, dispatcher-派单员',
    `password` VARCHAR(128) NULL COMMENT '密码（派单员使用）',
    `avatar_url` VARCHAR(255) NULL COMMENT '用户头像路径',
    `phone` VARCHAR(20) NULL COMMENT '联系电话',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_role` (`role`),
    INDEX `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 2. 服务订单表
CREATE TABLE IF NOT EXISTS `service_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `customer_id` BIGINT NOT NULL COMMENT '客户ID',
    `worker_id` BIGINT NULL COMMENT '服务员ID（派单后才有）',
    `service_type` VARCHAR(50) NOT NULL COMMENT '服务类型（如：cleaning-保洁, repair-维修等）',
    `address` VARCHAR(255) NOT NULL COMMENT '服务地址',
    `description` TEXT NULL COMMENT '服务说明',
    `service_time` DATETIME NOT NULL COMMENT '服务预约时间',
    `status` ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待审核, IN_PROGRESS-进行中, COMPLETED-已完成, CANCELLED-已取消',
    `assigned_time` DATETIME NULL COMMENT '派单时间',
    `amount` DECIMAL(10, 2) NULL DEFAULT 0.00 COMMENT '订单金额',
    `paid` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已支付：0-未支付, 1-已支付',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_customer_id` (`customer_id`),
    INDEX `idx_worker_id` (`worker_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_service_time` (`service_time`),
    FOREIGN KEY (`customer_id`) REFERENCES `user_info` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (`worker_id`) REFERENCES `user_info` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务订单表';

-- 3. 服务员日程表
CREATE TABLE IF NOT EXISTS `worker_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日程ID',
    `worker_id` BIGINT NOT NULL COMMENT '服务员ID',
    `start_time` DATETIME NOT NULL COMMENT '可服务开始时间',
    `end_time` DATETIME NOT NULL COMMENT '可服务结束时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_worker_id` (`worker_id`),
    INDEX `idx_time_range` (`start_time`, `end_time`),
    FOREIGN KEY (`worker_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务员日程表';

-- 4. 订单评价表
CREATE TABLE IF NOT EXISTS `order_rating` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `order_id` BIGINT NOT NULL COMMENT '对应订单ID',
    `rater_id` BIGINT NOT NULL COMMENT '评价人ID（客户或服务员）',
    `ratee_id` BIGINT NOT NULL COMMENT '被评价人ID',
    `rating` TINYINT NOT NULL COMMENT '评分 1~5',
    `comment` TEXT NULL COMMENT '评价内容',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_rater_id` (`rater_id`),
    INDEX `idx_ratee_id` (`ratee_id`),
    FOREIGN KEY (`order_id`) REFERENCES `service_order` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`rater_id`) REFERENCES `user_info` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (`ratee_id`) REFERENCES `user_info` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    UNIQUE KEY `uk_order_rater` (`order_id`, `rater_id`) COMMENT '同一订单同一评价人只能评价一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单评价表';

-- 5. 文件表
CREATE TABLE IF NOT EXISTS `file_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `file_type` ENUM('avatar', 'qualification') NOT NULL COMMENT '文件类型：avatar-头像, qualification-资质',
    `file_path` VARCHAR(255) NOT NULL COMMENT '文件存储路径',
    `file_name` VARCHAR(255) NULL COMMENT '原始文件名',
    `file_size` BIGINT NULL COMMENT '文件大小（字节）',
    `uploaded_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_file_type` (`file_type`),
    FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 插入初始测试数据（可选）
-- 插入一个派单员账号（用于后台登录）
-- 密码：admin123（明文，仅用于演示）
INSERT INTO `user_info` (`username`, `role`, `password`, `phone`) 
VALUES ('admin', 'dispatcher', 'admin123', '13800138000') 
ON DUPLICATE KEY UPDATE `password`='admin123';
