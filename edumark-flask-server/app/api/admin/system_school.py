from app.api.admin.common import *

@admin_bp.post("/auth/login")
def auth_login():
    payload = _json()
    user = store.find_one("users", username=payload.get("username"))
    if not user or user.get("password") != payload.get("password"):
        return fail("用户名或密码错误", 500)
    token = issue_token(user)
    return ok({
        "token": token,
        "tokenType": "Bearer",
        "expiresIn": 86400,
        "userInfo": {
            "userId": user["id"],
            "username": user["username"],
            "realName": user.get("realName"),
            "avatar": user.get("avatar"),
            "roleCode": user.get("roleCode"),
            "roleName": user.get("roleName"),
            "schoolId": user.get("schoolId"),
            "schoolName": "示范中学",
            "permissions": [item["permissionCode"] for item in store.list("permissions")],
        },
    })


@admin_bp.get("/auth/info")
@login_required
def auth_info():
    user = current_user()
    return ok({
        "userId": user["id"],
        "username": user["username"],
        "realName": user.get("realName"),
        "avatar": user.get("avatar"),
        "roleCode": user.get("roleCode"),
        "roleName": user.get("roleName"),
        "schoolId": user.get("schoolId"),
        "schoolName": "示范中学",
        "permissions": [item["permissionCode"] for item in store.list("permissions")],
    })


@admin_bp.post("/auth/logout")
@login_required
def auth_logout():
    logout_current()
    return ok()


@admin_bp.get("/system/user/page")
@login_required
def user_page():
    return _page("system/user")


@admin_bp.get("/system/user/<int:item_id>")
@login_required
def user_detail(item_id: int):
    return _detail("system/user", item_id)


@admin_bp.post("/system/user")
@login_required
def user_create():
    payload = _json()
    role = store.find_by_id("roles", payload.get("roleId") or 1) or {}
    payload.setdefault("password", "admin123")
    payload["roleCode"] = role.get("roleCode", "SUPER_ADMIN")
    payload["roleName"] = role.get("roleName", "超级管理员")
    return _create("system/user", payload)


@admin_bp.put("/system/user")
@login_required
def user_update():
    return _update("system/user")


@admin_bp.delete("/system/user/<int:item_id>")
@login_required
def user_delete(item_id: int):
    return _delete("system/user", item_id)


@admin_bp.delete("/system/user/batch")
@login_required
def user_batch_delete():
    return _batch_delete("system/user")


@admin_bp.put("/system/user/<int:item_id>/password/reset")
@login_required
def user_reset_password(item_id: int):
    user = store.find_by_id("users", item_id)
    if not user:
        return fail("用户不存在")
    payload = _json()
    user["password"] = payload.get("newPassword") or "admin123"
    return ok()


@admin_bp.put("/system/user/<int:item_id>/status")
@login_required
def user_status(item_id: int):
    return _update_status("system/user", item_id)


@admin_bp.get("/system/role/list")
@login_required
def role_list():
    return ok(store.list("roles"))


@admin_bp.get("/system/role/page")
@login_required
def role_page():
    return _page("system/role")


@admin_bp.get("/system/role/<int:item_id>")
@login_required
def role_detail(item_id: int):
    return _detail("system/role", item_id)


@admin_bp.post("/system/role")
@login_required
def role_create():
    return _create("system/role")


@admin_bp.put("/system/role")
@login_required
def role_update():
    return _update("system/role")


@admin_bp.delete("/system/role/<int:item_id>")
@login_required
def role_delete(item_id: int):
    return _delete("system/role", item_id)


@admin_bp.put("/system/role/<int:item_id>/status")
@login_required
def role_status(item_id: int):
    return _update_status("system/role", item_id)


@admin_bp.get("/system/permission/tree")
@login_required
def permission_tree():
    return ok(store.list("permissions"))


@admin_bp.get("/system/permission/page")
@login_required
def permission_page():
    return _page("system/permission")


@admin_bp.get("/system/permission/<int:item_id>")
@login_required
def permission_detail(item_id: int):
    return _detail("system/permission", item_id)


@admin_bp.post("/system/permission")
@login_required
def permission_create():
    return _create("system/permission")


@admin_bp.put("/system/permission")
@login_required
def permission_update():
    return _update("system/permission")


@admin_bp.delete("/system/permission/<int:item_id>")
@login_required
def permission_delete(item_id: int):
    return _delete("system/permission", item_id)


@admin_bp.put("/system/permission/<int:item_id>/status")
@login_required
def permission_status(item_id: int):
    return _update_status("system/permission", item_id)


@admin_bp.get("/school/page")
@login_required
def school_page():
    return _page("school")


@admin_bp.get("/school/<int:item_id>")
@login_required
def school_detail(item_id: int):
    return _detail("school", item_id)


@admin_bp.post("/school")
@login_required
def school_create():
    return _create("school")


@admin_bp.put("/school")
@login_required
def school_update():
    return _update("school")


@admin_bp.delete("/school/<int:item_id>")
@login_required
def school_delete(item_id: int):
    return _delete("school", item_id)


@admin_bp.delete("/school/batch")
@login_required
def school_batch_delete():
    return _batch_delete("school")


@admin_bp.put("/school/<int:item_id>/status")
@login_required
def school_status(item_id: int):
    return _update_status("school", item_id)


@admin_bp.get("/school/select")
@login_required
def school_select():
    return ok([_serialize("school", item) for item in store.list("schools")])


@admin_bp.get("/grade/page")
@login_required
def grade_page():
    return _page("grade")


@admin_bp.get("/grade/<int:item_id>")
@login_required
def grade_detail(item_id: int):
    return _detail("grade", item_id)


@admin_bp.post("/grade")
@login_required
def grade_create():
    return _create("grade")


@admin_bp.put("/grade")
@login_required
def grade_update():
    return _update("grade")


@admin_bp.delete("/grade/<int:item_id>")
@login_required
def grade_delete(item_id: int):
    return _delete("grade", item_id)


@admin_bp.delete("/grade/batch")
@login_required
def grade_batch_delete():
    return _batch_delete("grade")


@admin_bp.put("/grade/<int:item_id>/status")
@login_required
def grade_status(item_id: int):
    return _update_status("grade", item_id)


@admin_bp.get("/grade/list/<int:school_id>")
@login_required
def grade_list_by_school(school_id: int):
    return ok([_serialize("grade", item) for item in store.list_by("grades", schoolId=school_id)])


@admin_bp.get("/class/page")
@login_required
def class_page():
    return _page("class")


@admin_bp.get("/class/<int:item_id>")
@login_required
def class_detail(item_id: int):
    return _detail("class", item_id)


@admin_bp.post("/class")
@login_required
def class_create():
    return _create("class")


@admin_bp.put("/class")
@login_required
def class_update():
    return _update("class")


@admin_bp.delete("/class/<int:item_id>")
@login_required
def class_delete(item_id: int):
    return _delete("class", item_id)


@admin_bp.delete("/class/batch")
@login_required
def class_batch_delete():
    return _batch_delete("class")


@admin_bp.put("/class/<int:item_id>/status")
@login_required
def class_status(item_id: int):
    return _update_status("class", item_id)


@admin_bp.get("/class/list/grade/<int:grade_id>")
@login_required
def class_list_by_grade(grade_id: int):
    return ok([_serialize("class", item) for item in store.list_by("classes", gradeId=grade_id)])


@admin_bp.get("/class/list/school/<int:school_id>")
@login_required
def class_list_by_school(school_id: int):
    return ok([_serialize("class", item) for item in store.list_by("classes", schoolId=school_id)])


@admin_bp.get("/teacher/page")
@login_required
def teacher_page():
    return _page("teacher")


@admin_bp.get("/teacher/<int:item_id>")
@login_required
def teacher_detail(item_id: int):
    return _detail("teacher", item_id)


@admin_bp.post("/teacher")
@login_required
def teacher_create():
    return _create("teacher")


@admin_bp.put("/teacher")
@login_required
def teacher_update():
    return _update("teacher")


@admin_bp.delete("/teacher/<int:item_id>")
@login_required
def teacher_delete(item_id: int):
    return _delete("teacher", item_id)


@admin_bp.delete("/teacher/batch")
@login_required
def teacher_batch_delete():
    return _batch_delete("teacher")


@admin_bp.put("/teacher/<int:item_id>/status")
@login_required
def teacher_status(item_id: int):
    return _update_status("teacher", item_id)


@admin_bp.get("/teacher/list/<int:school_id>")
@login_required
def teacher_list_by_school(school_id: int):
    return ok([_serialize("teacher", item) for item in store.list_by("teachers", schoolId=school_id)])


@admin_bp.get("/student/page")
@login_required
def student_page():
    return _page("student")


@admin_bp.get("/student/<int:item_id>")
@login_required
def student_detail(item_id: int):
    return _detail("student", item_id)


@admin_bp.post("/student")
@login_required
def student_create():
    payload = _json()
    payload.setdefault("bindCode", f"CODE{store.next_id('students')}")
    payload.setdefault("status", 1)
    item = store.create("students", payload)
    return ok(item["id"])


@admin_bp.put("/student")
@login_required
def student_update():
    return _update("student")


@admin_bp.delete("/student/<int:item_id>")
@login_required
def student_delete(item_id: int):
    return _delete("student", item_id)


@admin_bp.delete("/student/batch")
@login_required
def student_batch_delete():
    return _batch_delete("student")


@admin_bp.put("/student/<int:item_id>/status")
@login_required
def student_status(item_id: int):
    return _update_status("student", item_id)


@admin_bp.get("/student/list/<int:class_id>")
@login_required
def student_list_by_class(class_id: int):
    return ok([_serialize("student", item) for item in store.list_by("students", classId=class_id)])


@admin_bp.post("/student/<int:item_id>/refresh-bind-code")
@login_required
def refresh_student_bind_code(item_id: int):
    student = store.find_by_id("students", item_id)
    if not student:
        return fail("学生不存在")
    student["bindCode"] = f"NEW{item_id:04d}"
    return ok(student["bindCode"])


@admin_bp.get("/parent/page")
@login_required
def parent_page():
    return _page("parent")


@admin_bp.get("/parent/<int:item_id>")
@login_required
def parent_detail(item_id: int):
    return _detail("parent", item_id)


@admin_bp.post("/parent")
@login_required
def parent_create():
    return _create("parent")


@admin_bp.put("/parent")
@login_required
def parent_update():
    return _update("parent")


@admin_bp.delete("/parent/<int:item_id>")
@login_required
def parent_delete(item_id: int):
    return _delete("parent", item_id)


@admin_bp.delete("/parent/batch")
@login_required
def parent_batch_delete():
    return _batch_delete("parent")


@admin_bp.put("/parent/<int:item_id>/status")
@login_required
def parent_status(item_id: int):
    return _update_status("parent", item_id)


@admin_bp.post("/parent/<int:parent_id>/bind")
@login_required
def parent_bind_student(parent_id: int):
    payload = _json()
    student_id = payload.get("studentId")
    if not student_id:
        student = next((
            item for item in store.list("students")
            if item.get("studentName") == payload.get("studentName")
            and item.get("studentCode") == payload.get("studentNumber")
            and item.get("bindCode") == payload.get("bindCode")
        ), None)
        student_id = student.get("id") if student else None
    if not store.find_by_id("parents", parent_id):
        return fail("家长不存在")
    if not store.find_by_id("students", student_id):
        return fail("学生不存在")
    store.create("parent_student_binds", {
        "parentId": parent_id,
        "studentId": student_id,
        "relation": payload.get("relation", 1),
        "relationName": payload.get("relationName", "家长"),
        "status": 1,
    })
    return ok()


@admin_bp.delete("/parent/<int:parent_id>/unbind/<int:student_id>")
@login_required
def parent_unbind_student(parent_id: int, student_id: int):
    binds = [item for item in store.list("parent_student_binds") if not (item["parentId"] == parent_id and item["studentId"] == student_id)]
    store.data["parent_student_binds"] = binds
    return ok()


@admin_bp.get("/parent/<int:parent_id>/students")
@login_required
def parent_students(parent_id: int):
    binds = [item for item in store.list("parent_student_binds") if item["parentId"] == parent_id]
    result = []
    for bind in binds:
        student = store.find_by_id("students", bind.get("studentId")) or {}
        result.append({
            "bindId": bind.get("id"),
            "studentId": student.get("id"),
            "studentName": student.get("studentName"),
            "studentNumber": student.get("studentCode"),
            "className": _class_name(student.get("classId")),
            "relation": bind.get("relation", 1),
            "relationName": bind.get("relationName", "家长"),
        })
    return ok(result)


