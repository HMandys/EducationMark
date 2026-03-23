-- 阅卷记录跳过功能补丁
-- 添加 skip_count 字段，防止跳过的记录永远不被评阅

ALTER TABLE `marking_record`
ADD COLUMN `skip_count` INT NOT NULL DEFAULT 0 COMMENT '跳过次数' AFTER `status`,
ADD KEY `idx_skip_count` (`skip_count`);
