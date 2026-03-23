-- =============================================
-- 双评阅卷码补丁脚本
-- =============================================

-- 1. 阅卷任务补充二评阅卷码
ALTER TABLE marking_task
ADD COLUMN second_access_code VARCHAR(8) NULL COMMENT '二评阅卷码(8位数字)' AFTER access_code;

-- 2. 阅卷会话补充评阅角色
ALTER TABLE marking_session
ADD COLUMN marking_role TINYINT NOT NULL DEFAULT 1 COMMENT '评阅角色: 1-一评 2-二评' AFTER access_code;

-- 3. 补充会话索引
ALTER TABLE marking_session
ADD INDEX idx_task_role (task_id, marking_role);
