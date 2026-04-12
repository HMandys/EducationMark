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


@admin_bp.get("/answer-sheet-template/page")
@login_required
def template_page():
    return _page("answer-sheet-template")


@admin_bp.get("/answer-sheet-template/<int:item_id>")
@login_required
def template_detail(item_id: int):
    return _detail("answer-sheet-template", item_id)


@admin_bp.get("/answer-sheet-template/paper/<int:paper_id>")
@login_required
def template_by_paper(paper_id: int):
    item = next((template for template in store.list("templates") if template.get("paperId") == paper_id), None)
    return ok(_template_payload(item) if item else None)


@admin_bp.post("/answer-sheet-template")
@login_required
def template_create():
    return _create("answer-sheet-template")


@admin_bp.put("/answer-sheet-template")
@login_required
def template_update():
    return _update("answer-sheet-template")


@admin_bp.delete("/answer-sheet-template/<int:item_id>")
@login_required
def template_delete(item_id: int):
    return _delete("answer-sheet-template", item_id)


@admin_bp.post("/answer-sheet-template/generate/<int:paper_id>")
@login_required
def template_generate(paper_id: int):
    item = store.create("templates", {"paperId": paper_id, "name": f"模板-{paper_id}", "status": 0, "pageSize": "A4", "orientation": 1, "columns": 1, "marginTop": 10, "marginBottom": 10, "marginLeft": 10, "marginRight": 10, "headerConfig": {}, "studentInfoConfig": {}, "cornerConfig": {}, "regions": []})
    return ok(item["id"])


@admin_bp.get("/answer-sheet-template/<int:item_id>/validate")
@login_required
def template_validate(item_id: int):
    template = store.find_by_id("templates", item_id)
    regions = (template or {}).get("regions", [])
    return ok({
        "passed": True,
        "totalRegionCount": len(regions),
        "annotatedRegionCount": len(regions),
        "issueCount": 0,
        "issues": [],
    })


@admin_bp.post("/answer-sheet-template/<int:item_id>/publish")
@login_required
def template_publish(item_id: int):
    item = store.find_by_id("templates", item_id)
    if item:
        item["status"] = 1
    return ok()


@admin_bp.get("/answer-sheet-template/<int:item_id>/preview")
@login_required
def template_preview(item_id: int):
    return ok(f"https://via.placeholder.com/1200x1600?text=Template+{item_id}")


@admin_bp.get("/answer-sheet-template/<int:item_id>/download")
@login_required
def template_download(item_id: int):
    return ok(f"/downloads/template-{item_id}.pdf")


@admin_bp.post("/answer-sheet-template/<int:item_id>/upload-image")
@login_required
def template_upload_image(item_id: int):
    item = store.find_by_id("templates", item_id)
    if item:
        item["templateImagePath"] = f"template/{item_id}/image.png"
        item["templateImageUrl"] = f"https://via.placeholder.com/1200x1600?text=Template+{item_id}"
    return ok(f"template/{item_id}/image.png")


@admin_bp.put("/answer-sheet-template/<int:item_id>/corner-config")
@login_required
def template_corner_config(item_id: int):
    item = store.find_by_id("templates", item_id)
    if not item:
        return fail("模板不存在")
    item["cornerConfig"] = _json()
    return ok()


@admin_bp.put("/answer-sheet-template/<int:item_id>/region/<int:region_id>/answers")
@login_required
def template_region_answers(item_id: int, region_id: int):
    item = store.find_by_id("templates", item_id)
    if not item:
        return fail("模板不存在")
    answers = item.setdefault("regionAnswers", {})
    answers[str(region_id)] = _json()
    for region in item.get("regions", []):
        if str(region.get("id")) == str(region_id):
            config = region.setdefault("config", {})
            config["correctAnswers"] = _json()
    return ok()


@admin_bp.get("/answer-sheet-template/<int:item_id>/image")
@login_required
def template_image(item_id: int):
    return ok(f"https://via.placeholder.com/1200x1600?text=Template+Image+{item_id}")


@admin_bp.post("/corner-detection/detect")
@login_required
def corner_detect_post():
    return ok({
        "success": True,
        "topLeftX": 10,
        "topLeftY": 10,
        "topRightX": 790,
        "topRightY": 10,
        "bottomLeftX": 10,
        "bottomLeftY": 1190,
        "bottomRightX": 790,
        "bottomRightY": 1190,
        "angle": 0.2,
        "imageWidth": 800,
        "imageHeight": 1200,
        "correctedImagePath": "corrected/demo.png",
        "correctedImageUrl": "https://via.placeholder.com/800x1200?text=Corrected",
    })


@admin_bp.get("/corner-detection/detect")
@login_required
def corner_detect_get():
    return corner_detect_post()


@admin_bp.post("/corner-detection/correct")
@login_required
def corner_correct():
    return ok("https://via.placeholder.com/800x1200?text=Corrected")


@admin_bp.post("/bubble-detection/detect")
@login_required
def bubble_detect_post():
    return ok({
        "success": True,
        "detectedCount": 20,
        "expectedCount": 20,
        "bubbleMap": [
            {"questionNo": 1, "option": "A", "x": 10, "y": 10, "width": 20, "height": 20, "confidence": 0.99},
            {"questionNo": 2, "option": "C", "x": 40, "y": 10, "width": 20, "height": 20, "confidence": 0.97},
        ],
        "rowCount": 4,
        "bubblesPerRow": 5,
    })


@admin_bp.get("/bubble-detection/detect")
@login_required
def bubble_detect_get():
    return bubble_detect_post()


@admin_bp.post("/file/upload")
@login_required
def file_upload():
    return ok({
        "fileName": "demo.png",
        "originalName": "demo.png",
        "objectName": "common/demo.png",
        "fileSize": 1024,
        "contentType": "image/png",
        "url": "https://via.placeholder.com/800x1200?text=Upload",
    })


@admin_bp.post("/file/upload/batch")
@login_required
def file_upload_batch():
    return ok([{
        "fileName": "demo-1.png",
        "originalName": "demo-1.png",
        "objectName": "common/demo-1.png",
        "fileSize": 1024,
        "contentType": "image/png",
        "url": "https://via.placeholder.com/800x1200?text=Upload+1",
    }])


@admin_bp.delete("/file")
@login_required
def file_delete():
    return ok()


@admin_bp.get("/file/presigned-url")
@login_required
def file_presigned_url():
    return ok("https://via.placeholder.com/800x1200?text=Presigned")


@admin_bp.post("/file/crop/batch/<int:exam_subject_id>")
@login_required
def crop_batch(exam_subject_id: int):
    return ok(message="批量裁切任务已创建")


@admin_bp.post("/file/crop/answer-sheet/<int:answer_sheet_id>")
@login_required
def crop_answer_sheet(answer_sheet_id: int):
    return ok(message="答题卡裁切完成")


@admin_bp.get("/file/crop/progress/<int:exam_subject_id>")
@login_required
def crop_progress(exam_subject_id: int):
    return ok({"total": 10, "completed": 10, "progress": 100})


@admin_bp.post("/file/crop/recrop/<int:answer_sheet_id>/<int:question_id>")
@login_required
def crop_recrop(answer_sheet_id: int, question_id: int):
    return ok(message="重新裁切完成")


@admin_bp.get("/file/crop/statistics/<int:exam_subject_id>")
@login_required
def crop_statistics(exam_subject_id: int):
    return ok({"completed": 10, "pending": 0, "exception": 0})


@admin_bp.get("/file/crop/completed/<int:exam_subject_id>")
@login_required
def crop_completed(exam_subject_id: int):
    return ok(store.list("answer_sheets"))

