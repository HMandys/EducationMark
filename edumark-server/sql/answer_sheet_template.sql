-- 答题卡模板表
CREATE TABLE `answer_sheet_template` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `paper_id` bigint NOT NULL COMMENT '关联试卷ID',
    `name` varchar(100) NOT NULL COMMENT '模板名称',
    `page_size` varchar(20) NOT NULL DEFAULT 'A4' COMMENT '纸张大小: A4/A3/B5',
    `orientation` tinyint NOT NULL DEFAULT 1 COMMENT '方向: 1-纵向 2-横向',
    `columns` tinyint NOT NULL DEFAULT 1 COMMENT '列数: 1-单列 2-双列',
    `margin_top` int NOT NULL DEFAULT 20 COMMENT '上边距(mm)',
    `margin_bottom` int NOT NULL DEFAULT 20 COMMENT '下边距(mm)',
    `margin_left` int NOT NULL DEFAULT 15 COMMENT '左边距(mm)',
    `margin_right` int NOT NULL DEFAULT 15 COMMENT '右边距(mm)',
    `header_config` json DEFAULT NULL COMMENT '页眉配置',
    `student_info_config` json DEFAULT NULL COMMENT '学生信息区配置',
    `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿 1-已发布',
    `pdf_object_name` varchar(255) DEFAULT NULL COMMENT 'PDF存储路径',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='答题卡模板表';

-- 答题区域配置表
CREATE TABLE `answer_sheet_region` (
    `id` bigint NOT NULL COMMENT '主键ID',
    `template_id` bigint NOT NULL COMMENT '模板ID',
    `region_type` tinyint NOT NULL COMMENT '区域类型: 1-选择题 2-填空题 3-解答题 4-作文题',
    `region_name` varchar(50) NOT NULL COMMENT '区域名称',
    `page_no` int NOT NULL DEFAULT 1 COMMENT '页码',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `question_start` int DEFAULT NULL COMMENT '起始题号',
    `question_end` int DEFAULT NULL COMMENT '结束题号',
    `question_ids` json DEFAULT NULL COMMENT '关联题目ID列表',
    `config` json DEFAULT NULL COMMENT '区域配置(选项数、行高、格子等)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_page_sort` (`template_id`, `page_no`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='答题区域配置表';

-- ==================================================
-- 答题卡设计功能重做 - 数据库变更
-- ==================================================

-- 答题卡模板表新增字段
ALTER TABLE `answer_sheet_template`
ADD COLUMN `template_image_path` VARCHAR(500) DEFAULT NULL COMMENT '模板图片路径' AFTER `pdf_object_name`,
ADD COLUMN `corner_config` JSON DEFAULT NULL COMMENT '四角定位点配置' AFTER `template_image_path`;

-- corner_config JSON 结构示例:
-- {
--   "topLeft": { "x": 5.2, "y": 3.8 },
--   "topRight": { "x": 94.5, "y": 4.0 },
--   "bottomLeft": { "x": 5.0, "y": 96.2 },
--   "bottomRight": { "x": 94.8, "y": 96.0 },
--   "corrected": true,
--   "angle": 0.5
-- }

-- 答题区域配置表新增正确答案字段（客观题用）
-- 该字段存储在 config JSON 中，键名为 correctAnswers
-- config.correctAnswers 结构示例:
-- {
--   "1": "A",
--   "2": "B",
--   "3": "CD",
--   "4": "A"
-- }
