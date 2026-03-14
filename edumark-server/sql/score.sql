-- 学生考试成绩汇总表（一个学生一次考试的总成绩）
CREATE TABLE `exam_score` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `exam_id` bigint NOT NULL COMMENT '考试ID',
    `student_id` bigint NOT NULL COMMENT '学生ID',
    `class_id` bigint DEFAULT NULL COMMENT '班级ID',
    `total_score` decimal(6,1) NOT NULL DEFAULT 0 COMMENT '总分',
    `subject_count` int NOT NULL DEFAULT 0 COMMENT '科目数',
    `class_rank` int DEFAULT NULL COMMENT '班级排名',
    `grade_rank` int DEFAULT NULL COMMENT '年级排名',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_exam_student` (`exam_id`, `student_id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生考试成绩汇总表';

-- 学生科目成绩表（单科成绩及排名）
CREATE TABLE `subject_score` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `exam_id` bigint NOT NULL COMMENT '考试ID',
    `exam_subject_id` bigint NOT NULL COMMENT '考试科目ID',
    `student_id` bigint NOT NULL COMMENT '学生ID',
    `class_id` bigint DEFAULT NULL COMMENT '班级ID',
    `answer_sheet_id` bigint DEFAULT NULL COMMENT '答题卡ID',
    `score` decimal(5,1) NOT NULL DEFAULT 0 COMMENT '得分',
    `objective_score` decimal(5,1) DEFAULT 0 COMMENT '客观题得分',
    `subjective_score` decimal(5,1) DEFAULT 0 COMMENT '主观题得分',
    `class_rank` int DEFAULT NULL COMMENT '班级排名',
    `grade_rank` int DEFAULT NULL COMMENT '年级排名',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_subject_student` (`exam_subject_id`, `student_id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_exam_subject_id` (`exam_subject_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生科目成绩表';

-- 成绩统计表（班级/年级维度的统计数据）
CREATE TABLE `score_statistics` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `exam_id` bigint NOT NULL COMMENT '考试ID',
    `exam_subject_id` bigint DEFAULT NULL COMMENT '考试科目ID(为空表示总分统计)',
    `class_id` bigint DEFAULT NULL COMMENT '班级ID(为空表示年级统计)',
    `stat_type` tinyint NOT NULL COMMENT '统计类型: 1-班级科目 2-班级总分 3-年级科目 4-年级总分',
    `student_count` int NOT NULL DEFAULT 0 COMMENT '参考人数',
    `full_score` decimal(6,1) DEFAULT NULL COMMENT '满分',
    `max_score` decimal(5,1) DEFAULT NULL COMMENT '最高分',
    `min_score` decimal(5,1) DEFAULT NULL COMMENT '最低分',
    `avg_score` decimal(5,1) DEFAULT NULL COMMENT '平均分',
    `pass_count` int DEFAULT 0 COMMENT '及格人数',
    `pass_rate` decimal(5,2) DEFAULT NULL COMMENT '及格率(%)',
    `excellent_count` int DEFAULT 0 COMMENT '优秀人数',
    `excellent_rate` decimal(5,2) DEFAULT NULL COMMENT '优秀率(%)',
    `score_segments` json DEFAULT NULL COMMENT '分数段统计',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`),
    KEY `idx_exam_subject_id` (`exam_subject_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成绩统计表';

-- 成绩发布记录表
CREATE TABLE `score_publish_record` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `exam_id` bigint NOT NULL COMMENT '考试ID',
    `publish_type` tinyint NOT NULL COMMENT '发布类型: 1-发布 2-撤回',
    `publish_time` datetime NOT NULL COMMENT '发布/撤回时间',
    `publish_by` bigint NOT NULL COMMENT '操作人ID',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_exam_id` (`exam_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成绩发布记录表';
