from __future__ import annotations

from flask import request

from app.core.result import fail, ok, page_result
from app.core.store import store

from app.api.admin.auth_helpers import _json
from app.api.admin.serializers import _serialize


ENTITY_MAP = {
    "school": ("schools", ["schoolName", "schoolCode", "contactPerson"]),
    "grade": ("grades", ["gradeName", "gradeCode"]),
    "class": ("classes", ["className", "classCode"]),
    "teacher": ("teachers", ["teacherName", "teacherCode", "phone", "subject"]),
    "student": ("students", ["studentName", "studentCode", "phone"]),
    "parent": ("parents", ["parentName", "phone"]),
    "exam": ("exams", ["name", "code", "typeName", "academicYear"]),
    "exam-subject": ("exam_subjects", ["subjectName"]),
    "knowledge-point": ("knowledge_points", ["subjectName", "name", "code"]),
    "system/user": ("users", ["username", "realName", "phone"]),
    "system/role": ("roles", ["roleName", "roleCode"]),
    "system/permission": ("permissions", ["permissionName", "permissionCode"]),
    "answer-sheet-template": ("templates", ["name"]),
}


def _normalize_input(entity_key: str, payload: dict):
    data = dict(payload)
    if entity_key == "school":
        data["schoolName"] = data.get("name", data.get("schoolName"))
        data["schoolCode"] = data.get("code", data.get("schoolCode"))
        data["contactPerson"] = data.get("contactName", data.get("contactPerson"))
        data["contactPhone"] = data.get("contactPhone", data.get("phone", ""))
    elif entity_key == "grade":
        data["gradeName"] = data.get("name", data.get("gradeName"))
        data["gradeCode"] = data.get("code", data.get("gradeCode"))
    elif entity_key == "class":
        data["className"] = data.get("name", data.get("className"))
        data["classCode"] = data.get("code", data.get("classCode"))
    elif entity_key == "teacher":
        data["teacherName"] = data.get("name", data.get("teacherName"))
        data["teacherCode"] = data.get("jobNumber", data.get("teacherCode"))
    elif entity_key == "student":
        data["studentName"] = data.get("name", data.get("studentName"))
        data["studentCode"] = data.get("studentNumber", data.get("studentCode"))
    elif entity_key == "parent":
        data["parentName"] = data.get("name", data.get("parentName"))
    elif entity_key == "exam":
        data["classes"] = data.get("classes", data.get("classes", []))
        data["subjects"] = data.get("subjects", data.get("subjects", []))
    elif entity_key == "exam-subject":
        data["sort"] = data.get("sort", 0)
        data["fullScore"] = data.get("fullScore", 100)
        data["status"] = data.get("status", 1)
    return data


def _page(entity_key: str):
    table_key, fuzzy_fields = ENTITY_MAP[entity_key]
    items, total, page_num, page_size = store.page(table_key, request.args.to_dict(), fuzzy_fields)
    return page_result([_serialize(entity_key, item) for item in items], total, page_num, page_size)


def _detail(entity_key: str, item_id: int):
    table_key, _ = ENTITY_MAP[entity_key]
    item = store.find_by_id(table_key, item_id)
    if not item:
        return fail("数据不存在")
    return ok(_serialize(entity_key, item))


def _create(entity_key: str, payload: dict | None = None):
    table_key, _ = ENTITY_MAP[entity_key]
    item = store.create(table_key, _normalize_input(entity_key, payload or _json()))
    return ok(item["id"])


def _update(entity_key: str):
    table_key, _ = ENTITY_MAP[entity_key]
    item = store.update(table_key, _normalize_input(entity_key, _json()))
    if not item:
        return fail("数据不存在")
    return ok()


def _delete(entity_key: str, item_id: int):
    table_key, _ = ENTITY_MAP[entity_key]
    store.delete(table_key, item_id)
    return ok()


def _batch_delete(entity_key: str):
    table_key, _ = ENTITY_MAP[entity_key]
    payload = _json()
    ids = payload.get("ids") or payload or []
    store.batch_delete(table_key, ids)
    return ok()


def _update_status(entity_key: str, item_id: int):
    table_key, _ = ENTITY_MAP[entity_key]
    item = store.find_by_id(table_key, item_id)
    if not item:
        return fail("数据不存在")
    item["status"] = int(request.args.get("status") or _json().get("status") or 1)
    return ok()


__all__ = [name for name in globals() if not name.startswith("__")]
