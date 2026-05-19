from __future__ import annotations

from app.api.app_client.auth_helpers import current_user, fail, store


def _current_student_for_parent(user: dict):
    binds = [item for item in store.list("parent_student_binds") if item["parentId"] == user.get("parentId")]
    student_ids = [item["studentId"] for item in binds]
    return [student for student in store.list("students") if student["id"] in student_ids]


def _subject_scores(exam_id: int, student_id: int):
    return [item for item in store.list("subject_scores") if item["examId"] == exam_id and item["studentId"] == student_id]


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
