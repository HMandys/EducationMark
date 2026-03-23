-- 阅卷码唯一索引补丁
-- 防止并发时生成重复阅卷码

-- 先清理可能的重复数据（保留最新的）
-- 如果存在重复，需要手动处理

-- 添加唯一索引
ALTER TABLE `marking_task`
ADD UNIQUE KEY `uk_access_code` (`access_code`),
ADD UNIQUE KEY `uk_second_access_code` (`second_access_code`);
