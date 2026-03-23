-- =============================================
-- 阅卷码功能补丁脚本
-- =============================================

-- 1. MarkingTask 添加阅卷码字段
ALTER TABLE marking_task
ADD COLUMN access_code VARCHAR(8) COMMENT '阅卷码(8位数字)' AFTER status,
ADD COLUMN access_code_expire_time DATETIME COMMENT '阅卷码过期时间' AFTER access_code;

-- 2. 添加阅卷会话表（记录免登录会话）
CREATE TABLE IF NOT EXISTS `marking_session` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '阅卷任务ID',
    `access_code` VARCHAR(8) NOT NULL COMMENT '使用的阅卷码',
    `session_token` VARCHAR(64) NOT NULL COMMENT '会话令牌',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_session_token` (`session_token`),
    INDEX `idx_task_access` (`task_id`, `access_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅卷会话表';

-- 3. MarkingRecord 添加问题卷标记字段
ALTER TABLE marking_record
ADD COLUMN problem_flag TINYINT DEFAULT 0 COMMENT '问题卷标记: 0-正常 1-问题卷' AFTER status,
ADD COLUMN problem_reason VARCHAR(500) COMMENT '问题卷原因' AFTER problem_flag;
