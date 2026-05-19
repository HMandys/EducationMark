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


