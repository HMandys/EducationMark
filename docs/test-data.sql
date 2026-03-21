-- ============================================
-- EduMark 考试系统测试数据
-- ============================================

-- 清理旧测试数据（使用多变量方式避免嵌套子查询问题）
DELETE FROM answer_sheet_detail WHERE answer_sheet_id IN (
    SELECT id FROM (SELECT id FROM answer_sheet WHERE exam_subject_id IN (
        SELECT id FROM exam_subject WHERE exam_id IN (
            SELECT id FROM exam WHERE code LIKE 'TEST%'
        )
    )) AS tmp
);
DELETE FROM marking_record WHERE task_id IN (
    SELECT id FROM marking_task WHERE exam_id IN (
        SELECT id FROM exam WHERE code LIKE 'TEST%'
    )
);
DELETE FROM marking_task WHERE exam_id IN (
    SELECT id FROM exam WHERE code LIKE 'TEST%'
);
DELETE FROM answer_sheet WHERE exam_subject_id IN (
    SELECT id FROM exam_subject WHERE exam_id IN (
        SELECT id FROM exam WHERE code LIKE 'TEST%'
    )
);
DELETE FROM paper_question WHERE paper_id IN (
    SELECT id FROM paper WHERE exam_subject_id IN (
        SELECT id FROM exam_subject WHERE exam_id IN (
            SELECT id FROM exam WHERE code LIKE 'TEST%'
        )
    )
);
DELETE FROM paper WHERE exam_subject_id IN (
    SELECT id FROM exam_subject WHERE exam_id IN (
        SELECT id FROM exam WHERE code LIKE 'TEST%'
    )
);
DELETE FROM exam_subject WHERE exam_id IN (
    SELECT id FROM exam WHERE code LIKE 'TEST%'
);
DELETE FROM exam_class WHERE exam_id IN (
    SELECT id FROM exam WHERE code LIKE 'TEST%'
);
DELETE FROM exam WHERE code LIKE 'TEST%';
DELETE FROM student WHERE student_code LIKE 'TEST%';
DELETE FROM class_info WHERE grade_id IN (
    SELECT id FROM grade WHERE grade_name LIKE '测试年级%'
);
DELETE FROM grade WHERE grade_name LIKE '测试年级%';
DELETE FROM school WHERE school_name LIKE '测试学校%';

-- ============================================
-- 1. 学校数据
-- ============================================
INSERT INTO school (id, school_name, school_code, province, city, district, address, contact_person, contact_phone, status, create_time, update_time, deleted) VALUES
(100, '测试第一中学', 'SCH001', '北京市', '北京市', '朝阳区', '北京市朝阳区', '张校长', '010-12345678', 1, NOW(), NOW(), 0);

SET @school_id = 100;

-- ============================================
-- 2. 年级数据
-- ============================================
INSERT INTO grade (id, school_id, grade_name, grade_code, sort, status, create_time, update_time, deleted) VALUES
(10, @school_id, '高一', 'G202410', 1, 1, NOW(), NOW(), 0),
(11, @school_id, '高二', 'G202411', 2, 1, NOW(), NOW(), 0),
(12, @school_id, '高三', 'G202412', 3, 1, NOW(), NOW(), 0);

SET @grade1_id = 10;
SET @grade2_id = 11;
SET @grade3_id = 12;

-- ============================================
-- 3. 班级数据
-- ============================================
INSERT INTO class_info (id, school_id, grade_id, class_name, class_code, student_count, sort, status, create_time, update_time, deleted) VALUES
(100, @school_id, @grade1_id, '高一(1)班', 'G202410C01', 45, 1, 1, NOW(), NOW(), 0),
(101, @school_id, @grade1_id, '高一(2)班', 'G202410C02', 44, 2, 1, NOW(), NOW(), 0),
(102, @school_id, @grade2_id, '高二(1)班', 'G202411C01', 42, 3, 1, NOW(), NOW(), 0),
(103, @school_id, @grade2_id, '高二(2)班', 'G202411C02', 43, 4, 1, NOW(), NOW(), 0),
(104, @school_id, @grade3_id, '高三(1)班', 'G202412C01', 40, 5, 1, NOW(), NOW(), 0);

SET @class1_id = 100;
SET @class2_id = 101;
SET @class3_id = 102;

-- ============================================
-- 4. 学生数据
-- ============================================
INSERT INTO student (id, school_id, grade_id, class_id, student_name, student_code, gender, status, create_time, update_time, deleted) VALUES
-- 高一(1)班学生
(1000, @school_id, @grade1_id, @class1_id, '张三', 'TEST202401001', 1, 1, NOW(), NOW(), 0),
(1001, @school_id, @grade1_id, @class1_id, '李四', 'TEST202401002', 1, 1, NOW(), NOW(), 0),
(1002, @school_id, @grade1_id, @class1_id, '王五', 'TEST202401003', 1, 1, NOW(), NOW(), 0),
(1003, @school_id, @grade1_id, @class1_id, '赵六', 'TEST202401004', 2, 1, NOW(), NOW(), 0),
(1004, @school_id, @grade1_id, @class1_id, '钱七', 'TEST202401005', 1, 1, NOW(), NOW(), 0),
(1005, @school_id, @grade1_id, @class1_id, '孙八', 'TEST202401006', 2, 1, NOW(), NOW(), 0),
(1006, @school_id, @grade1_id, @class1_id, '周九', 'TEST202401007', 1, 1, NOW(), NOW(), 0),
(1007, @school_id, @grade1_id, @class1_id, '吴十', 'TEST202401008', 2, 1, NOW(), NOW(), 0),
(1008, @school_id, @grade1_id, @class1_id, '郑一', 'TEST202401009', 1, 1, NOW(), NOW(), 0),
(1009, @school_id, @grade1_id, @class1_id, '王小二', 'TEST202401010', 1, 1, NOW(), NOW(), 0),
-- 高一(2)班学生
(1010, @school_id, @grade1_id, @class2_id, '陈明', 'TEST202402001', 1, 1, NOW(), NOW(), 0),
(1011, @school_id, @grade1_id, @class2_id, '林芳', 'TEST202402002', 2, 1, NOW(), NOW(), 0),
(1012, @school_id, @grade1_id, @class2_id, '黄强', 'TEST202402003', 1, 1, NOW(), NOW(), 0),
(1013, @school_id, @grade1_id, @class2_id, '梁静', 'TEST202402004', 2, 1, NOW(), NOW(), 0),
(1014, @school_id, @grade1_id, @class2_id, '郭伟', 'TEST202402005', 1, 1, NOW(), NOW(), 0),
-- 高二(1)班学生
(1015, @school_id, @grade2_id, @class3_id, '刘德华', 'TEST202403001', 1, 1, NOW(), NOW(), 0),
(1016, @school_id, @grade2_id, @class3_id, '梁朝伟', 'TEST202403002', 1, 1, NOW(), NOW(), 0),
(1017, @school_id, @grade2_id, @class3_id, '周星驰', 'TEST202403003', 1, 1, NOW(), NOW(), 0),
(1018, @school_id, @grade2_id, @class3_id, '成龙', 'TEST202403004', 1, 1, NOW(), NOW(), 0);

-- ============================================
-- 5. 考试数据
-- ============================================
INSERT INTO exam (id, school_id, name, code, type, academic_year, semester, grade_id, status, total_score, create_time, update_time, deleted) VALUES
(100, @school_id, '2024-2025学年第一学期期中考试', 'TEST_EXAM_MID_001', 1, '2024-2025', 1, @grade1_id, 4, 710, NOW(), NOW(), 0),
(101, @school_id, '2024-2025学年第一学期月考（一）', 'TEST_EXAM_MON_001', 3, '2024-2025', 1, @grade2_id, 4, 750, NOW(), NOW(), 0),
(102, @school_id, '2024-2025学年第一学期模拟考试', 'TEST_EXAM_SIM_001', 4, '2024-2025', 1, @grade3_id, 5, 750, NOW(), NOW(), 0);

SET @exam1_id = 100;
SET @exam2_id = 101;
SET @exam3_id = 102;

-- ============================================
-- 6. 考试班级关联
-- ============================================
INSERT INTO exam_class (id, exam_id, class_id, create_time, update_time, deleted) VALUES
(100, @exam1_id, @class1_id, NOW(), NOW(), 0),
(101, @exam1_id, @class2_id, NOW(), NOW(), 0),
(102, @exam2_id, @class3_id, NOW(), NOW(), 0),
(103, @exam3_id, @class3_id, NOW(), NOW(), 0);

-- ============================================
-- 7. 考试科目数据
-- ============================================
-- 考试1：语文、数学、英语、物理、化学
INSERT INTO exam_subject (id, exam_id, subject_name, subject_code, full_score, pass_score, excellent_score, duration, sort, status, create_time, update_time, deleted) VALUES
(100, @exam1_id, '语文', 'CH', 150, 90, 120, 150, 1, 1, NOW(), NOW(), 0),
(101, @exam1_id, '数学', 'MA', 150, 90, 120, 150, 2, 1, NOW(), NOW(), 0),
(102, @exam1_id, '英语', 'EN', 150, 90, 120, 120, 3, 1, NOW(), NOW(), 0),
(103, @exam1_id, '物理', 'PH', 100, 60, 80, 90, 4, 1, NOW(), NOW(), 0),
(104, @exam1_id, '化学', 'CH2', 110, 66, 88, 90, 5, 1, NOW(), NOW(), 0);

SET @subject1_ch = 100;
SET @subject1_ma = 101;
SET @subject1_en = 102;

-- 考试2：数学、英语、物理
INSERT INTO exam_subject (id, exam_id, subject_name, subject_code, full_score, pass_score, excellent_score, duration, sort, status, create_time, update_time, deleted) VALUES
(105, @exam2_id, '数学', 'MA2', 150, 90, 120, 150, 1, 1, NOW(), NOW(), 0),
(106, @exam2_id, '英语', 'EN2', 150, 90, 120, 120, 2, 1, NOW(), NOW(), 0),
(107, @exam2_id, '物理', 'PH2', 150, 90, 120, 120, 3, 1, NOW(), NOW(), 0);

SET @subject2_ma = 105;

-- 考试3：综合
INSERT INTO exam_subject (id, exam_id, subject_name, subject_code, full_score, pass_score, excellent_score, duration, sort, status, create_time, update_time, deleted) VALUES
(108, @exam3_id, '综合', 'COM', 750, 450, 600, 180, 1, 1, NOW(), NOW(), 0);

-- ============================================
-- 8. 试卷数据
-- ============================================
INSERT INTO paper (id, exam_subject_id, name, code, total_score, question_count, objective_count, subjective_count, status, create_time, update_time, deleted) VALUES
(100, @subject1_ma, '高一数学期中试卷', 'PAPER_MA_001', 150, 22, 12, 10, 1, NOW(), NOW(), 0),
(101, @subject1_en, '高一英语期中试卷', 'PAPER_EN_001', 150, 24, 20, 4, 1, NOW(), NOW(), 0),
(102, @subject2_ma, '高二数学月考试卷', 'PAPER_MA_002', 150, 20, 8, 12, 1, NOW(), NOW(), 0);

SET @paper1_ma = 100;
SET @paper1_en = 101;

-- ============================================
-- 9. 试卷题目数据 - 数学试卷
-- ============================================
-- 选择题（客观题）
INSERT INTO paper_question (id, paper_id, question_no, question_type, is_objective, score, correct_answer, sort, create_time, update_time, deleted) VALUES
(1000, @paper1_ma, '1', 1, 1, 5, 'A', 1, NOW(), NOW(), 0),
(1001, @paper1_ma, '2', 1, 1, 5, 'B', 2, NOW(), NOW(), 0),
(1002, @paper1_ma, '3', 1, 1, 5, 'C', 3, NOW(), NOW(), 0),
(1003, @paper1_ma, '4', 1, 1, 5, 'D', 4, NOW(), NOW(), 0),
(1004, @paper1_ma, '5', 1, 1, 5, 'A', 5, NOW(), NOW(), 0),
(1005, @paper1_ma, '6', 1, 1, 5, 'B', 6, NOW(), NOW(), 0),
(1006, @paper1_ma, '7', 1, 1, 5, 'C', 7, NOW(), NOW(), 0),
(1007, @paper1_ma, '8', 1, 1, 5, 'A', 8, NOW(), NOW(), 0),
(1008, @paper1_ma, '9', 2, 1, 5, 'AB', 9, NOW(), NOW(), 0),
(1009, @paper1_ma, '10', 2, 1, 5, 'ABC', 10, NOW(), NOW(), 0),
(1010, @paper1_ma, '11', 2, 1, 5, 'ABD', 11, NOW(), NOW(), 0),
(1011, @paper1_ma, '12', 2, 1, 5, 'ACD', 12, NOW(), NOW(), 0),
-- 填空题（客观题）
(1012, @paper1_ma, '13', 4, 1, 5, '3', 13, NOW(), NOW(), 0),
(1013, @paper1_ma, '14', 4, 1, 5, '2', 14, NOW(), NOW(), 0),
(1014, @paper1_ma, '15', 4, 1, 5, '5', 15, NOW(), NOW(), 0),
(1015, @paper1_ma, '16', 4, 1, 5, '7', 16, NOW(), NOW(), 0),
-- 解答题（主观题）
(1016, @paper1_ma, '17', 5, 0, 10, NULL, 17, NOW(), NOW(), 0),
(1017, @paper1_ma, '18', 5, 0, 10, NULL, 18, NOW(), NOW(), 0),
(1018, @paper1_ma, '19', 5, 0, 12, NULL, 19, NOW(), NOW(), 0),
(1019, @paper1_ma, '20', 6, 0, 14, NULL, 20, NOW(), NOW(), 0),
(1020, @paper1_ma, '21', 6, 0, 14, NULL, 21, NOW(), NOW(), 0),
(1021, @paper1_ma, '22', 6, 0, 15, NULL, 22, NOW(), NOW(), 0);

-- 设置双评和阈值
UPDATE paper_question SET enable_double_marking = 1, double_marking_threshold = 3
WHERE paper_id = @paper1_ma AND question_no IN ('20', '21', '22');

-- 英语试卷题目
INSERT INTO paper_question (id, paper_id, question_no, question_type, is_objective, score, correct_answer, sort, create_time, update_time, deleted) VALUES
(2000, @paper1_en, '1', 1, 1, 3, 'A', 1, NOW(), NOW(), 0),
(2001, @paper1_en, '2', 1, 1, 3, 'B', 2, NOW(), NOW(), 0),
(2002, @paper1_en, '3', 1, 1, 3, 'C', 3, NOW(), NOW(), 0),
(2003, @paper1_en, '4', 1, 1, 3, 'D', 4, NOW(), NOW(), 0),
(2004, @paper1_en, '5', 1, 1, 3, 'A', 5, NOW(), NOW(), 0),
(2005, @paper1_en, '6', 1, 1, 3, 'B', 6, NOW(), NOW(), 0),
(2006, @paper1_en, '7', 1, 1, 3, 'C', 7, NOW(), NOW(), 0),
(2007, @paper1_en, '8', 1, 1, 3, 'D', 8, NOW(), NOW(), 0),
(2008, @paper1_en, '9', 1, 1, 3, 'A', 9, NOW(), NOW(), 0),
(2009, @paper1_en, '10', 1, 1, 3, 'B', 10, NOW(), NOW(), 0),
(2010, @paper1_en, '11', 1, 1, 3, 'C', 11, NOW(), NOW(), 0),
(2011, @paper1_en, '12', 1, 1, 3, 'D', 12, NOW(), NOW(), 0),
(2012, @paper1_en, '13', 1, 1, 3, 'A', 13, NOW(), NOW(), 0),
(2013, @paper1_en, '14', 1, 1, 3, 'B', 14, NOW(), NOW(), 0),
(2014, @paper1_en, '15', 1, 1, 3, 'C', 15, NOW(), NOW(), 0),
(2015, @paper1_en, '16', 1, 1, 3, 'D', 16, NOW(), NOW(), 0),
(2016, @paper1_en, '17', 1, 1, 3, 'A', 17, NOW(), NOW(), 0),
(2017, @paper1_en, '18', 1, 1, 3, 'B', 18, NOW(), NOW(), 0),
(2018, @paper1_en, '19', 1, 1, 3, 'C', 19, NOW(), NOW(), 0),
(2019, @paper1_en, '20', 1, 1, 3, 'D', 20, NOW(), NOW(), 0),
(2020, @paper1_en, '21', 4, 1, 10, 'friendship', 21, NOW(), NOW(), 0),
(2021, @paper1_en, '22', 4, 1, 10, 'beautiful', 22, NOW(), NOW(), 0),
(2022, @paper1_en, '23', 7, 0, 25, NULL, 23, NOW(), NOW(), 0),
(2023, @paper1_en, '24', 7, 0, 25, NULL, 24, NOW(), NOW(), 0);

-- 作文启用双评
UPDATE paper_question SET enable_double_marking = 1, double_marking_threshold = 5
WHERE paper_id = @paper1_en AND question_no IN ('23', '24');

-- ============================================
-- 10. 答题卡数据
-- ============================================
SET @student1_id = 1000;
SET @student2_id = 1001;
SET @student3_id = 1002;
SET @student4_id = 1003;
SET @student5_id = 1004;

-- 插入答题卡（不同状态，用于测试流程）
INSERT INTO answer_sheet (id, exam_id, exam_subject_id, student_id, student_number, seat_number, image_count, status, objective_score, subjective_score, total_score, create_time, update_time, deleted) VALUES
-- 状态2：待阅卷
(1000, @exam1_id, @subject1_ma, @student1_id, 'TEST202401001', '01', 2, 2, 60, 0, 60, NOW(), NOW(), 0),
(1001, @exam1_id, @subject1_ma, @student2_id, 'TEST202401002', '02', 2, 2, 55, 0, 55, NOW(), NOW(), 0),
-- 状态3：阅卷中
(1002, @exam1_id, @subject1_ma, @student3_id, 'TEST202401003', '03', 2, 3, 45, 20, 65, NOW(), NOW(), 0),
-- 状态4：已完成
(1003, @exam1_id, @subject1_ma, @student4_id, 'TEST202401004', '04', 2, 4, 70, 35, 105, NOW(), NOW(), 0),
-- 状态5：识别异常
(1004, @exam1_id, @subject1_ma, @student5_id, 'TEST202401005', '05', 2, 5, 0, 0, 0, NOW(), NOW(), 0),
-- 英语答题卡
(1005, @exam1_id, @subject1_en, @student1_id, 'TEST202401001', '01', 2, 2, 54, 0, 54, NOW(), NOW(), 0),
(1006, @exam1_id, @subject1_en, @student2_id, 'TEST202401002', '02', 2, 4, 60, 40, 100, NOW(), NOW(), 0);

SET @as1_ma = 1000;
SET @as2_ma = 1001;
SET @as3_ma = 1002;
SET @as4_ma = 1003;
SET @as5_ma = 1004;
SET @as1_en = 1005;

-- ============================================
-- 11. 答题卡明细数据
-- ============================================

-- 张三数学答题卡明细（状态2-待阅卷）
INSERT INTO answer_sheet_detail (id, answer_sheet_id, question_id, student_answer, score, status, create_time, update_time, deleted) VALUES
(10000, @as1_ma, 1000, 'A', 5, 1, NOW(), NOW(), 0),
(10001, @as1_ma, 1001, 'B', 5, 1, NOW(), NOW(), 0),
(10002, @as1_ma, 1002, 'C', 5, 1, NOW(), NOW(), 0),
(10003, @as1_ma, 1003, 'D', 0, 1, NOW(), NOW(), 0),
(10004, @as1_ma, 1004, 'A', 5, 1, NOW(), NOW(), 0),
(10005, @as1_ma, 1005, 'B', 5, 1, NOW(), NOW(), 0),
(10006, @as1_ma, 1006, 'C', 5, 1, NOW(), NOW(), 0),
(10007, @as1_ma, 1007, 'A', 0, 1, NOW(), NOW(), 0),
(10008, @as1_ma, 1008, 'AB', 5, 1, NOW(), NOW(), 0),
(10009, @as1_ma, 1009, 'ABC', 5, 1, NOW(), NOW(), 0),
(10010, @as1_ma, 1010, 'AB', 5, 1, NOW(), NOW(), 0),
(10011, @as1_ma, 1011, 'AC', 0, 1, NOW(), NOW(), 0),
(10012, @as1_ma, 1012, '3', 5, 1, NOW(), NOW(), 0),
(10013, @as1_ma, 1013, '2', 0, 1, NOW(), NOW(), 0),
(10014, @as1_ma, 1014, '5', 5, 1, NOW(), NOW(), 0),
(10015, @as1_ma, 1015, '7', 5, 1, NOW(), NOW(), 0),
(10016, @as1_ma, 1016, NULL, 0, 0, NOW(), NOW(), 0),
(10017, @as1_ma, 1017, NULL, 0, 0, NOW(), NOW(), 0),
(10018, @as1_ma, 1018, NULL, 0, 0, NOW(), NOW(), 0),
(10019, @as1_ma, 1019, NULL, 0, 0, NOW(), NOW(), 0),
(10020, @as1_ma, 1020, NULL, 0, 0, NOW(), NOW(), 0),
(10021, @as1_ma, 1021, NULL, 0, 0, NOW(), NOW(), 0);

-- 赵六数学答题卡明细（状态4-已完成）
INSERT INTO answer_sheet_detail (id, answer_sheet_id, question_id, student_answer, score, status, create_time, update_time, deleted) VALUES
(10100, @as4_ma, 1000, 'A', 5, 1, NOW(), NOW(), 0),
(10101, @as4_ma, 1001, 'B', 5, 1, NOW(), NOW(), 0),
(10102, @as4_ma, 1002, 'C', 5, 1, NOW(), NOW(), 0),
(10103, @as4_ma, 1003, 'D', 5, 1, NOW(), NOW(), 0),
(10104, @as4_ma, 1004, 'A', 5, 1, NOW(), NOW(), 0),
(10105, @as4_ma, 1005, 'B', 5, 1, NOW(), NOW(), 0),
(10106, @as4_ma, 1006, 'C', 5, 1, NOW(), NOW(), 0),
(10107, @as4_ma, 1007, 'A', 5, 1, NOW(), NOW(), 0),
(10108, @as4_ma, 1008, 'AB', 5, 1, NOW(), NOW(), 0),
(10109, @as4_ma, 1009, 'ABC', 5, 1, NOW(), NOW(), 0),
(10110, @as4_ma, 1010, 'ABD', 5, 1, NOW(), NOW(), 0),
(10111, @as4_ma, 1011, 'ACD', 5, 1, NOW(), NOW(), 0),
(10112, @as4_ma, 1012, '3', 5, 1, NOW(), NOW(), 0),
(10113, @as4_ma, 1013, '2', 5, 1, NOW(), NOW(), 0),
(10114, @as4_ma, 1014, '5', 5, 1, NOW(), NOW(), 0),
(10115, @as4_ma, 1015, '7', 5, 1, NOW(), NOW(), 0),
(10116, @as4_ma, 1016, NULL, 8, 1, NOW(), NOW(), 0),
(10117, @as4_ma, 1017, NULL, 9, 1, NOW(), NOW(), 0),
(10118, @as4_ma, 1018, NULL, 10, 1, NOW(), NOW(), 0),
(10119, @as4_ma, 1019, NULL, 12, 1, NOW(), NOW(), 0),
(10120, @as4_ma, 1020, NULL, 14, 1, NOW(), NOW(), 0),
(10121, @as4_ma, 1021, NULL, 12, 1, NOW(), NOW(), 0);

-- ============================================
-- 12. 阅卷任务数据
-- ============================================
INSERT INTO marking_task (id, exam_id, exam_subject_id, question_id, name, task_type, total_count, completed_count, pending_count, enable_double_marking, double_marking_threshold, status, create_time, update_time, deleted) VALUES
(100, @exam1_id, @subject1_ma, 1019, '第20题阅卷任务', 2, 5, 2, 3, 1, 3, 1, NOW(), NOW(), 0),
(101, @exam1_id, @subject1_ma, 1020, '第21题阅卷任务', 2, 5, 1, 4, 1, 3, 1, NOW(), NOW(), 0),
(102, @exam1_id, @subject1_ma, 1021, '第22题阅卷任务', 2, 5, 0, 5, 1, 3, 0, NOW(), NOW(), 0);

-- ============================================
-- 查询验证
-- ============================================
SELECT '=== 测试数据创建完成 ===' AS info;
SELECT '=== 考试信息 ===' AS info;
SELECT e.id, e.name, e.code, e.status FROM exam e WHERE e.code LIKE 'TEST%';

SELECT '=== 科目信息 ===' AS info;
SELECT es.id, es.exam_id, es.subject_name, es.full_score FROM exam_subject es WHERE es.exam_id IN (SELECT id FROM exam WHERE code LIKE 'TEST%');

SELECT '=== 试卷题目 ===' AS info;
SELECT pq.id, pq.question_no, pq.question_type, pq.is_objective, pq.score FROM paper_question pq WHERE pq.paper_id = @paper1_ma ORDER BY pq.sort LIMIT 10;

SELECT '=== 答题卡状态 ===' AS info;
SELECT a.id, a.student_number, a.status, a.objective_score, a.subjective_score, a.total_score FROM answer_sheet a WHERE a.exam_id = @exam1_id AND a.exam_subject_id = @subject1_ma;

SELECT '=== 测试数据统计 ===' AS info;
SELECT
    (SELECT COUNT(*) FROM school WHERE school_name LIKE '测试学校%') AS school_count,
    (SELECT COUNT(*) FROM grade WHERE grade_name LIKE '测试年级%') AS grade_count,
    (SELECT COUNT(*) FROM student WHERE student_code LIKE 'TEST%') AS student_count,
    (SELECT COUNT(*) FROM exam WHERE code LIKE 'TEST%') AS exam_count,
    (SELECT COUNT(*) FROM exam_subject WHERE exam_id IN (SELECT id FROM exam WHERE code LIKE 'TEST%')) AS subject_count,
    (SELECT COUNT(*) FROM answer_sheet WHERE exam_id IN (SELECT id FROM exam WHERE code LIKE 'TEST%')) AS answer_sheet_count;
