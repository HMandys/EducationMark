USE edumark;

CREATE TABLE IF NOT EXISTS ai_marking_provider (
    id BIGINT NOT NULL COMMENT '主键ID',
    provider_name VARCHAR(100) NOT NULL COMMENT '提供商名称',
    protocol VARCHAR(50) NOT NULL COMMENT '协议类型',
    base_url VARCHAR(255) NOT NULL COMMENT '接口地址',
    api_key VARCHAR(500) COMMENT 'API Key',
    model VARCHAR(100) NOT NULL COMMENT '模型名称',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认提供商',
    timeout_ms INT DEFAULT 30000 COMMENT '超时时间(毫秒)',
    max_tokens INT DEFAULT 2048 COMMENT '最大输出Token',
    priority INT DEFAULT 0 COMMENT '优先级',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ai_marking_provider_name (provider_name),
    KEY idx_ai_marking_provider_protocol (protocol),
    KEY idx_ai_marking_provider_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI批改提供商配置表';

CREATE TABLE IF NOT EXISTS ai_marking_policy (
    id BIGINT NOT NULL COMMENT '主键ID',
    enabled TINYINT DEFAULT 0 COMMENT '是否启用 AI 自动批改',
    low_confidence_threshold DECIMAL(5,4) DEFAULT 0.7500 COMMENT '低置信度阈值',
    failure_strategy VARCHAR(50) DEFAULT 'exception-pool' COMMENT '失败回退策略',
    prompt_template TEXT COMMENT '默认提示词模板',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI批改策略配置表';

CREATE TABLE IF NOT EXISTS ai_marking_record (
    id BIGINT NOT NULL COMMENT '主键ID',
    answer_sheet_id BIGINT NOT NULL COMMENT '答题卡ID',
    detail_id BIGINT COMMENT '题目明细ID',
    question_id BIGINT COMMENT '题目ID',
    region_id BIGINT COMMENT '区域ID',
    question_no INT COMMENT '题号',
    provider_id BIGINT COMMENT '提供商ID',
    provider_name VARCHAR(100) COMMENT '提供商名称',
    protocol VARCHAR(50) COMMENT '协议类型',
    model VARCHAR(100) COMMENT '模型名称',
    reference_answer VARCHAR(500) COMMENT '标准答案',
    recognized_text VARCHAR(500) COMMENT '识别文本',
    suggested_score INT COMMENT '建议得分',
    confidence DECIMAL(6,4) COMMENT '置信度',
    judge_reason VARCHAR(1000) COMMENT '判分理由',
    status TINYINT DEFAULT 0 COMMENT '执行状态: 0-失败 1-成功',
    error_message VARCHAR(500) COMMENT '错误信息',
    raw_response TEXT COMMENT '原始响应',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人ID',
    update_by BIGINT COMMENT '更新人ID',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_ai_marking_record_sheet (answer_sheet_id),
    KEY idx_ai_marking_record_question (question_id),
    KEY idx_ai_marking_record_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI批改审计记录表';
