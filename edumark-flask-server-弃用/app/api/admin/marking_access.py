from app.api.admin.common import *

@admin_bp.post("/marking/access/login")
def marking_access_login():
    task = store.find_by_id("marking_tasks", 1) or {}
    return ok(_marking_session_payload(task))


@admin_bp.get("/marking/access/task")
def marking_access_task():
    task = store.find_by_id("marking_tasks", 1) or {}
    return ok(_marking_session_payload(task))


@admin_bp.get("/marking/access/next")
def marking_access_next():
    record = store.list("marking_records")[0] if store.list("marking_records") else {}
    return ok(_marking_item_payload(record))


@admin_bp.post("/marking/access/submit")
def marking_access_submit():
    return ok(True)


@admin_bp.post("/marking/access/skip")
def marking_access_skip():
    return ok(True)


@admin_bp.post("/marking/access/generate/<int:task_id>")
@login_required
def marking_access_generate(task_id: int):
    task = store.find_by_id("marking_tasks", task_id)
    if not task:
        return fail("阅卷任务不存在")
    task["accessCode"] = f"AC{task_id:04d}"
    task["accessCodeExpireTime"] = store.now()
    return ok(_marking_task_payload(task))


@admin_bp.post("/marking/access/refresh/<int:task_id>")
@login_required
def marking_access_refresh(task_id: int):
    return ok()


@admin_bp.get("/marking/access/task/<int:task_id>")
@login_required
def marking_access_task_detail(task_id: int):
    task = store.find_by_id("marking_tasks", task_id)
    return ok(_marking_task_payload(task, include_assigns=True) if task else {})
