USE edumark;

DELETE FROM sys_dict WHERE dict_type = 'exam_status';

INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, dict_value, sort, status, deleted) VALUES
(21, 'exam_status', '0', '草稿', '0', 0, 1, 0),
(22, 'exam_status', '1', '待考试', '1', 1, 1, 0),
(23, 'exam_status', '2', '考试中', '2', 2, 1, 0),
(24, 'exam_status', '3', '阅卷中', '3', 3, 1, 0),
(25, 'exam_status', '4', '已完成', '4', 4, 1, 0),
(26, 'exam_status', '5', '已发布', '5', 5, 1, 0);
