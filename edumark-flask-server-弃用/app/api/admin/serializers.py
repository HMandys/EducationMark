from __future__ import annotations

from app.core.store import store


def _school_name(school_id):
    school = store.find_by_id("schools", school_id)
    return school.get("schoolName") if school else None


def _grade_name(grade_id):
    grade = store.find_by_id("grades", grade_id)
    return grade.get("gradeName") if grade else None


def _class_name(class_id):
    class_info = store.find_by_id("classes", class_id)
    return class_info.get("className") if class_info else None


def _subject_score_payload(item: dict):
    student = store.find_by_id("students", item.get("studentId")) or {}
    return {
        **item,
        "studentName": item.get("studentName") or student.get("studentName"),
        "studentNumber": item.get("studentNumber") or student.get("studentCode"),
        "classId": item.get("classId") or student.get("classId"),
        "className": item.get("className") or _class_name(item.get("classId") or student.get("classId")),
    }


def _exam_score_payload(item: dict):
    data = dict(item)
    data["subjectScores"] = [
        _subject_score_payload(score)
        for score in store.list("subject_scores")
        if score["examId"] == item["examId"] and score["studentId"] == item["studentId"]
    ]
    data["createTime"] = item.get("createTime") or item.get("updateTime")
    return data


def _answer_sheet_payload(item: dict):
    exam = store.find_by_id("exams", item.get("examId")) or {}
    student = store.find_by_id("students", item.get("studentId")) or {}
    images = [
        dict(
            img,
            pageNum=img.get("pageNum", img.get("pageNo", 1)),
            imagePath=img.get("imagePath", img.get("imageUrl")),
        )
        for img in store.list("answer_sheet_images")
        if img.get("answerSheetId") == item.get("id")
    ]
    return {
        **item,
        "examName": exam.get("name"),
        "studentName": student.get("studentName"),
        "studentNumber": item.get("studentNumber") or student.get("studentCode"),
        "className": _class_name(student.get("classId")),
        "imageCount": len(images),
        "statusName": item.get("statusName") or {0: "待上传", 1: "待识别", 2: "已识别", 3: "已阅卷", 4: "异常"}.get(item.get("status", 1), "待处理"),
        "images": images,
        "createTime": item.get("createTime"),
    }


def _marking_assign_payload(task: dict, marking_role: int = 1):
    exam_subject = store.find_by_id("exam_subjects", task.get("examSubjectId")) or {}
    exam = store.find_by_id("exams", exam_subject.get("examId")) or {}
    return {
        "id": task.get("id"),
        "taskId": task.get("id"),
        "taskName": task.get("taskName"),
        "teacherId": task.get("teacherId", 1),
        "teacherName": task.get("teacherName", "王老师"),
        "assignCount": task.get("assignCount", 0),
        "completedCount": task.get("completedCount", 0),
        "markingRole": marking_role,
        "markingRoleName": {1: "一评", 2: "二评", 3: "仲裁"}.get(marking_role, "一评"),
        "status": task.get("status", 1),
        "statusName": task.get("statusName", "待处理"),
        "examName": exam.get("name"),
        "subjectName": exam_subject.get("subjectName"),
        "questionNo": str(task.get("questionNo", "")),
        "taskStatus": task.get("status", 1),
    }


def _marking_task_payload(item: dict, include_assigns: bool = False):
    exam_subject = store.find_by_id("exam_subjects", item.get("examSubjectId")) or {}
    exam = store.find_by_id("exams", exam_subject.get("examId")) or {}
    data = {
        "id": item.get("id"),
        "examId": exam.get("id"),
        "examName": exam.get("name"),
        "examSubjectId": item.get("examSubjectId"),
        "subjectName": exam_subject.get("subjectName", "未知科目"),
        "questionId": item.get("questionId"),
        "questionNo": str(item.get("questionNo", "")),
        "name": item.get("taskName"),
        "taskType": item.get("taskType", 1),
        "totalCount": item.get("assignCount", 0),
        "completedCount": item.get("completedCount", 0),
        "pendingCount": max(0, item.get("assignCount", 0) - item.get("completedCount", 0)),
        "enableDoubleMarking": item.get("enableDoubleMarking", 0),
        "doubleMarkingThreshold": item.get("doubleMarkingThreshold", 5),
        "status": item.get("status", 1),
        "statusName": item.get("statusName", "待处理"),
        "accessCode": item.get("accessCode", f"AC{item.get('id'):04d}"),
        "secondAccessCode": item.get("secondAccessCode"),
        "accessCodeExpireTime": item.get("accessCodeExpireTime"),
        "startTime": item.get("startTime", item.get("createTime")),
        "endTime": item.get("endTime", ""),
        "remark": item.get("remark", ""),
        "createTime": item.get("createTime"),
    }
    if include_assigns:
        data["assigns"] = [_marking_assign_payload(item)]
    return data


def _marking_record_payload(item: dict):
    data = dict(item)
    image_url = data.get("answerImageUrl") or data.get("imageUrl")
    data.setdefault("teacherId", 1)
    data.setdefault("teacherName", "王老师")
    data.setdefault("markingRole", 1)
    data.setdefault("markingRoleName", "一评")
    data.setdefault("comment", "")
    data.setdefault("markingTime", data.get("createTime"))
    data.setdefault("answerImages", [image_url] if image_url else [])
    data.setdefault("answerImageUrl", image_url)
    data.setdefault("originalImageUrl", image_url)
    return data


def _marking_arbitration_payload(item: dict):
    data = dict(item)
    image_url = data.get("answerImageUrl")
    data.setdefault("answerImages", [image_url] if image_url else [])
    data.setdefault("originalImageUrl", image_url)
    data.setdefault("arbitrationComment", "")
    data.setdefault("arbitrationTime", data.get("createTime"))
    return data


def _marking_session_payload(task: dict):
    exam_subject = store.find_by_id("exam_subjects", task.get("examSubjectId")) or {}
    exam = store.find_by_id("exams", exam_subject.get("examId")) or {}
    return {
        "sessionToken": task.get("accessCode", f"access-{task.get('id', 1)}"),
        "taskId": task.get("id"),
        "examName": exam.get("name"),
        "subjectName": exam_subject.get("subjectName"),
        "questionNo": str(task.get("questionNo", "")),
        "fullScore": next((d.get("fullScore") for d in store.list("answer_sheet_details") if d.get("questionId") == task.get("questionId")), 10),
        "markingRole": 1,
        "markingRoleName": "一评",
        "totalCount": task.get("assignCount", 0),
        "completedCount": task.get("completedCount", 0),
        "pendingCount": max(0, task.get("assignCount", 0) - task.get("completedCount", 0)),
    }


def _marking_item_payload(record: dict):
    image_url = record.get("answerImageUrl") or record.get("imageUrl")
    task = store.find_by_id("marking_tasks", record.get("taskId")) or {}
    return {
        "recordId": record.get("id"),
        "answerSheetId": record.get("answerSheetId"),
        "questionId": record.get("questionId"),
        "questionImage": image_url,
        "fullScore": record.get("fullScore", 0),
        "questionNo": str(record.get("questionNo", "")),
        "currentIndex": task.get("completedCount", 0) + 1,
        "totalCount": task.get("assignCount", 0),
        "annotations": record.get("comment", ""),
    }


def _serialize(entity_key: str, item: dict):
    if not item:
        return item
    data = dict(item)

    if entity_key == "school":
        return {
            "id": data.get("id"),
            "name": data.get("schoolName"),
            "code": data.get("schoolCode"),
            "type": data.get("type", 1),
            "typeName": data.get("typeName", "学校"),
            "province": data.get("province", ""),
            "city": data.get("city", ""),
            "district": data.get("district", ""),
            "address": data.get("address", ""),
            "phone": data.get("phone", data.get("contactPhone", "")),
            "contactName": data.get("contactName", data.get("contactPerson", "")),
            "contactPhone": data.get("contactPhone", ""),
            "logo": data.get("logo", ""),
            "status": data.get("status", 1),
            "sort": data.get("sort", 0),
            "remark": data.get("remark", ""),
            "gradeCount": len([g for g in store.list("grades") if g.get("schoolId") == data.get("id")]),
            "teacherCount": len([t for t in store.list("teachers") if t.get("schoolId") == data.get("id")]),
            "studentCount": len([s for s in store.list("students") if s.get("schoolId") == data.get("id")]),
            "createTime": data.get("createTime"),
        }
    if entity_key == "grade":
        return {
            "id": data.get("id"),
            "schoolId": data.get("schoolId"),
            "schoolName": _school_name(data.get("schoolId")),
            "name": data.get("gradeName"),
            "code": data.get("gradeCode"),
            "enrollYear": data.get("enrollYear", 2026),
            "gradeNum": data.get("gradeNum", data.get("sort", 1)),
            "status": data.get("status", 1),
            "sort": data.get("sort", 0),
            "remark": data.get("remark", ""),
            "classCount": len([c for c in store.list("classes") if c.get("gradeId") == data.get("id")]),
            "studentCount": len([s for s in store.list("students") if s.get("gradeId") == data.get("id")]),
            "createTime": data.get("createTime"),
        }
    if entity_key == "class":
        teacher = store.find_by_id("teachers", data.get("headTeacherId"))
        return {
            "id": data.get("id"),
            "schoolId": data.get("schoolId"),
            "schoolName": _school_name(data.get("schoolId")),
            "gradeId": data.get("gradeId"),
            "gradeName": _grade_name(data.get("gradeId")),
            "name": data.get("className"),
            "code": data.get("classCode"),
            "classNum": data.get("classNum", data.get("sort", 1)),
            "headTeacherId": data.get("headTeacherId"),
            "headTeacherName": teacher.get("teacherName") if teacher else None,
            "status": data.get("status", 1),
            "sort": data.get("sort", 0),
            "remark": data.get("remark", ""),
            "studentCount": len([s for s in store.list("students") if s.get("classId") == data.get("id")]),
            "createTime": data.get("createTime"),
        }
    if entity_key == "teacher":
        return {
            "id": data.get("id"),
            "schoolId": data.get("schoolId"),
            "schoolName": _school_name(data.get("schoolId")),
            "userId": data.get("userId"),
            "username": data.get("username"),
            "jobNumber": data.get("jobNumber", data.get("teacherCode")),
            "name": data.get("teacherName"),
            "gender": data.get("gender", 1),
            "phone": data.get("phone", ""),
            "email": data.get("email", ""),
            "idCard": data.get("idCard", ""),
            "subject": data.get("subject", ""),
            "title": data.get("title", ""),
            "entryDate": data.get("entryDate", ""),
            "status": data.get("status", 1),
            "remark": data.get("remark", ""),
            "createTime": data.get("createTime"),
        }
    if entity_key == "student":
        return {
            "id": data.get("id"),
            "schoolId": data.get("schoolId"),
            "schoolName": _school_name(data.get("schoolId")),
            "classId": data.get("classId"),
            "className": _class_name(data.get("classId")),
            "gradeId": data.get("gradeId"),
            "gradeName": _grade_name(data.get("gradeId")),
            "userId": data.get("userId"),
            "studentNumber": data.get("studentNumber", data.get("studentCode")),
            "name": data.get("studentName"),
            "gender": data.get("gender", 1),
            "phone": data.get("phone", ""),
            "idCard": data.get("idCard", ""),
            "birthday": data.get("birthday", ""),
            "enrollDate": data.get("enrollDate", ""),
            "bindCode": data.get("bindCode", ""),
            "status": data.get("status", 1),
            "remark": data.get("remark", ""),
            "createTime": data.get("createTime"),
        }
    if entity_key == "parent":
        binds = [item for item in store.list("parent_student_binds") if item.get("parentId") == data.get("id")]
        students = []
        for bind in binds:
            student = store.find_by_id("students", bind.get("studentId")) or {}
            students.append({
                "bindId": bind.get("id"),
                "studentId": student.get("id"),
                "studentName": student.get("studentName"),
                "studentNumber": student.get("studentCode"),
                "className": _class_name(student.get("classId")),
                "relation": bind.get("relation", 1),
                "relationName": bind.get("relationName", "家长"),
            })
        return {
            "id": data.get("id"),
            "userId": data.get("userId"),
            "username": data.get("username"),
            "name": data.get("parentName"),
            "gender": data.get("gender", 1),
            "phone": data.get("phone", ""),
            "idCard": data.get("idCard", ""),
            "status": data.get("status", 1),
            "remark": data.get("remark", ""),
            "students": students,
            "createTime": data.get("createTime"),
        }
    if entity_key == "exam":
        subjects = [_serialize("exam-subject", item) for item in store.list_by("exam_subjects", examId=data.get("id"))]
        return {
            "id": data.get("id"),
            "schoolId": data.get("schoolId"),
            "schoolName": _school_name(data.get("schoolId")),
            "name": data.get("name"),
            "code": data.get("code"),
            "type": data.get("type", 1),
            "typeName": data.get("typeName", "考试"),
            "academicYear": data.get("academicYear"),
            "semester": data.get("semester", 1),
            "gradeId": data.get("gradeId"),
            "gradeName": data.get("gradeName") or _grade_name(data.get("gradeId")),
            "startTime": data.get("startTime"),
            "endTime": data.get("endTime"),
            "status": data.get("status", 0),
            "statusName": data.get("statusName", "草稿"),
            "totalScore": data.get("totalScore", 0),
            "studentCount": data.get("studentCount", 0),
            "subjectCount": data.get("subjectCount", len(subjects)),
            "description": data.get("description", ""),
            "remark": data.get("remark", ""),
            "classes": data.get("classes", []),
            "subjects": subjects,
            "createTime": data.get("createTime"),
        }
    if entity_key == "exam-subject":
        exam = store.find_by_id("exams", data.get("examId")) or {}
        return {
            "id": data.get("id"),
            "examId": data.get("examId"),
            "examName": exam.get("name"),
            "subjectName": data.get("subjectName"),
            "subjectCode": data.get("subjectCode", ""),
            "fullScore": data.get("fullScore", 100),
            "passScore": data.get("passScore", 60),
            "excellentScore": data.get("excellentScore", 85),
            "duration": data.get("duration", 120),
            "startTime": data.get("startTime"),
            "endTime": data.get("endTime"),
            "sort": data.get("sort", 0),
            "status": data.get("status", 1),
            "remark": data.get("remark", ""),
            "questionCount": data.get("questionCount", 0),
            "paperId": data.get("paperId"),
            "createTime": data.get("createTime"),
        }
    if entity_key == "knowledge-point":
        parent = store.find_by_id("knowledge_points", data.get("parentId")) or {}
        children = [_serialize("knowledge-point", item) for item in store.list("knowledge_points") if item.get("parentId") == data.get("id")]
        return {
            "id": data.get("id"),
            "schoolId": data.get("schoolId"),
            "schoolName": _school_name(data.get("schoolId")),
            "subjectName": data.get("subjectName"),
            "parentId": data.get("parentId", 0),
            "parentName": parent.get("name") or parent.get("knowledgePointName"),
            "name": data.get("name"),
            "code": data.get("code", ""),
            "level": data.get("level", 1),
            "path": data.get("path", ""),
            "sort": data.get("sort", 0),
            "status": data.get("status", 1),
            "remark": data.get("remark", ""),
            "children": children,
            "createTime": data.get("createTime"),
        }
    if entity_key == "answer-sheet-template":
        return {
            "id": data.get("id"),
            "paperId": data.get("paperId"),
            "examId": data.get("examId"),
            "paperName": data.get("paperName", ""),
            "examName": data.get("examName", ""),
            "subjectName": data.get("subjectName", ""),
            "name": data.get("name", ""),
            "pageSize": data.get("pageSize", "A4"),
            "orientation": data.get("orientation", 1),
            "columns": data.get("columns", 1),
            "marginTop": data.get("marginTop", 10),
            "marginBottom": data.get("marginBottom", 10),
            "marginLeft": data.get("marginLeft", 10),
            "marginRight": data.get("marginRight", 10),
            "headerConfig": data.get("headerConfig", {}),
            "studentInfoConfig": data.get("studentInfoConfig", {}),
            "status": data.get("status", 0),
            "pdfObjectName": data.get("pdfObjectName"),
            "pdfUrl": data.get("pdfUrl"),
            "templateImagePath": data.get("templateImagePath"),
            "templateImageUrl": data.get("templateImageUrl"),
            "cornerConfig": data.get("cornerConfig", {}),
            "createTime": data.get("createTime"),
            "updateTime": data.get("updateTime"),
            "regions": data.get("regions", []),
        }
    return data


def _template_payload(item: dict):
    return _serialize("answer-sheet-template", item)


__all__ = [name for name in globals() if not name.startswith("__")]
