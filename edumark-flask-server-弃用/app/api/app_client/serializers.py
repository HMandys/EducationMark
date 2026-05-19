from __future__ import annotations

from app.core.store import store
from app.api.app_client.access import _subject_scores


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


__all__ = [name for name in globals() if not name.startswith("__")]
