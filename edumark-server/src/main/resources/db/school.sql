-- =============================================
-- EduMark 学校组织管理模块表结构
-- =============================================

-- 学校表
CREATE TABLE IF NOT EXISTS `school` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '学校名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '学校编码',
    `type` TINYINT DEFAULT 1 COMMENT '学校类型: 1-小学 2-初中 3-高中 4-完全中学 5-九年一贯制',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
    `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '负责人姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '负责人手机',
    `logo` VARCHAR(255) DEFAULT NULL COMMENT '学校Logo',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学校表';

-- 年级表
CREATE TABLE IF NOT EXISTS `grade` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `name` VARCHAR(50) NOT NULL COMMENT '年级名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '年级编码',
    `enroll_year` INT DEFAULT NULL COMMENT '入学年份',
    `grade_num` TINYINT DEFAULT NULL COMMENT '年级序号: 1-9表示一年级到九年级, 10-12表示高一到高三',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='年级表';

-- 班级表
CREATE TABLE IF NOT EXISTS `class_info` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `grade_id` BIGINT NOT NULL COMMENT '所属年级ID',
    `name` VARCHAR(50) NOT NULL COMMENT '班级名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '班级编码',
    `class_num` TINYINT DEFAULT NULL COMMENT '班级序号',
    `head_teacher_id` BIGINT DEFAULT NULL COMMENT '班主任教师ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_grade_id` (`grade_id`),
    KEY `idx_head_teacher_id` (`head_teacher_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 教师表
CREATE TABLE IF NOT EXISTS `teacher` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `job_number` VARCHAR(50) DEFAULT NULL COMMENT '工号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `subject` VARCHAR(50) DEFAULT NULL COMMENT '任教科目',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
    `entry_date` VARCHAR(20) DEFAULT NULL COMMENT '入职日期',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-离职 1-在职',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- 学生表
CREATE TABLE IF NOT EXISTS `student` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `school_id` BIGINT NOT NULL COMMENT '所属学校ID',
    `class_id` BIGINT NOT NULL COMMENT '所属班级ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `student_number` VARCHAR(50) NOT NULL COMMENT '学号',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `birthday` VARCHAR(20) DEFAULT NULL COMMENT '出生日期',
    `enroll_date` VARCHAR(20) DEFAULT NULL COMMENT '入学日期',
    `bind_code` VARCHAR(10) DEFAULT NULL COMMENT '绑定码-家长绑定用',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-休学 1-在读 2-毕业 3-退学',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_school_id` (`school_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_student_number` (`student_number`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- 家长表
CREATE TABLE IF NOT EXISTS `parent` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知 1-男 2-女',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家长表';

-- 家长学生绑定关系表
CREATE TABLE IF NOT EXISTS `parent_student_bind` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT NOT NULL COMMENT '家长ID',
    `student_id` BIGINT NOT NULL COMMENT '学生ID',
    `relation` TINYINT DEFAULT 9 COMMENT '关系: 1-父亲 2-母亲 3-爷爷 4-奶奶 5-外公 6-外婆 9-其他',
    `is_primary` TINYINT DEFAULT 0 COMMENT '是否为主要联系人: 0-否 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_parent_student` (`parent_id`, `student_id`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家长学生绑定关系表';

-- =============================================
-- 插入测试数据
-- =============================================

-- 插入测试学校
INSERT INTO `school` (`id`, `name`, `code`, `type`, `province`, `city`, `district`, `address`, `phone`, `contact_name`, `contact_phone`, `status`) VALUES
(1, '北京市第一中学', 'BJ001', 4, '北京市', '北京市', '东城区', '东城区xx路100号', '010-12345678', '张校长', '13800138001', 1),
(2, '北京市实验小学', 'BJ002', 1, '北京市', '北京市', '西城区', '西城区xx路200号', '010-87654321', '李校长', '13800138002', 1);

-- 插入测试年级
INSERT INTO `grade` (`id`, `school_id`, `name`, `code`, `enroll_year`, `grade_num`, `status`, `sort`) VALUES
(1, 1, '高一年级', 'G10', 2024, 10, 1, 1),
(2, 1, '高二年级', 'G11', 2023, 11, 1, 2),
(3, 1, '高三年级', 'G12', 2022, 12, 1, 3),
(4, 2, '一年级', 'G1', 2024, 1, 1, 1),
(5, 2, '二年级', 'G2', 2023, 2, 1, 2);

-- 插入测试班级
INSERT INTO `class_info` (`id`, `school_id`, `grade_id`, `name`, `code`, `class_num`, `status`, `sort`) VALUES
(1, 1, 1, '高一(1)班', 'G10C1', 1, 1, 1),
(2, 1, 1, '高一(2)班', 'G10C2', 2, 1, 2),
(3, 1, 2, '高二(1)班', 'G11C1', 1, 1, 1),
(4, 2, 4, '一年级(1)班', 'G1C1', 1, 1, 1),
(5, 2, 4, '一年级(2)班', 'G1C2', 2, 1, 2);
