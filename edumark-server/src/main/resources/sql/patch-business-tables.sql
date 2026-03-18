USE edumark;

CREATE TABLE IF NOT EXISTS exam (
    id BIGINT NOT NULL COMMENT '主键ID',
    school_id BIGINT NOT NULL COMMENT '学校ID',
    name VARCHAR(100) NOT NULL COMMENT '考试名称',
    code VARCHAR(50) COMMENT '考试编码',
    type TINYINT DEFAULT 5 COMMENT '考试类型',
    academic_year VARCHAR(20) COMMENT '学年',
    semester TINYINT DEFAULT 1 COMMENT '学期',
    grade_id BIGINT COMMENT '年级ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status TINYINT DEFAULT 0 COMMENT '状态',
    total_score INT DEFAULT 0 COMMENT '总分',
    student_count INT DEFAULT 0 COMMENT '参考人数',
    description VARCHAR(500) COMMENT '描述',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_exam_school_id (school_id),
    KEY idx_exam_grade_id (grade_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

CREATE TABLE IF NOT EXISTS exam_subject (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    subject_name VARCHAR(50) NOT NULL COMMENT '科目名称',
    subject_code VARCHAR(50) COMMENT '科目编码',
    full_score INT DEFAULT 100 COMMENT '满分',
    pass_score INT DEFAULT 60 COMMENT '及格分',
    excellent_score INT DEFAULT 90 COMMENT '优秀分',
    duration INT DEFAULT 120 COMMENT '考试时长',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_exam_subject_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试科目表';

CREATE TABLE IF NOT EXISTS exam_class (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exam_class (exam_id, class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试班级关联表';

CREATE TABLE IF NOT EXISTS knowledge_point (
    id BIGINT NOT NULL COMMENT '主键ID',
    school_id BIGINT NOT NULL COMMENT '学校ID',
    subject_name VARCHAR(50) NOT NULL COMMENT '科目名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    code VARCHAR(50) COMMENT '知识点编码',
    level INT DEFAULT 1 COMMENT '层级',
    path VARCHAR(500) COMMENT '路径',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_knowledge_school_subject (school_id, subject_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

CREATE TABLE IF NOT EXISTS paper (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_subject_id BIGINT NOT NULL COMMENT '考试科目ID',
    name VARCHAR(100) NOT NULL COMMENT '试卷名称',
    code VARCHAR(50) COMMENT '试卷编码',
    type TINYINT DEFAULT 1 COMMENT '试卷类型',
    total_score INT DEFAULT 100 COMMENT '总分',
    question_count INT DEFAULT 0 COMMENT '题目数量',
    objective_count INT DEFAULT 0 COMMENT '客观题数量',
    subjective_count INT DEFAULT 0 COMMENT '主观题数量',
    status TINYINT DEFAULT 1 COMMENT '状态',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_paper_exam_subject_id (exam_subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

CREATE TABLE IF NOT EXISTS paper_question (
    id BIGINT NOT NULL COMMENT '主键ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    question_no VARCHAR(50) NOT NULL COMMENT '题号',
    section_no INT DEFAULT 1 COMMENT '大题号',
    section_name VARCHAR(100) COMMENT '大题名称',
    item_no INT DEFAULT 1 COMMENT '小题号',
    question_type TINYINT DEFAULT 5 COMMENT '题型',
    is_objective TINYINT DEFAULT 0 COMMENT '是否客观题',
    score INT DEFAULT 0 COMMENT '分值',
    correct_answer TEXT COMMENT '正确答案',
    scoring_criteria TEXT COMMENT '评分标准',
    double_marking_threshold INT DEFAULT 0 COMMENT '双评阈值',
    enable_double_marking TINYINT DEFAULT 0 COMMENT '是否启用双评',
    sort INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_paper_question_paper_id (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目表';

CREATE TABLE IF NOT EXISTS question_knowledge_point (
    id BIGINT NOT NULL COMMENT '主键ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    knowledge_point_id BIGINT NOT NULL COMMENT '知识点ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_knowledge (question_id, knowledge_point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目知识点关联表';

CREATE TABLE IF NOT EXISTS answer_sheet_template (
    id BIGINT NOT NULL COMMENT '主键ID',
    paper_id BIGINT NOT NULL COMMENT '试卷ID',
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    page_size VARCHAR(20) DEFAULT 'A4' COMMENT '纸张大小',
    orientation TINYINT DEFAULT 1 COMMENT '方向',
    columns INT DEFAULT 1 COMMENT '列数',
    margin_top INT DEFAULT 10 COMMENT '上边距',
    margin_bottom INT DEFAULT 10 COMMENT '下边距',
    margin_left INT DEFAULT 10 COMMENT '左边距',
    margin_right INT DEFAULT 10 COMMENT '右边距',
    header_config JSON NULL COMMENT '页眉配置',
    student_info_config JSON NULL COMMENT '学生信息区配置',
    status TINYINT DEFAULT 0 COMMENT '状态',
    pdf_object_name VARCHAR(255) COMMENT 'PDF对象名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_answer_sheet_template_paper_id (paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题卡模板表';

CREATE TABLE IF NOT EXISTS answer_sheet_region (
    id BIGINT NOT NULL COMMENT '主键ID',
    template_id BIGINT NOT NULL COMMENT '模板ID',
    region_type TINYINT NOT NULL COMMENT '区域类型',
    region_name VARCHAR(100) NOT NULL COMMENT '区域名称',
    page_no INT DEFAULT 1 COMMENT '页码',
    sort_order INT DEFAULT 0 COMMENT '排序',
    question_start INT NULL COMMENT '起始题号',
    question_end INT NULL COMMENT '结束题号',
    question_ids JSON NULL COMMENT '题目ID列表',
    config JSON NULL COMMENT '区域配置',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_answer_sheet_region_template_id (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题卡区域表';

CREATE TABLE IF NOT EXISTS answer_sheet (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    exam_subject_id BIGINT NOT NULL COMMENT '考试科目ID',
    student_id BIGINT NULL COMMENT '学生ID',
    student_number VARCHAR(50) COMMENT '学号',
    seat_number VARCHAR(50) COMMENT '座位号',
    image_count INT DEFAULT 0 COMMENT '图片数量',
    status TINYINT DEFAULT 0 COMMENT '状态',
    objective_score INT DEFAULT 0 COMMENT '客观题得分',
    subjective_score INT DEFAULT 0 COMMENT '主观题得分',
    total_score INT DEFAULT 0 COMMENT '总分',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_answer_sheet_exam_subject (exam_subject_id),
    KEY idx_answer_sheet_student (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题卡表';

CREATE TABLE IF NOT EXISTS answer_sheet_image (
    id BIGINT NOT NULL COMMENT '主键ID',
    answer_sheet_id BIGINT NOT NULL COMMENT '答题卡ID',
    page_num INT DEFAULT 1 COMMENT '页码',
    image_path VARCHAR(500) COMMENT '图片路径',
    image_url VARCHAR(500) COMMENT '图片URL',
    original_name VARCHAR(255) COMMENT '原始文件名',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小',
    width INT DEFAULT 0 COMMENT '宽度',
    height INT DEFAULT 0 COMMENT '高度',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_answer_sheet_image_sheet_id (answer_sheet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题卡图片表';

CREATE TABLE IF NOT EXISTS answer_sheet_detail (
    id BIGINT NOT NULL COMMENT '主键ID',
    answer_sheet_id BIGINT NOT NULL COMMENT '答题卡ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    student_answer TEXT COMMENT '学生答案',
    score INT DEFAULT 0 COMMENT '得分',
    status TINYINT DEFAULT 0 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_answer_sheet_detail_sheet_id (answer_sheet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题卡明细表';

CREATE TABLE IF NOT EXISTS marking_task (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    exam_subject_id BIGINT NOT NULL COMMENT '考试科目ID',
    question_id BIGINT NULL COMMENT '题目ID',
    name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_type TINYINT DEFAULT 2 COMMENT '任务类型',
    total_count INT DEFAULT 0 COMMENT '总份数',
    completed_count INT DEFAULT 0 COMMENT '已完成份数',
    pending_count INT DEFAULT 0 COMMENT '待阅份数',
    enable_double_marking TINYINT DEFAULT 0 COMMENT '是否双评',
    double_marking_threshold INT DEFAULT 0 COMMENT '双评阈值',
    status TINYINT DEFAULT 0 COMMENT '状态',
    start_time DATETIME NULL COMMENT '开始时间',
    end_time DATETIME NULL COMMENT '结束时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_marking_task_exam_subject_id (exam_subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅卷任务表';

CREATE TABLE IF NOT EXISTS marking_task_assign (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    assign_count INT DEFAULT 0 COMMENT '分配份数',
    completed_count INT DEFAULT 0 COMMENT '完成份数',
    marking_role TINYINT DEFAULT 1 COMMENT '评阅角色',
    status TINYINT DEFAULT 0 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_marking_task_assign_task_id (task_id),
    KEY idx_marking_task_assign_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅卷任务分配表';

CREATE TABLE IF NOT EXISTS marking_record (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    answer_sheet_id BIGINT NOT NULL COMMENT '答题卡ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    marking_role TINYINT DEFAULT 1 COMMENT '评阅角色',
    score INT DEFAULT 0 COMMENT '得分',
    full_score INT DEFAULT 0 COMMENT '满分',
    comment VARCHAR(1000) COMMENT '评语',
    marking_time DATETIME NULL COMMENT '阅卷时间',
    status TINYINT DEFAULT 0 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_marking_record_task_id (task_id),
    KEY idx_marking_record_answer_sheet_id (answer_sheet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅卷记录表';

CREATE TABLE IF NOT EXISTS marking_arbitration (
    id BIGINT NOT NULL COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    answer_sheet_id BIGINT NOT NULL COMMENT '答题卡ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    first_marking_id BIGINT NULL COMMENT '一评记录ID',
    first_score INT DEFAULT 0 COMMENT '一评分数',
    first_teacher_id BIGINT NULL COMMENT '一评教师ID',
    second_marking_id BIGINT NULL COMMENT '二评记录ID',
    second_score INT DEFAULT 0 COMMENT '二评分数',
    second_teacher_id BIGINT NULL COMMENT '二评教师ID',
    score_diff INT DEFAULT 0 COMMENT '分差',
    arbitration_teacher_id BIGINT NULL COMMENT '仲裁教师ID',
    arbitration_score INT DEFAULT 0 COMMENT '仲裁分数',
    arbitration_time DATETIME NULL COMMENT '仲裁时间',
    arbitration_comment VARCHAR(1000) COMMENT '仲裁说明',
    status TINYINT DEFAULT 0 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_marking_arbitration_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阅卷仲裁表';

CREATE TABLE IF NOT EXISTS exam_score (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    class_id BIGINT NULL COMMENT '班级ID',
    total_score DECIMAL(10,2) DEFAULT 0 COMMENT '总分',
    subject_count INT DEFAULT 0 COMMENT '科目数',
    class_rank INT DEFAULT 0 COMMENT '班级排名',
    grade_rank INT DEFAULT 0 COMMENT '年级排名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_exam_score_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试成绩汇总表';

CREATE TABLE IF NOT EXISTS subject_score (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    exam_subject_id BIGINT NOT NULL COMMENT '考试科目ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    class_id BIGINT NULL COMMENT '班级ID',
    answer_sheet_id BIGINT NULL COMMENT '答题卡ID',
    score DECIMAL(10,2) DEFAULT 0 COMMENT '得分',
    objective_score DECIMAL(10,2) DEFAULT 0 COMMENT '客观题得分',
    subjective_score DECIMAL(10,2) DEFAULT 0 COMMENT '主观题得分',
    class_rank INT DEFAULT 0 COMMENT '班级排名',
    grade_rank INT DEFAULT 0 COMMENT '年级排名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_subject_score_exam_subject_id (exam_subject_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科目成绩表';

CREATE TABLE IF NOT EXISTS score_statistics (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    exam_subject_id BIGINT NULL COMMENT '考试科目ID',
    class_id BIGINT NULL COMMENT '班级ID',
    stat_type TINYINT NOT NULL COMMENT '统计类型',
    student_count INT DEFAULT 0 COMMENT '参考人数',
    full_score DECIMAL(10,2) DEFAULT 0 COMMENT '满分',
    max_score DECIMAL(10,2) DEFAULT 0 COMMENT '最高分',
    min_score DECIMAL(10,2) DEFAULT 0 COMMENT '最低分',
    avg_score DECIMAL(10,2) DEFAULT 0 COMMENT '平均分',
    pass_count INT DEFAULT 0 COMMENT '及格人数',
    pass_rate DECIMAL(10,2) DEFAULT 0 COMMENT '及格率',
    excellent_count INT DEFAULT 0 COMMENT '优秀人数',
    excellent_rate DECIMAL(10,2) DEFAULT 0 COMMENT '优秀率',
    score_segments JSON NULL COMMENT '分数段统计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_score_statistics_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩统计表';

CREATE TABLE IF NOT EXISTS score_publish_record (
    id BIGINT NOT NULL COMMENT '主键ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    publish_type TINYINT NOT NULL COMMENT '发布类型',
    publish_time DATETIME NULL COMMENT '发布时间',
    publish_by BIGINT NULL COMMENT '操作人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_score_publish_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩发布记录表';

INSERT INTO exam (id, school_id, name, code, type, academic_year, semester, grade_id, start_time, end_time, status, total_score, student_count, description, remark, create_time, update_time, deleted)
SELECT 10001, 1, '示例期中考试', 'EXAM_DEMO_001', 1, '2025-2026', 2, 1, '2026-03-20 09:00:00', '2026-03-20 11:00:00', 1, 100, 0, '用于本地联调的示例考试', '系统自动生成', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM exam WHERE id = 10001);

INSERT INTO exam_subject (id, exam_id, subject_name, subject_code, full_score, pass_score, excellent_score, duration, start_time, end_time, sort, status, remark, create_time, update_time, deleted)
SELECT 11001, 10001, '数学', 'MATH', 100, 60, 90, 120, '2026-03-20 09:00:00', '2026-03-20 11:00:00', 1, 1, '示例科目', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM exam_subject WHERE id = 11001);

INSERT INTO exam_class (id, exam_id, class_id, create_time, update_time, deleted)
SELECT 11101, 10001, 1, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM exam_class WHERE id = 11101);

INSERT INTO paper (id, exam_subject_id, name, code, type, total_score, question_count, objective_count, subjective_count, status, remark, create_time, update_time, deleted)
SELECT 12001, 11001, '数学试卷A', 'PAPER_DEMO_001', 1, 100, 4, 2, 2, 1, '示例试卷', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM paper WHERE id = 12001);

INSERT INTO paper_question (id, paper_id, question_no, section_no, section_name, item_no, question_type, is_objective, score, correct_answer, scoring_criteria, double_marking_threshold, enable_double_marking, sort, remark, create_time, update_time, deleted)
SELECT 12101, 12001, '1', 1, '选择题', 1, 1, 1, 10, 'A', '按标准答案给分', 0, 0, 1, '示例题目', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM paper_question WHERE id = 12101);

INSERT INTO paper_question (id, paper_id, question_no, section_no, section_name, item_no, question_type, is_objective, score, correct_answer, scoring_criteria, double_marking_threshold, enable_double_marking, sort, remark, create_time, update_time, deleted)
SELECT 12102, 12001, '2', 2, '解答题', 1, 5, 0, 20, NULL, '按步骤给分', 5, 1, 2, '示例主观题', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM paper_question WHERE id = 12102);

INSERT INTO knowledge_point (id, school_id, subject_name, parent_id, name, code, level, path, sort, status, remark, create_time, update_time, deleted)
SELECT 14001, 1, '数学', 0, '函数', 'KP_MATH_001', 1, '函数', 1, 1, '示例知识点', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM knowledge_point WHERE id = 14001);

INSERT INTO question_knowledge_point (id, question_id, knowledge_point_id, create_time, update_time, deleted)
SELECT 14101, 12102, 14001, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM question_knowledge_point WHERE id = 14101);

INSERT INTO answer_sheet_template (id, paper_id, name, page_size, orientation, columns, margin_top, margin_bottom, margin_left, margin_right, header_config, student_info_config, status, pdf_object_name, create_time, update_time, deleted)
SELECT 13001, 12001, '数学答题卡模板', 'A4', 1, 1, 10, 10, 10, 10, JSON_OBJECT('title', '示例期中考试 数学答题卡', 'showTitle', true), JSON_OBJECT('showStudentId', true, 'showName', true, 'showClass', true), 1, NULL, NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM answer_sheet_template WHERE id = 13001);

INSERT INTO answer_sheet_region (id, template_id, region_type, region_name, page_no, sort_order, question_start, question_end, question_ids, config, create_time, update_time, deleted)
SELECT 13101, 13001, 1, '选择题区域', 1, 1, 1, 1, JSON_ARRAY(12101), JSON_OBJECT('optionCount', 4, 'questionsPerRow', 5, 'bubbleStyle', 'circle'), NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM answer_sheet_region WHERE id = 13101);

INSERT INTO answer_sheet_region (id, template_id, region_type, region_name, page_no, sort_order, question_start, question_end, question_ids, config, create_time, update_time, deleted)
SELECT 13102, 13001, 3, '解答题区域', 1, 2, 2, 2, JSON_ARRAY(12102), JSON_OBJECT('height', 240, 'showBorder', true, 'scoreBoxPosition', 'top-right'), NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM answer_sheet_region WHERE id = 13102);
