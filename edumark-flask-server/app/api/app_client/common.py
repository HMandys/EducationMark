from __future__ import annotations

from collections import defaultdict

from flask import Blueprint, request

from app.core.auth import current_user, issue_token, login_required, logout_current
from app.core.result import fail, ok
from app.core.store import store

app_bp = Blueprint("app_client", __name__)


def _json():
    return request.get_json(silent=True) or {}


def _current_student_for_parent(user: dict):
    binds = [item for item in store.list("parent_student_binds") if item["parentId"] == user.get("parentId")]
    student_ids = [item["studentId"] for item in binds]
    return [student for student in store.list("students") if student["id"] in student_ids]


def _subject_scores(exam_id: int, student_id: int):
    return [item for item in store.list("subject_scores") if item["examId"] == exam_id and item["studentId"] == student_id]


def _student_payload(student: dict, bind_time=None):
    class_info = store.find_by_id("classes", student.get("classId")) or {}
    grade = store.find_by_id("grades", student.get("gradeId")) or {}
    school = store.find_by_id("schools", student.get("schoolId")) or {}
    return {
        "id": student["id"],
        "name": student["studentName"],
        "studentNumber": student["studentCode"],
        "className": class_info.get("className", ""),
        "classId": student.get("classId"),
        "gradeName": grade.get("gradeName", ""),
        "gradeId": student.get("gradeId"),
        "schoolName": school.get("schoolName", ""),
        "schoolId": student.get("schoolId"),
        "bindTime": bind_time,
    }


def _subject_score_payload(item: dict):
    return {
        "id": item.get("id"),
        "examSubjectId": item.get("examSubjectId"),
        "subjectName": item.get("subjectName"),
        "fullScore": item.get("fullScore"),
        "score": item.get("score"),
        "objectiveScore": item.get("objectiveScore"),
        "subjectiveScore": item.get("subjectiveScore"),
        "classRank": item.get("classRank"),
        "gradeRank": item.get("gradeRank"),
    }


def _exam_score_payload(item: dict):
    return {
        **item,
        "subjectScores": [_subject_score_payload(score) for score in _subject_scores(item["examId"], item["studentId"])],
        "createTime": item.get("createTime") or item.get("updateTime"),
    }


def _answer_sheet_image_payload(item: dict):
    return {
        "id": item.get("id"),
        "answerSheetId": item.get("answerSheetId"),
        "imageUrl": item.get("imageUrl"),
        "pageNo": item.get("pageNo", item.get("pageNum", 1)),
        "imageType": item.get("imageType", 1),
        "sort": item.get("sort", 1),
    }


def _answer_sheet_payload(sheet: dict):
    images = [_answer_sheet_image_payload(item) for item in store.list("answer_sheet_images") if item["answerSheetId"] == sheet["id"]]
    return {
        "id": sheet.get("id"),
        "examId": sheet.get("examId"),
        "examSubjectId": sheet.get("examSubjectId"),
        "subjectName": sheet.get("subjectName"),
        "studentId": sheet.get("studentId"),
        "totalScore": sheet.get("totalScore"),
        "objectiveScore": sheet.get("objectiveScore"),
        "subjectiveScore": sheet.get("subjectiveScore"),
        "status": sheet.get("status"),
        "images": images,
    }


def _ensure_student_access(student_id: int):
    user = current_user()
    if not user:
        return False, fail("未登录", 401)
    if user.get("studentId") and int(user["studentId"]) == int(student_id):
        return True, None
    if user.get("parentId"):
        binds = [item for item in store.list("parent_student_binds") if item["parentId"] == user["parentId"] and item["studentId"] == student_id]
        if binds:
            return True, None
    return False, fail("无权查看该学生数据")


__all__ = [name for name in globals() if not name.startswith("__")]

