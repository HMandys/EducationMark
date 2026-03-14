-- =====================================================
-- EduMark 智能阅卷与家长查分平台 - 初始化数据脚本
-- =====================================================

USE edumark;

-- =====================================================
-- 一、初始化角色数据
-- =====================================================

INSERT INTO sys_role (id, role_name, role_code, description, sort, status, data_scope, deleted) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有系统所有权限', 1, 1, 1, 0),
(2, '学校管理员', 'SCHOOL_ADMIN', '管理本校所有数据', 2, 1, 2, 0),
(3, '教务主任', 'DIRECTOR', '组织考试、发布成绩、查看统计', 3, 1, 2, 0),
(4, '阅卷组长', 'MARKING_LEADER', '分配阅卷任务、监控阅卷进度', 4, 1, 2, 0),
(5, '任课教师', 'TEACHER', '执行阅卷任务', 5, 1, 4, 0),
(6, '班主任', 'HEAD_TEACHER', '查看本班成绩、学情分析', 6, 1, 3, 0),
(7, '家长', 'PARENT', '查询孩子成绩、查看AI分析', 7, 1, 4, 0),
(8, '学生', 'STUDENT', '查询个人成绩、答题卡、学习建议', 8, 1, 4, 0);

-- =====================================================
-- 二、初始化权限数据
-- =====================================================

-- 一级菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(1, 0, '系统管理', NULL, 1, '/system', NULL, 'Setting', 1, 1, 1, 0),
(2, 0, '学校管理', NULL, 1, '/school', NULL, 'School', 2, 1, 1, 0),
(3, 0, '考试管理', NULL, 1, '/exam', NULL, 'Document', 3, 1, 1, 0),
(4, 0, '阅卷管理', NULL, 1, '/marking', NULL, 'Edit', 4, 1, 1, 0),
(5, 0, '成绩管理', NULL, 1, '/score', NULL, 'DataAnalysis', 5, 1, 1, 0),
(6, 0, 'AI分析', NULL, 1, '/ai', NULL, 'Cpu', 6, 1, 1, 0);

-- 系统管理子菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(101, 1, '用户管理', 'system:user:list', 2, 'user', 'system/user/index', 'User', 1, 1, 1, 0),
(102, 1, '角色管理', 'system:role:list', 2, 'role', 'system/role/index', 'UserFilled', 2, 1, 1, 0),
(103, 1, '权限管理', 'system:permission:list', 2, 'permission', 'system/permission/index', 'Lock', 3, 1, 1, 0),
(104, 1, '操作日志', 'system:log:list', 2, 'log', 'system/log/index', 'Tickets', 4, 1, 1, 0);

-- 用户管理按钮权限
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(1011, 101, '用户查询', 'system:user:query', 3, NULL, NULL, NULL, 1, 1, 1, 0),
(1012, 101, '用户新增', 'system:user:add', 3, NULL, NULL, NULL, 2, 1, 1, 0),
(1013, 101, '用户修改', 'system:user:edit', 3, NULL, NULL, NULL, 3, 1, 1, 0),
(1014, 101, '用户删除', 'system:user:delete', 3, NULL, NULL, NULL, 4, 1, 1, 0),
(1015, 101, '重置密码', 'system:user:resetPwd', 3, NULL, NULL, NULL, 5, 1, 1, 0);

-- 学校管理子菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(201, 2, '学校列表', 'school:school:list', 2, 'list', 'school/school/index', 'OfficeBuilding', 1, 1, 1, 0),
(202, 2, '年级管理', 'school:grade:list', 2, 'grade', 'school/grade/index', 'Collection', 2, 1, 1, 0),
(203, 2, '班级管理', 'school:class:list', 2, 'class', 'school/class/index', 'Memo', 3, 1, 1, 0),
(204, 2, '教师管理', 'school:teacher:list', 2, 'teacher', 'school/teacher/index', 'Avatar', 4, 1, 1, 0),
(205, 2, '学生管理', 'school:student:list', 2, 'student', 'school/student/index', 'User', 5, 1, 1, 0),
(206, 2, '家长管理', 'school:parent:list', 2, 'parent', 'school/parent/index', 'UserFilled', 6, 1, 1, 0);

-- 考试管理子菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(301, 3, '考试列表', 'exam:exam:list', 2, 'list', 'exam/exam/index', 'Document', 1, 1, 1, 0),
(302, 3, '科目管理', 'exam:subject:list', 2, 'subject', 'exam/subject/index', 'Reading', 2, 1, 1, 0),
(303, 3, '答题卡管理', 'exam:answer:list', 2, 'answer', 'exam/answer/index', 'PictureFilled', 3, 1, 1, 0);

-- 阅卷管理子菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(401, 4, '阅卷任务', 'marking:task:list', 2, 'task', 'marking/task/index', 'List', 1, 1, 1, 0),
(402, 4, '阅卷工作台', 'marking:work:view', 2, 'work', 'marking/work/index', 'Edit', 2, 1, 1, 0),
(403, 4, '阅卷进度', 'marking:progress:view', 2, 'progress', 'marking/progress/index', 'DataLine', 3, 1, 1, 0);

-- 成绩管理子菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(501, 5, '成绩汇总', 'score:summary:view', 2, 'summary', 'score/summary/index', 'DataAnalysis', 1, 1, 1, 0),
(502, 5, '成绩统计', 'score:stats:view', 2, 'stats', 'score/stats/index', 'TrendCharts', 2, 1, 1, 0),
(503, 5, '成绩发布', 'score:publish:manage', 2, 'publish', 'score/publish/index', 'Promotion', 3, 1, 1, 0);

-- AI分析子菜单
INSERT INTO sys_permission (id, parent_id, permission_name, permission_code, permission_type, path, component, icon, sort, visible, status, deleted) VALUES
(601, 6, '学生报告', 'ai:student:view', 2, 'student', 'ai/student/index', 'Document', 1, 1, 1, 0),
(602, 6, '班级报告', 'ai:class:view', 2, 'class', 'ai/class/index', 'DocumentCopy', 2, 1, 1, 0),
(603, 6, '分析任务', 'ai:task:list', 2, 'task', 'ai/task/index', 'List', 3, 1, 1, 0);

-- =====================================================
-- 三、为超级管理员分配所有权限
-- =====================================================

INSERT INTO sys_role_permission (id, role_id, permission_id)
SELECT @rownum := @rownum + 1, 1, id
FROM sys_permission, (SELECT @rownum := 0) r
WHERE deleted = 0;

-- =====================================================
-- 四、初始化超级管理员账号
-- 密码: admin123 (BCrypt加密)
-- =====================================================

INSERT INTO sys_user (id, username, password, real_name, phone, email, gender, user_type, status, remark, deleted) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800000000', 'admin@edumark.com', 1, 1, 1, '系统超级管理员', 0);

-- 为超级管理员分配角色
INSERT INTO sys_user_role (id, user_id, role_id) VALUES (1, 1, 1);

-- =====================================================
-- 五、初始化字典数据
-- =====================================================

-- 性别
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(1, 'gender', '0', '未知', '0', 0, 1, 0),
(2, 'gender', '1', '男', '1', 1, 1, 0),
(3, 'gender', '2', '女', '2', 2, 1, 0);

-- 用户状态
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(11, 'user_status', '0', '禁用', '0', 0, 1, 0),
(12, 'user_status', '1', '正常', '1', 1, 1, 0);

-- 考试状态
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(21, 'exam_status', '0', '草稿', '0', 0, 1, 0),
(22, 'exam_status', '1', '待阅卷', '1', 1, 1, 0),
(23, 'exam_status', '2', '阅卷中', '2', 2, 1, 0),
(24, 'exam_status', '3', '已完成', '3', 3, 1, 0),
(25, 'exam_status', '4', '已发布', '4', 4, 1, 0);

-- 阅卷任务状态
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(31, 'marking_status', '0', '待分配', '0', 0, 1, 0),
(32, 'marking_status', '1', '待领取', '1', 1, 1, 0),
(33, 'marking_status', '2', '阅卷中', '2', 2, 1, 0),
(34, 'marking_status', '3', '已完成', '3', 3, 1, 0),
(35, 'marking_status', '4', '待仲裁', '4', 4, 1, 0);

-- 题目类型
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(41, 'question_type', 'SINGLE', '单选题', 'SINGLE', 1, 1, 0),
(42, 'question_type', 'MULTIPLE', '多选题', 'MULTIPLE', 2, 1, 0),
(43, 'question_type', 'JUDGE', '判断题', 'JUDGE', 3, 1, 0),
(44, 'question_type', 'FILL', '填空题', 'FILL', 4, 1, 0),
(45, 'question_type', 'SHORT', '简答题', 'SHORT', 5, 1, 0),
(46, 'question_type', 'ESSAY', '作文', 'ESSAY', 6, 1, 0);

-- 成绩发布状态
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(51, 'publish_status', '0', '未发布', '0', 0, 1, 0),
(52, 'publish_status', '1', '已发布', '1', 1, 1, 0);

-- AI任务状态
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(61, 'ai_task_status', '0', '待执行', '0', 0, 1, 0),
(62, 'ai_task_status', '1', '执行中', '1', 1, 1, 0),
(63, 'ai_task_status', '2', '已完成', '2', 2, 1, 0),
(64, 'ai_task_status', '3', '执行失败', '3', 3, 1, 0);

-- =====================================================
-- 六、初始化测试学校数据
-- =====================================================

INSERT INTO school (id, school_name, school_code, province, city, district, contact_person, contact_phone, status, deleted) VALUES
(1, '示范中学', 'SFZX001', '北京市', '北京市', '海淀区', '张校长', '13900000001', 1, 0);

-- 年级
INSERT INTO grade (id, school_id, grade_name, grade_code, sort, status, deleted) VALUES
(1, 1, '高一', 'G1', 1, 1, 0),
(2, 1, '高二', 'G2', 2, 1, 0),
(3, 1, '高三', 'G3', 3, 1, 0);

-- 班级
INSERT INTO class_info (id, school_id, grade_id, class_name, class_code, student_count, sort, status, deleted) VALUES
(1, 1, 1, '高一(1)班', 'G1C1', 45, 1, 1, 0),
(2, 1, 1, '高一(2)班', 'G1C2', 44, 2, 1, 0),
(3, 1, 2, '高二(1)班', 'G2C1', 42, 1, 1, 0),
(4, 1, 2, '高二(2)班', 'G2C2', 43, 2, 1, 0);

COMMIT;
