-- 创建服务员服务类型表
-- 用于记录服务员可提供的服务类型

USE homeservice_db;

CREATE TABLE IF NOT EXISTS `worker_service_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `worker_id` BIGINT NOT NULL COMMENT '服务员ID',
    `service_type` VARCHAR(50) NOT NULL COMMENT '服务类型（cleaning-保洁, repair-维修, cooking-做饭, babysitting-育儿）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_worker_service` (`worker_id`, `service_type`) COMMENT '同一服务员同一服务类型只能设置一次',
    INDEX `idx_worker_id` (`worker_id`),
    INDEX `idx_service_type` (`service_type`),
    FOREIGN KEY (`worker_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务员服务类型表';
