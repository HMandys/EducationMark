from app.api.app_client.common import *

@app_bp.post("/app/auth/login")
def app_login():
    payload = _json()
    phone = payload.get("phone")
    password = payload.get("password")
    user_type = payload.get("userType")
    role_code = "PARENT" if user_type == "parent" else "STUDENT"
    user = next((item for item in store.list("users") if item.get("phone") == phone and item.get("roleCode") == role_code and item.get("status") == 1), None)
    if not user or user.get("password") != password:
        return fail("账号不存在或密码错误")
    token = issue_token(user)
    return ok({
        "token": token,
        "userInfo": {
            "id": user["id"],
            "username": user["username"],
            "nickname": user.get("realName") or user["username"],
            "phone": user.get("phone"),
            "avatar": user.get("avatar"),
            "role": "student" if user.get("roleCode") == "STUDENT" else "parent",
        },
    })


@app_bp.post("/app/auth/register")
def app_register():
    payload = _json()
    phone = payload.get("phone")
    password = payload.get("password")
    nickname = payload.get("nickname") or "新用户"
    verify_code = payload.get("verifyCode")
    user_type = payload.get("userType") or "parent"
    if not phone or not password:
        return fail("手机号和密码不能为空")
    if not verify_code:
        return fail("验证码不能为空")
    if any(item for item in store.list("users") if item.get("phone") == phone):
        return fail("手机号已存在")

    role_code = "PARENT" if user_type == "parent" else "STUDENT"
    role_id = 7 if role_code == "PARENT" else 8
    user = store.create("users", {
        "username": f"{role_code.lower()}_{store.next_id('users')}",
        "password": password,
        "realName": nickname,
        "phone": phone,
        "userType": 2 if role_code == "PARENT" else 3,
        "status": 1,
        "schoolId": 1,
        "roleId": role_id,
        "roleCode": role_code,
        "roleName": "家长" if role_code == "PARENT" else "学生",
    })
    if role_code == "PARENT":
        parent = store.create("parents", {
            "parentName": nickname,
            "phone": phone,
            "relation": "家长",
            "status": 1,
        })
        user["parentId"] = parent["id"]
    token = issue_token(user)
    return ok({
        "token": token,
        "userInfo": {
            "id": user["id"],
            "username": user["username"],
            "nickname": user.get("realName") or user["username"],
            "phone": user.get("phone"),
            "avatar": user.get("avatar"),
            "role": "student" if user.get("roleCode") == "STUDENT" else "parent",
        },
    })


@app_bp.post("/app/auth/send-code")
def app_send_code():
    payload = _json()
    phone = payload.get("phone")
    if not phone:
        return fail("手机号不能为空")
    return ok({"phone": phone, "expireSeconds": 300}, message="验证码发送成功")


@app_bp.get("/app/auth/user-info")
@login_required
def app_user_info():
    user = current_user()
    return ok({
        "id": user["id"],
        "username": user["username"],
        "nickname": user.get("realName") or user["username"],
        "phone": user.get("phone"),
        "avatar": user.get("avatar"),
        "role": "student" if user.get("roleCode") == "STUDENT" else "parent",
    })


@app_bp.post("/app/auth/logout")
@login_required
def app_logout():
    logout_current()
    return ok()


@app_bp.post("/app/auth/change-password")
@login_required
def app_change_password():
    payload = _json()
    user = current_user()
    if user.get("password") != payload.get("oldPassword"):
        return fail("原密码错误")
    user["password"] = payload.get("newPassword")
    logout_current()
    return ok()


@app_bp.get("/app/student/bind-list")
@login_required
def app_bind_list():
    user = current_user()
    if not user.get("parentId"):
        return ok([])
    result = []
    for student in _current_student_for_parent(user):
        bind = next((item for item in store.list("parent_student_binds") if item.get("parentId") == user.get("parentId") and item.get("studentId") == student.get("id")), None)
        result.append(_student_payload(student, bind.get("createTime") if bind else None))
    return ok(result)


@app_bp.post("/app/student/bind")
@login_required
def app_bind_student():
    user = current_user()
    payload = _json()
    if not user.get("parentId"):
        parent = store.create("parents", {
            "parentName": user.get("realName") or user["username"],
            "phone": user.get("phone"),
            "relation": "家长",
            "status": 1,
        })
        user["parentId"] = parent["id"]

    student = next((item for item in store.list("students") if item["studentName"] == payload.get("studentName") and item["studentCode"] == payload.get("studentNumber")), None)
    if not student:
        return fail("未找到匹配的学生信息")
    if student.get("bindCode") != payload.get("bindCode"):
        return fail("绑定码错误")
    exists = next((item for item in store.list("parent_student_binds") if item["parentId"] == user["parentId"] and item["studentId"] == student["id"]), None)
    if not exists:
        exists = store.create("parent_student_binds", {"parentId": user["parentId"], "studentId": student["id"], "relation": "家长", "status": 1})
    return ok(_student_payload(student, exists.get("createTime")))


@app_bp.delete("/app/student/unbind/<int:student_id>")
@login_required
def app_unbind_student(student_id: int):
    user = current_user()
    store.data["parent_student_binds"] = [
        item for item in store.list("parent_student_binds")
        if not (item["parentId"] == user.get("parentId") and item["studentId"] == student_id)
    ]
    return ok()


@app_bp.get("/app/student/info")
@login_required
def app_student_info():
    user = current_user()
    if not user.get("studentId"):
        return fail("当前账号未绑定学生信息")
    student = store.find_by_id("students", user["studentId"])
    return ok(_student_payload(student))


@app_bp.get("/app/exam/list/<int:student_id>")
@login_required
def app_exam_list(student_id: int):
    allowed, error = _ensure_student_access(student_id)
    if not allowed:
        return error
    return ok([exam for exam in store.list("exams") if exam.get("status") == 5])


@app_bp.get("/app/exam/<int:exam_id>")
@login_required
def app_exam_detail(exam_id: int):
    exam = store.find_by_id("exams", exam_id)
    if not exam:
        return fail("考试不存在")
    return ok(exam)


@app_bp.get("/app/exam/<int:exam_id>/<int:student_id>")
@login_required
def app_exam_student_detail(exam_id: int, student_id: int):
    allowed, error = _ensure_student_access(student_id)
    if not allowed:
        return error
    exam = store.find_by_id("exams", exam_id)
    return ok(exam or {})


@app_bp.get("/app/score/<int:exam_id>/<int:student_id>")
@login_required
def app_score_detail(exam_id: int, student_id: int):
    allowed, error = _ensure_student_access(student_id)
    if not allowed:
        return error
    item = next((score for score in store.list("exam_scores") if score["examId"] == exam_id and score["studentId"] == student_id), None)
    if not item:
        return fail("未找到该学生的考试成绩")
    return ok(_exam_score_payload(item))
