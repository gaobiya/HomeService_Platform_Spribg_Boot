-- 更新admin用户密码为明文（仅用于演示）
-- 执行此脚本更新数据库中的密码

USE homeservice_db;

-- 更新admin用户密码为明文
UPDATE `user_info` 
SET `password` = 'admin123' 
WHERE `username` = 'admin' AND `role` = 'dispatcher';

-- 如果admin用户不存在，则插入
INSERT INTO `user_info` (`username`, `role`, `password`, `phone`) 
VALUES ('admin', 'dispatcher', 'admin123', '13800138000') 
ON DUPLICATE KEY UPDATE `password`='admin123';
