-- =====================================================
-- 答题卡模块数据库脚本
-- =====================================================

-- 答题卡表
CREATE TABLE IF NOT EXISTS `answer_sheet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `exam_subject_id` BIGINT NOT NULL COMMENT '考试科目ID',
    `student_id` BIGINT DEFAULT NULL COMMENT '学生ID',
    `student_number` VARCHAR(50) DEFAULT NULL COMMENT '学号',
    `seat_number` VARCHAR(20) DEFAULT NULL COMMENT '座位号',
    `image_count` INT NOT NULL DEFAULT 0 COMMENT '图片数量',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待识别 1-已识别 2-待阅卷 3-阅卷中 4-已完成',
    `objective_score` INT DEFAULT NULL COMMENT '客观题得分',
    `subjective_score` INT DEFAULT NULL COMMENT '主观题得分',
    `total_score` INT DEFAULT NULL COMMENT '总分',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_exam_subject_id` (`exam_subject_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_student_number` (`student_number`),
    KEY `idx_status` (`status`),
    UNIQUE KEY `uk_student_subject` (`student_id`, `exam_subject_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='答题卡表';

-- 答题卡图片表
CREATE TABLE IF NOT EXISTS `answer_sheet_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `answer_sheet_id` BIGINT NOT NULL COMMENT '答题卡ID',
    `page_num` INT NOT NULL DEFAULT 1 COMMENT '页码',
    `image_path` VARCHAR(500) NOT NULL COMMENT '图片路径（MinIO对象名）',
    `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
    `original_name` VARCHAR(200) DEFAULT NULL COMMENT '原始文件名',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小',
    `width` INT DEFAULT NULL COMMENT '图片宽度',
    `height` INT DEFAULT NULL COMMENT '图片高度',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_answer_sheet_id` (`answer_sheet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='答题卡图片表';

-- 学生答案表（用于后续阅卷）
CREATE TABLE IF NOT EXISTS `student_answer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `answer_sheet_id` BIGINT NOT NULL COMMENT '答题卡ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `answer_content` TEXT COMMENT '答案内容',
    `answer_image_path` VARCHAR(500) DEFAULT NULL COMMENT '答案图片路径',
    `score` INT DEFAULT NULL COMMENT '得分',
    `is_correct` TINYINT DEFAULT NULL COMMENT '是否正确(客观题): 0-错误 1-正确',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待判分 1-已判分',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_answer_sheet_id` (`answer_sheet_id`),
    KEY `idx_question_id` (`question_id`),
    UNIQUE KEY `uk_answer_question` (`answer_sheet_id`, `question_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生答案表';
