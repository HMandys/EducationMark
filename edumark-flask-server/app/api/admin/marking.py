from app.api.admin.common import *


@admin_bp.get("/marking/task/page")
@login_required
def marking_task_page():
    items, total, page_num, page_size = store.page("marking_tasks", request.args.to_dict(), [])
    return page_result([_marking_task_payload(item) for item in items], total, page_num, page_size)


@admin_bp.get("/marking/task/<int:item_id>")
@login_required
def marking_task_detail(item_id: int):
    item = store.find_by_id("marking_tasks", item_id)
    if not item:
        return ok({})
    return ok(_marking_task_payload(item, include_assigns=True))


@admin_bp.post("/marking/task/generate/<int:exam_subject_id>")
@login_required
def marking_task_generate(exam_subject_id: int):
    item = store.create("marking_tasks", {"examSubjectId": exam_subject_id, "status": 1, "statusName": "待分配", "taskName": f"阅卷任务-{exam_subject_id}", "questionId": 1000 + exam_subject_id, "questionNo": 1, "assignCount": 0, "completedCount": 0})
    return ok(item["id"])


@admin_bp.delete("/marking/task/<int:item_id>")
@login_required
def marking_task_delete(item_id: int):
    store.delete("marking_tasks", item_id)
    return ok()


@admin_bp.post("/marking/task/assign")
@login_required
def marking_task_assign():
    payload = _json()
    task = store.find_by_id("marking_tasks", payload.get("taskId"))
    if not task:
        return fail("阅卷任务不存在")
    assigns = payload.get("assigns") or []
    if assigns:
        first = assigns[0]
        teacher = store.find_by_id("teachers", first.get("teacherId")) or {}
        task["teacherId"] = first.get("teacherId")
        task["teacherName"] = teacher.get("teacherName", "")
        task["assignCount"] = first.get("assignCount", task.get("assignCount", 0))
    task["status"] = 1
    task["statusName"] = "待开始"
    return ok(message="阅卷任务分配完成")


@admin_bp.post("/marking/task/start/<int:item_id>")
@login_required
def marking_task_start(item_id: int):
    task = store.find_by_id("marking_tasks", item_id)
    if task:
        task["status"] = 2
        task["statusName"] = "进行中"
    return ok()


@admin_bp.post("/marking/task/complete/<int:item_id>")
@login_required
def marking_task_complete(item_id: int):
    task = store.find_by_id("marking_tasks", item_id)
    if task:
        task["status"] = 3
        task["statusName"] = "已完成"
    return ok()


@admin_bp.get("/marking/task/list/<int:exam_subject_id>")
@login_required
def marking_task_list(exam_subject_id: int):
    items = [item for item in store.list("marking_tasks") if item.get("examSubjectId") == exam_subject_id]
    return ok([_marking_task_payload(item) for item in items])


@admin_bp.get("/marking/my-assigns")
@login_required
def marking_my_assigns():
    return ok([_marking_assign_payload(task) for task in store.list("marking_tasks")])


@admin_bp.get("/marking/records")
@login_required
def marking_records():
    items, total, page_num, page_size = store.page("marking_records", request.args.to_dict(), [])
    return page_result([_marking_record_payload(item) for item in items], total, page_num, page_size)


@admin_bp.get("/marking/next-pending")
@login_required
def marking_next_pending():
    record = store.list("marking_records")[0] if store.list("marking_records") else None
    return ok(_marking_record_payload(record) if record else None)


@admin_bp.get("/marking/record/<int:record_id>")
@login_required
def marking_record_detail(record_id: int):
    record = store.find_by_id("marking_records", record_id) or {}
    return ok(_marking_record_payload(record) if record else {})


@admin_bp.post("/marking/submit")
@login_required
def marking_submit():
    payload = _json()
    record = store.find_by_id("marking_records", payload.get("recordId"))
    if record:
        record["score"] = payload.get("score", record.get("score"))
        record["comment"] = payload.get("comment", "")
        record["status"] = 1
        record["statusName"] = "已完成"
    return ok(message="阅卷提交成功")


@admin_bp.get("/marking/arbitrations")
@login_required
def marking_arbitrations():
    items, total, page_num, page_size = store.page("marking_arbitrations", request.args.to_dict(), [])
    return page_result([_marking_arbitration_payload(item) for item in items], total, page_num, page_size)


@admin_bp.get("/marking/next-arbitration")
@login_required
def marking_next_arbitration():
    arbitration = store.list("marking_arbitrations")[0] if store.list("marking_arbitrations") else None
    return ok(_marking_arbitration_payload(arbitration) if arbitration else None)


@admin_bp.get("/marking/arbitration/<int:arbitration_id>")
@login_required
def marking_arbitration_detail(arbitration_id: int):
    item = store.find_by_id("marking_arbitrations", arbitration_id)
    return ok(_marking_arbitration_payload(item) if item else {})


@admin_bp.post("/marking/arbitration/submit")
@login_required
def marking_arbitration_submit():
    return ok(message="仲裁提交成功")


@admin_bp.post("/marking/auto-mark/<int:exam_subject_id>")
@login_required
def marking_auto_mark(exam_subject_id: int):
    return ok(message="自动阅卷已触发")


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
