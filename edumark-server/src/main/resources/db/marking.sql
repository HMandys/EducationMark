-- =============================================
-- 阅卷模块数据库脚本
-- =============================================

-- 阅卷任务表
CREATE TABLE IF NOT EXISTS `marking_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `exam_subject_id` BIGINT NOT NULL COMMENT '考试科目ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `name` VARCHAR(100) DEFAULT NULL COMMENT '任务名称',
    `task_type` TINYINT NOT NULL DEFAULT 2 COMMENT '任务类型: 1-客观题 2-主观题',
    `total_count` INT NOT NULL DEFAULT 0 COMMENT '总份数',
    `completed_count` INT NOT NULL DEFAULT 0 COMMENT '已完成份数',
    `pending_count` INT NOT NULL DEFAULT 0 COMMENT '待阅份数',
    `enable_double_marking` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用双评: 0-否 1-是',
    `double_marking_threshold` INT DEFAULT NULL COMMENT '双评阈值',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始 1-进行中 2-已完成',
    `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_exam_subject_id` (`exam_subject_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅卷任务表';

-- 阅卷任务分配表
CREATE TABLE IF NOT EXISTS `marking_task_assign` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `assign_count` INT NOT NULL DEFAULT 0 COMMENT '分配份数',
    `completed_count` INT NOT NULL DEFAULT 0 COMMENT '已完成份数',
    `marking_role` TINYINT NOT NULL DEFAULT 1 COMMENT '评阅角色: 1-一评 2-二评 3-仲裁',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未开始 1-进行中 2-已完成',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    UNIQUE KEY `uk_task_teacher_role` (`task_id`, `teacher_id`, `marking_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅卷任务分配表';

-- 阅卷记录表
CREATE TABLE IF NOT EXISTS `marking_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `answer_sheet_id` BIGINT NOT NULL COMMENT '答题卡ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `student_id` BIGINT DEFAULT NULL COMMENT '学生ID',
    `teacher_id` BIGINT NOT NULL COMMENT '阅卷教师ID',
    `marking_role` TINYINT NOT NULL DEFAULT 1 COMMENT '评阅角色: 1-一评 2-二评 3-仲裁',
    `score` INT DEFAULT NULL COMMENT '得分',
    `full_score` INT DEFAULT NULL COMMENT '满分',
    `comment` VARCHAR(500) DEFAULT NULL COMMENT '评语',
    `marking_time` DATETIME DEFAULT NULL COMMENT '阅卷时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待评 1-已评 2-待仲裁 3-仲裁完成',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_answer_sheet_id` (`answer_sheet_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅卷记录表';

-- 阅卷仲裁表
CREATE TABLE IF NOT EXISTS `marking_arbitration` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `answer_sheet_id` BIGINT NOT NULL COMMENT '答题卡ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `student_id` BIGINT DEFAULT NULL COMMENT '学生ID',
    `first_marking_id` BIGINT NOT NULL COMMENT '一评记录ID',
    `first_score` INT NOT NULL COMMENT '一评分数',
    `first_teacher_id` BIGINT NOT NULL COMMENT '一评教师ID',
    `second_marking_id` BIGINT NOT NULL COMMENT '二评记录ID',
    `second_score` INT NOT NULL COMMENT '二评分数',
    `second_teacher_id` BIGINT NOT NULL COMMENT '二评教师ID',
    `score_diff` INT NOT NULL COMMENT '分差',
    `arbitration_teacher_id` BIGINT DEFAULT NULL COMMENT '仲裁教师ID',
    `arbitration_score` INT DEFAULT NULL COMMENT '仲裁分数',
    `arbitration_time` DATETIME DEFAULT NULL COMMENT '仲裁时间',
    `arbitration_comment` VARCHAR(500) DEFAULT NULL COMMENT '仲裁说明',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待仲裁 1-已仲裁',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_answer_sheet_id` (`answer_sheet_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅卷仲裁表';

-- 答题卡明细表
CREATE TABLE IF NOT EXISTS `answer_sheet_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `answer_sheet_id` BIGINT NOT NULL COMMENT '答题卡ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `student_answer` TEXT DEFAULT NULL COMMENT '学生答案',
    `score` INT DEFAULT NULL COMMENT '得分',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待阅，1-已阅',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `idx_answer_sheet_id` (`answer_sheet_id`),
    KEY `idx_question_id` (`question_id`),
    UNIQUE KEY `uk_sheet_question` (`answer_sheet_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='答题卡明细表';
