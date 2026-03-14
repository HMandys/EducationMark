-- =====================================================
-- 考试管理模块数据库脚本
-- =====================================================

-- 考试表
CREATE TABLE IF NOT EXISTS `exam` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `name` VARCHAR(100) NOT NULL COMMENT '考试名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '考试编码',
    `type` TINYINT NOT NULL DEFAULT 5 COMMENT '考试类型: 1-期中考试 2-期末考试 3-月考 4-模拟考试 5-其他',
    `academic_year` VARCHAR(20) NOT NULL COMMENT '学年',
    `semester` TINYINT NOT NULL DEFAULT 1 COMMENT '学期: 1-第一学期 2-第二学期',
    `grade_id` BIGINT DEFAULT NULL COMMENT '年级ID',
    `start_time` DATETIME DEFAULT NULL COMMENT '考试开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '考试结束时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '考试状态: 0-草稿 1-待考试 2-考试中 3-阅卷中 4-已完成 5-已发布',
    `total_score` INT DEFAULT 0 COMMENT '总分',
    `student_count` INT DEFAULT 0 COMMENT '参考人数',
    `description` TEXT COMMENT '描述',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_grade_id` (`grade_id`),
    KEY `idx_academic_year` (`academic_year`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考试表';

-- 考试科目表
CREATE TABLE IF NOT EXISTS `exam_subject` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `subject_name` VARCHAR(50) NOT NULL COMMENT '科目名称',
    `subject_code` VARCHAR(20) DEFAULT NULL COMMENT '科目编码',
    `full_score` INT NOT NULL DEFAULT 100 COMMENT '满分',
    `pass_score` INT DEFAULT 60 COMMENT '及格分',
    `excellent_score` INT DEFAULT 85 COMMENT '优秀分',
    `duration` INT DEFAULT 120 COMMENT '考试时长(分钟)',
    `start_time` DATETIME DEFAULT NULL COMMENT '考试开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '考试结束时间',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考试科目表';

-- 考试班级关联表
CREATE TABLE IF NOT EXISTS `exam_class` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exam_id` BIGINT NOT NULL COMMENT '考试ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_class_id` (`class_id`),
    UNIQUE KEY `uk_exam_class` (`exam_id`, `class_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='考试班级关联表';

-- 试卷表
CREATE TABLE IF NOT EXISTS `paper` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `exam_subject_id` BIGINT NOT NULL COMMENT '考试科目ID',
    `name` VARCHAR(100) NOT NULL COMMENT '试卷名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '试卷编码',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '试卷类型: 1-A卷 2-B卷',
    `total_score` INT NOT NULL DEFAULT 0 COMMENT '总分',
    `question_count` INT DEFAULT 0 COMMENT '题目数量',
    `objective_count` INT DEFAULT 0 COMMENT '客观题数量',
    `subjective_count` INT DEFAULT 0 COMMENT '主观题数量',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-已完成',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_exam_subject_id` (`exam_subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='试卷表';

-- 试卷题目表
CREATE TABLE IF NOT EXISTS `paper_question` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `paper_id` BIGINT NOT NULL COMMENT '试卷ID',
    `question_no` VARCHAR(20) NOT NULL COMMENT '题号',
    `section_no` INT DEFAULT 1 COMMENT '大题号',
    `section_name` VARCHAR(50) DEFAULT NULL COMMENT '大题名称',
    `item_no` INT DEFAULT 1 COMMENT '小题号',
    `question_type` TINYINT NOT NULL DEFAULT 9 COMMENT '题目类型: 1-单选 2-多选 3-判断 4-填空 5-简答 6-计算 7-作文 9-其他',
    `is_objective` TINYINT NOT NULL DEFAULT 0 COMMENT '是否客观题: 0-否 1-是',
    `score` INT NOT NULL DEFAULT 0 COMMENT '满分',
    `correct_answer` TEXT COMMENT '正确答案',
    `scoring_criteria` TEXT COMMENT '评分标准',
    `double_marking_threshold` INT DEFAULT 0 COMMENT '双评阈值',
    `enable_double_marking` TINYINT DEFAULT 0 COMMENT '是否启用双评: 0-否 1-是',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_paper_id` (`paper_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='试卷题目表';

-- 知识点表
CREATE TABLE IF NOT EXISTS `knowledge_point` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `subject_name` VARCHAR(50) NOT NULL COMMENT '科目名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父级ID',
    `name` VARCHAR(100) NOT NULL COMMENT '知识点名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '知识点编码',
    `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
    `path` VARCHAR(500) DEFAULT NULL COMMENT '路径',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_subject_name` (`subject_name`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='知识点表';

-- 题目知识点关联表
CREATE TABLE IF NOT EXISTS `question_knowledge_point` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `question_id` BIGINT NOT NULL COMMENT '题目ID',
    `knowledge_point_id` BIGINT NOT NULL COMMENT '知识点ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_knowledge_point_id` (`knowledge_point_id`),
    UNIQUE KEY `uk_question_knowledge` (`question_id`, `knowledge_point_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='题目知识点关联表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 添加知识点测试数据
INSERT INTO `knowledge_point` (`school_id`, `subject_name`, `parent_id`, `name`, `code`, `level`, `path`, `sort`, `status`) VALUES
(1, '语文', 0, '阅读理解', 'YW-YD', 1, '/1', 1, 1),
(1, '语文', 0, '写作', 'YW-XZ', 1, '/2', 2, 1),
(1, '语文', 0, '古诗文', 'YW-GSW', 1, '/3', 3, 1),
(1, '语文', 1, '现代文阅读', 'YW-YD-XDW', 2, '/1/4', 1, 1),
(1, '语文', 1, '文言文阅读', 'YW-YD-WYW', 2, '/1/5', 2, 1),
(1, '语文', 2, '记叙文', 'YW-XZ-JXW', 2, '/2/6', 1, 1),
(1, '语文', 2, '议论文', 'YW-XZ-YLW', 2, '/2/7', 2, 1),
(1, '数学', 0, '代数', 'SX-DS', 1, '/8', 1, 1),
(1, '数学', 0, '几何', 'SX-JH', 1, '/9', 2, 1),
(1, '数学', 0, '统计与概率', 'SX-TJGL', 1, '/10', 3, 1),
(1, '数学', 8, '方程', 'SX-DS-FC', 2, '/8/11', 1, 1),
(1, '数学', 8, '不等式', 'SX-DS-BDS', 2, '/8/12', 2, 1),
(1, '数学', 8, '函数', 'SX-DS-HS', 2, '/8/13', 3, 1),
(1, '数学', 9, '三角形', 'SX-JH-SJX', 2, '/9/14', 1, 1),
(1, '数学', 9, '四边形', 'SX-JH-SBX', 2, '/9/15', 2, 1),
(1, '数学', 9, '圆', 'SX-JH-Y', 2, '/9/16', 3, 1),
(1, '英语', 0, '词汇', 'ENG-CH', 1, '/17', 1, 1),
(1, '英语', 0, '语法', 'ENG-YF', 1, '/18', 2, 1),
(1, '英语', 0, '阅读', 'ENG-YD', 1, '/19', 3, 1),
(1, '英语', 0, '写作', 'ENG-XZ', 1, '/20', 4, 1);

-- 添加考试测试数据
INSERT INTO `exam` (`school_id`, `name`, `code`, `type`, `academic_year`, `semester`, `grade_id`, `status`, `total_score`, `description`) VALUES
(1, '2024-2025学年第一学期期中考试', 'EXAM-2024-001', 1, '2024-2025', 1, 1, 0, 450, '九年级期中考试'),
(1, '2024-2025学年第一学期月考（一）', 'EXAM-2024-002', 3, '2024-2025', 1, 1, 0, 450, '九年级第一次月考');

-- 添加考试科目测试数据
INSERT INTO `exam_subject` (`exam_id`, `subject_name`, `subject_code`, `full_score`, `pass_score`, `excellent_score`, `duration`, `sort`) VALUES
(1, '语文', 'YW', 150, 90, 128, 150, 1),
(1, '数学', 'SX', 150, 90, 128, 120, 2),
(1, '英语', 'ENG', 150, 90, 128, 120, 3),
(2, '语文', 'YW', 150, 90, 128, 150, 1),
(2, '数学', 'SX', 150, 90, 128, 120, 2),
(2, '英语', 'ENG', 150, 90, 128, 120, 3);

-- 添加考试班级关联测试数据
INSERT INTO `exam_class` (`exam_id`, `class_id`) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 2);
