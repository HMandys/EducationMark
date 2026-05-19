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


