from app.api.admin.common import *

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


