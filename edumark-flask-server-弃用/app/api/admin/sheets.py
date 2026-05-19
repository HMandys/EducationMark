from app.api.admin.common import *


@admin_bp.get("/answer-sheet/page")
@login_required
def answer_sheet_page():
    items, total, page_num, page_size = store.page("answer_sheets", request.args.to_dict(), ["subjectName"])
    return page_result([_answer_sheet_payload(item) for item in items], total, page_num, page_size)


@admin_bp.get("/answer-sheet/<int:item_id>")
@login_required
def answer_sheet_detail(item_id: int):
    item = store.find_by_id("answer_sheets", item_id)
    if not item:
        return fail("答题卡不存在")
    return ok(_answer_sheet_payload(item))


@admin_bp.get("/answer-sheet/<int:item_id>/details")
@login_required
def answer_sheet_question_details(item_id: int):
    sheet = store.find_by_id("answer_sheets", item_id)
    if not sheet:
        return fail("答题卡不存在")
    return ok(store.list_by("answer_sheet_details", answerSheetId=item_id))


@admin_bp.get("/answer-sheet/<int:item_id>/details/<int:question_id>/preview")
@login_required
def answer_sheet_preview(item_id: int, question_id: int):
    return ok(f"https://via.placeholder.com/800x1200?text=Preview+{item_id}-{question_id}")


@admin_bp.post("/answer-sheet/<int:item_id>/objective-recognize")
@login_required
def answer_sheet_objective_recognize(item_id: int):
    return ok(store.list_by("answer_sheet_details", answerSheetId=item_id))


@admin_bp.put("/answer-sheet/<int:item_id>/details/<int:question_id>/objective-answer")
@login_required
def answer_sheet_objective_answer(item_id: int, question_id: int):
    payload = _json()
    detail = next((item for item in store.list("answer_sheet_details") if item.get("answerSheetId") == item_id and str(item.get("questionId")) == str(question_id)), None)
    if not detail:
        return fail("题目明细不存在")
    detail["studentAnswer"] = payload.get("studentAnswer", detail.get("studentAnswer"))
    return ok(detail)


@admin_bp.put("/answer-sheet/<int:item_id>/details/<int:question_id>/subjective-review-status")
@login_required
def answer_sheet_subjective_status(item_id: int, question_id: int):
    detail = next((item for item in store.list("answer_sheet_details") if item.get("answerSheetId") == item_id and str(item.get("questionId")) == str(question_id)), None)
    if not detail:
        return fail("题目明细不存在")
    status = int(request.args.get("status") or (_json().get("status") if isinstance(_json(), dict) else 1) or 1)
    detail["status"] = status
    detail["statusName"] = {0: "待处理", 1: "已完成", 2: "已复核", 3: "异常"}.get(status, "已完成")
    return ok(detail)


@admin_bp.post("/answer-sheet/<int:item_id>/subjective-review/rerun")
@login_required
def answer_sheet_subjective_rerun(item_id: int):
    return ok(store.list_by("answer_sheet_details", answerSheetId=item_id))


@admin_bp.post("/answer-sheet/<int:item_id>/ai-marking/rerun")
@login_required
def answer_sheet_ai_rerun(item_id: int):
    return ok(message="AI 批改已重新执行")


@admin_bp.post("/answer-sheet")
@login_required
def answer_sheet_create():
    payload = _json()
    payload.setdefault("status", 1)
    payload.setdefault("statusName", "待识别")
    item = store.create("answer_sheets", payload)
    return ok(item["id"])


@admin_bp.put("/answer-sheet")
@login_required
def answer_sheet_update():
    item = store.update("answer_sheets", _json())
    if not item:
        return fail("答题卡不存在")
    return ok()


@admin_bp.delete("/answer-sheet/<int:item_id>")
@login_required
def answer_sheet_delete(item_id: int):
    store.delete("answer_sheets", item_id)
    return ok()


@admin_bp.delete("/answer-sheet/batch")
@login_required
def answer_sheet_batch_delete():
    ids = _json() or []
    store.batch_delete("answer_sheets", ids)
    return ok()


@admin_bp.post("/answer-sheet/<int:item_id>/images")
@login_required
def answer_sheet_upload_images(item_id: int):
    image = {"id": store.next_id("answer_sheet_images"), "answerSheetId": item_id, "pageNum": 1, "pageNo": 1, "imagePath": "uploaded/demo.png", "imageUrl": "https://via.placeholder.com/800x1200?text=Uploaded", "originalName": "uploaded-demo.png", "fileSize": 100000, "width": 800, "height": 1200, "sort": 1}
    store.data["answer_sheet_images"].append(dict(image, pageNo=1, imageType=1))
    sheet = store.find_by_id("answer_sheets", item_id)
    if sheet:
        sheet["status"] = max(int(sheet.get("status", 1)), 1)
    return ok(_answer_sheet_payload(sheet or {"id": item_id, "images": [image]}))


@admin_bp.post("/answer-sheet/upload")
@login_required
def answer_sheet_upload():
    payload = _json()
    item = store.create("answer_sheets", {
        "examId": payload.get("examId"),
        "examSubjectId": payload.get("examSubjectId"),
        "studentId": payload.get("studentId"),
        "studentNumber": payload.get("studentNumber"),
        "seatNumber": payload.get("seatNumber"),
        "status": 1,
        "statusName": "待识别",
        "objectiveScore": 0,
        "subjectiveScore": 0,
        "totalScore": 0,
        "remark": "",
    })
    for index, object_name in enumerate(payload.get("imageObjectNames") or [], start=1):
        store.data["answer_sheet_images"].append({
            "id": store.next_id("answer_sheet_images"),
            "answerSheetId": item["id"],
            "pageNo": index,
            "pageNum": index,
            "imagePath": object_name,
            "imageUrl": f"https://mock.local/{object_name}",
            "originalName": (payload.get("imageOriginalNames") or [None] * index)[index - 1] or f"page-{index}.png",
            "fileSize": 100000,
            "width": 800,
            "height": 1200,
            "imageType": 1,
            "sort": index,
        })
    return ok(item["id"])


@admin_bp.delete("/answer-sheet/image/<int:image_id>")
@login_required
def answer_sheet_delete_image(image_id: int):
    store.delete("answer_sheet_images", image_id)
    return ok()


@admin_bp.get("/answer-sheet/list/<int:exam_subject_id>")
@login_required
def answer_sheet_list_by_subject(exam_subject_id: int):
    return ok([_answer_sheet_payload(item) for item in store.list_by("answer_sheets", examSubjectId=exam_subject_id)])


@admin_bp.put("/answer-sheet/<int:item_id>/status")
@login_required
def answer_sheet_status(item_id: int):
    item = store.find_by_id("answer_sheets", item_id)
    if not item:
        return fail("答题卡不存在")
    item["status"] = int(request.args.get("status") or _json().get("status") or 1)
    return ok()


@admin_bp.post("/answer-sheet/<int:item_id>/recognize")
@login_required
def answer_sheet_recognize(item_id: int):
    return ok(message="答题卡识别完成")


