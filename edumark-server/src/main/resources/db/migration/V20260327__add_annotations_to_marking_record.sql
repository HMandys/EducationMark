-- 阅卷记录增加标注数据字段
ALTER TABLE marking_record ADD COLUMN annotations TEXT COMMENT '标注数据(JSON)' AFTER skip_count;
