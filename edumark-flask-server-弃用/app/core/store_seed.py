from __future__ import annotations


def build_seed_data(now: str) -> dict[str, list[dict]]:
    return {
        "roles": [
            {"id": 1, "roleName": "超级管理员", "roleCode": "SUPER_ADMIN", "status": 1, "description": "拥有系统所有权限", "createTime": now},
            {"id": 7, "roleName": "家长", "roleCode": "PARENT", "status": 1, "description": "家长端", "createTime": now},
            {"id": 8, "roleName": "学生", "roleCode": "STUDENT", "status": 1, "description": "学生端", "createTime": now},
        ],
        "permissions": [
            {"id": 1, "permissionName": "系统管理", "permissionCode": "system:menu:list", "status": 1, "parentId": 0},
            {"id": 2, "permissionName": "用户新增", "permissionCode": "system:menu:add", "status": 1, "parentId": 1},
            {"id": 3, "permissionName": "用户修改", "permissionCode": "system:menu:edit", "status": 1, "parentId": 1},
            {"id": 4, "permissionName": "用户删除", "permissionCode": "system:menu:delete", "status": 1, "parentId": 1},
        ],
        "schools": [
            {"id": 1, "schoolName": "示范中学", "schoolCode": "SFZX001", "status": 1, "province": "北京市", "city": "北京市", "district": "海淀区", "contactPerson": "张校长", "contactPhone": "13900000001", "deleted": 0}
        ],
        "grades": [
            {"id": 1, "schoolId": 1, "gradeName": "高一", "gradeCode": "G1", "status": 1, "sort": 1},
            {"id": 2, "schoolId": 1, "gradeName": "高二", "gradeCode": "G2", "status": 1, "sort": 2},
        ],
        "classes": [
            {"id": 1, "schoolId": 1, "gradeId": 1, "className": "高一(1)班", "classCode": "G1C1", "studentCount": 2, "status": 1, "sort": 1},
        ],
        "students": [
            {"id": 1, "schoolId": 1, "gradeId": 1, "classId": 1, "studentName": "测试学生", "studentCode": "S20260001", "phone": "13800000111", "bindCode": "ABC123", "status": 1},
            {"id": 2, "schoolId": 1, "gradeId": 1, "classId": 1, "studentName": "李同学", "studentCode": "S20260002", "phone": "13800000112", "bindCode": "XYZ789", "status": 1},
        ],
        "parents": [
            {"id": 1, "parentName": "测试家长", "phone": "13900000099", "relation": "父亲", "status": 1}
        ],
        "parent_student_binds": [
            {"id": 1, "parentId": 1, "studentId": 1, "relation": "父亲", "status": 1, "createTime": now}
        ],
        "teachers": [
            {"id": 1, "schoolId": 1, "teacherName": "王老师", "teacherCode": "T001", "phone": "13700000001", "subject": "数学", "status": 1}
        ],
        "users": [
            {"id": 1, "username": "admin", "password": "admin123", "realName": "超级管理员", "phone": "13800000000", "userType": 1, "status": 1, "schoolId": 1, "roleId": 1, "roleCode": "SUPER_ADMIN", "roleName": "超级管理员"},
            {"id": 10001, "username": "parent_test", "password": "admin123", "realName": "测试家长", "phone": "13900000099", "userType": 2, "status": 1, "schoolId": 1, "roleId": 7, "roleCode": "PARENT", "roleName": "家长", "parentId": 1},
            {"id": 10002, "username": "student_test", "password": "admin123", "realName": "测试学生", "phone": "13800000111", "userType": 3, "status": 1, "schoolId": 1, "roleId": 8, "roleCode": "STUDENT", "roleName": "学生", "studentId": 1},
        ],
        "knowledge_points": [
            {"id": 1, "schoolId": 1, "subjectName": "数学", "name": "函数", "code": "MATH-FUNC", "parentId": 0, "level": 1, "status": 1}
        ],
        "exams": [
            {"id": 1, "schoolId": 1, "name": "2026春季月考", "code": "EX2026001", "type": 1, "typeName": "月考", "academicYear": "2025-2026", "semester": 2, "gradeId": 1, "gradeName": "高一", "status": 5, "statusName": "已发布", "totalScore": 300, "subjectCount": 3, "studentCount": 2, "startTime": "2026-03-20 08:00:00", "endTime": "2026-03-20 17:00:00"},
            {"id": 2, "schoolId": 1, "name": "2026期中考试", "code": "EX2026002", "type": 2, "typeName": "期中", "academicYear": "2025-2026", "semester": 2, "gradeId": 1, "gradeName": "高一", "status": 5, "statusName": "已发布", "totalScore": 300, "subjectCount": 3, "studentCount": 2, "startTime": "2026-04-02 08:00:00", "endTime": "2026-04-02 17:00:00"},
        ],
        "exam_subjects": [
            {"id": 1, "examId": 1, "subjectName": "数学", "subjectCode": "math", "fullScore": 100, "passScore": 60, "excellentScore": 85, "duration": 120, "questionCount": 25, "paperId": 1, "sort": 1, "status": 1, "startTime": "2026-03-20 08:00:00", "endTime": "2026-03-20 10:00:00"},
            {"id": 2, "examId": 1, "subjectName": "语文", "subjectCode": "chinese", "fullScore": 100, "passScore": 60, "excellentScore": 85, "duration": 150, "questionCount": 18, "paperId": 2, "sort": 2, "status": 1, "startTime": "2026-03-20 10:30:00", "endTime": "2026-03-20 13:00:00"},
            {"id": 3, "examId": 1, "subjectName": "英语", "subjectCode": "english", "fullScore": 100, "passScore": 60, "excellentScore": 85, "duration": 120, "questionCount": 30, "paperId": 3, "sort": 3, "status": 1, "startTime": "2026-03-20 15:00:00", "endTime": "2026-03-20 17:00:00"},
            {"id": 4, "examId": 2, "subjectName": "数学", "subjectCode": "math", "fullScore": 100, "passScore": 60, "excellentScore": 85, "duration": 120, "questionCount": 25, "paperId": 1, "sort": 1, "status": 1, "startTime": "2026-04-02 08:00:00", "endTime": "2026-04-02 10:00:00"},
            {"id": 5, "examId": 2, "subjectName": "语文", "subjectCode": "chinese", "fullScore": 100, "passScore": 60, "excellentScore": 85, "duration": 150, "questionCount": 18, "paperId": 2, "sort": 2, "status": 1, "startTime": "2026-04-02 10:30:00", "endTime": "2026-04-02 13:00:00"},
            {"id": 6, "examId": 2, "subjectName": "英语", "subjectCode": "english", "fullScore": 100, "passScore": 60, "excellentScore": 85, "duration": 120, "questionCount": 30, "paperId": 3, "sort": 3, "status": 1, "startTime": "2026-04-02 15:00:00", "endTime": "2026-04-02 17:00:00"},
        ],
        "exam_scores": [
            {"id": 1, "examId": 1, "examName": "2026春季月考", "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "totalScore": 245, "subjectCount": 3, "classRank": 2, "gradeRank": 12, "updateTime": "2026-03-22 10:00:00"},
            {"id": 2, "examId": 2, "examName": "2026期中考试", "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "totalScore": 268, "subjectCount": 3, "classRank": 1, "gradeRank": 5, "updateTime": "2026-04-08 10:00:00"},
        ],
        "subject_scores": [
            {"id": 1, "examId": 1, "examSubjectId": 1, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "subjectName": "数学", "fullScore": 100, "score": 78, "objectiveScore": 45, "subjectiveScore": 33, "classRank": 2, "gradeRank": 14},
            {"id": 2, "examId": 1, "examSubjectId": 2, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "subjectName": "语文", "fullScore": 100, "score": 82, "objectiveScore": 35, "subjectiveScore": 47, "classRank": 2, "gradeRank": 11},
            {"id": 3, "examId": 1, "examSubjectId": 3, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "subjectName": "英语", "fullScore": 100, "score": 85, "objectiveScore": 50, "subjectiveScore": 35, "classRank": 1, "gradeRank": 8},
            {"id": 4, "examId": 2, "examSubjectId": 4, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "subjectName": "数学", "fullScore": 100, "score": 91, "objectiveScore": 54, "subjectiveScore": 37, "classRank": 1, "gradeRank": 6},
            {"id": 5, "examId": 2, "examSubjectId": 5, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "subjectName": "语文", "fullScore": 100, "score": 84, "objectiveScore": 34, "subjectiveScore": 50, "classRank": 2, "gradeRank": 9},
            {"id": 6, "examId": 2, "examSubjectId": 6, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "classId": 1, "className": "高一(1)班", "subjectName": "英语", "fullScore": 100, "score": 93, "objectiveScore": 56, "subjectiveScore": 37, "classRank": 1, "gradeRank": 3},
        ],
        "answer_sheets": [
            {"id": 1, "examId": 2, "examSubjectId": 4, "subjectName": "数学", "studentId": 1, "studentNumber": "S20260001", "seatNumber": "01", "objectiveScore": 54, "subjectiveScore": 37, "totalScore": 91, "status": 2, "statusName": "已识别", "remark": "", "createTime": now},
            {"id": 2, "examId": 2, "examSubjectId": 5, "subjectName": "语文", "studentId": 1, "studentNumber": "S20260001", "seatNumber": "01", "objectiveScore": 34, "subjectiveScore": 50, "totalScore": 84, "status": 3, "statusName": "已阅卷", "remark": "", "createTime": now},
        ],
        "answer_sheet_images": [
            {"id": 1, "answerSheetId": 1, "pageNo": 1, "pageNum": 1, "imagePath": "answer-sheet/1/page-1.png", "imageUrl": "https://via.placeholder.com/800x1200?text=Math+Page+1", "originalName": "math-1.png", "fileSize": 102400, "width": 800, "height": 1200, "imageType": 1, "sort": 1},
            {"id": 2, "answerSheetId": 2, "pageNo": 1, "pageNum": 1, "imagePath": "answer-sheet/2/page-1.png", "imageUrl": "https://via.placeholder.com/800x1200?text=Chinese+Page+1", "originalName": "chinese-1.png", "fileSize": 112400, "width": 800, "height": 1200, "imageType": 1, "sort": 1},
        ],
        "answer_sheet_details": [
            {"id": 1, "answerSheetId": 1, "questionId": 101, "questionNo": "1", "questionType": 1, "questionTypeName": "选择题", "isObjective": 1, "fullScore": 5, "correctAnswer": "A", "studentAnswer": "A", "score": 5, "status": 1, "statusName": "已完成", "regionRole": "choice_block", "regionRoleName": "选择题区", "cropMode": "single-question", "pageNo": 1, "optionCount": 4, "anomalyFlag": False, "anomalyReason": "", "previewAvailable": True},
            {"id": 2, "answerSheetId": 1, "questionId": 102, "questionNo": "2", "questionType": 4, "questionTypeName": "填空题", "isObjective": 0, "fullScore": 10, "correctAnswer": "2x+1", "studentAnswer": "2x+1", "score": 10, "status": 2, "statusName": "已复核", "regionRole": "subjective_crop", "regionRoleName": "主观题切图", "cropMode": "single-question", "pageNo": 1, "optionCount": 0, "anomalyFlag": False, "anomalyReason": "", "previewAvailable": True},
        ],
        "templates": [
            {"id": 1, "paperId": 1, "examId": 2, "paperName": "高一数学试卷", "examName": "2026期中考试", "subjectName": "数学", "name": "高一数学答题卡模板", "pageSize": "A4", "orientation": 1, "columns": 1, "marginTop": 10, "marginBottom": 10, "marginLeft": 10, "marginRight": 10, "headerConfig": {"title": "高一数学答题卡", "showTitle": True}, "studentInfoConfig": {"showStudentId": True, "showName": True, "showClass": True}, "status": 1, "pdfObjectName": "template/1/template.pdf", "pdfUrl": "/downloads/template-1.pdf", "templateImagePath": "template/1/image.png", "templateImageUrl": "https://via.placeholder.com/1200x1600?text=Template+1", "cornerConfig": {"topLeft": {"x": 12, "y": 15}, "topRight": {"x": 1188, "y": 18}, "bottomLeft": {"x": 10, "y": 1580}, "bottomRight": {"x": 1185, "y": 1582}, "corrected": True, "angle": 0.2}, "regions": [{"id": 11, "templateId": 1, "regionType": 1, "regionTypeName": "考生信息", "regionName": "考生信息区", "pageNo": 1, "sortOrder": 1, "config": {"regionRole": "student_id"}}, {"id": 12, "templateId": 1, "regionType": 2, "regionTypeName": "选择题", "regionName": "选择题区域", "pageNo": 1, "sortOrder": 2, "questionStart": 1, "questionEnd": 20, "config": {"regionRole": "choice_block", "optionCount": 4, "questionsPerRow": 5, "layoutDirection": "row", "bubbleStyle": "circle"}}, {"id": 13, "templateId": 1, "regionType": 3, "regionTypeName": "主观题", "regionName": "填空题区域", "pageNo": 1, "sortOrder": 3, "questionStart": 21, "questionEnd": 25, "config": {"regionRole": "subjective_crop", "cropMode": "single-question", "enableAiMarking": True, "aiReferenceAnswer": "按标准答案给分"}}]},
        ],
        "marking_tasks": [
            {"id": 1, "examSubjectId": 4, "taskName": "数学主观题阅卷", "questionId": 102, "questionNo": 2, "teacherId": 1, "teacherName": "王老师", "status": 2, "statusName": "进行中", "assignCount": 20, "completedCount": 8, "progress": 40, "taskType": 1, "enableDoubleMarking": 1, "doubleMarkingThreshold": 3, "accessCode": "AC0001", "secondAccessCode": "AC0001B", "accessCodeExpireTime": now, "startTime": now, "endTime": "", "remark": "数学主观题统一阅卷", "createTime": now},
        ],
        "marking_records": [
            {"id": 1, "taskId": 1, "answerSheetId": 1, "questionId": 102, "studentId": 1, "studentName": "测试学生", "studentNumber": "S20260001", "questionNo": 2, "teacherId": 1, "teacherName": "王老师", "markingRole": 1, "markingRoleName": "一评", "fullScore": 10, "score": 10, "comment": "步骤完整", "markingTime": now, "status": 1, "statusName": "已完成", "imageUrl": "https://via.placeholder.com/800x400?text=Marking+1", "answerImages": ["https://via.placeholder.com/800x400?text=Marking+1"], "answerImageUrl": "https://via.placeholder.com/800x400?text=Marking+1", "originalImageUrl": "https://via.placeholder.com/800x400?text=Marking+1", "createTime": now},
        ],
        "marking_arbitrations": [
            {"id": 1, "taskId": 1, "answerSheetId": 1, "questionId": 102, "questionNo": "2", "studentId": 1, "studentName": "测试学生", "firstScore": 8, "firstTeacherId": 1, "firstTeacherName": "王老师", "secondScore": 10, "secondTeacherId": 2, "secondTeacherName": "李老师", "scoreDiff": 2, "arbitrationTeacherId": 3, "arbitrationTeacherName": "张老师", "arbitrationScore": 9, "arbitrationTime": now, "arbitrationComment": "", "status": 1, "statusName": "待仲裁", "answerImages": ["https://via.placeholder.com/800x400?text=Arbitration+1"], "answerImageUrl": "https://via.placeholder.com/800x400?text=Arbitration+1", "originalImageUrl": "https://via.placeholder.com/800x400?text=Arbitration+1", "createTime": now},
        ],
        "ai_providers": [
            {"id": 1, "providerName": "OpenAI Default", "protocol": "openai-responses", "baseUrl": "https://api.openai.com/v1", "apiKey": "", "model": "gpt-4.1-mini", "enabled": 0, "isDefault": 0, "timeoutMs": 30000, "maxTokens": 2048, "priority": 0, "remark": ""},
        ],
        "ai_records": [
            {"id": 1, "answerSheetId": 1, "questionNo": 2, "providerName": "OpenAI Default", "protocol": "openai-responses", "model": "gpt-4.1-mini", "referenceAnswer": "2x+1", "recognizedText": "2x+1", "suggestedScore": 10, "confidence": 0.97, "judgeReason": "答案完全正确", "status": 1, "errorMessage": "", "rawResponse": "{\"score\":10}", "createTime": now},
        ],
    }
