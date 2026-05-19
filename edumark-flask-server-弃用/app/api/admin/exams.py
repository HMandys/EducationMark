from app.api.admin.common import *


@admin_bp.get("/exam/page")
@login_required
def exam_page():
    return _page("exam")


@admin_bp.get("/exam/<int:item_id>")
@login_required
def exam_detail(item_id: int):
    return _detail("exam", item_id)


@admin_bp.post("/exam")
@login_required
def exam_create():
    payload = _json()
    payload.setdefault("status", 0)
    payload.setdefault("statusName", "草稿")
    payload.setdefault("typeName", "考试")
    item = store.create("exams", _normalize_input("exam", payload))
    exam_id = item["id"]
    for index, subject in enumerate(payload.get("subjects") or [], start=1):
        store.create("exam_subjects", {
            **subject,
            "examId": exam_id,
            "sort": subject.get("sort", index),
            "status": subject.get("status", 1),
        })
    return ok(exam_id)


@admin_bp.put("/exam")
@login_required
def exam_update():
    payload = _json()
    item = store.update("exams", _normalize_input("exam", payload))
    if not item:
        return fail("数据不存在")
    if "subjects" in payload:
        store.data["exam_subjects"] = [subject for subject in store.list("exam_subjects") if subject.get("examId") != item.get("id")]
        for index, subject in enumerate(payload.get("subjects") or [], start=1):
            store.create("exam_subjects", {
                **subject,
                "examId": item.get("id"),
                "sort": subject.get("sort", index),
                "status": subject.get("status", 1),
            })
    return ok()


@admin_bp.delete("/exam/<int:item_id>")
@login_required
def exam_delete(item_id: int):
    return _delete("exam", item_id)


@admin_bp.delete("/exam/batch")
@login_required
def exam_batch_delete():
    return _batch_delete("exam")


@admin_bp.put("/exam/<int:item_id>/status")
@login_required
def exam_status(item_id: int):
    return _update_status("exam", item_id)


@admin_bp.post("/exam/<int:item_id>/publish")
@login_required
def exam_publish(item_id: int):
    exam = store.find_by_id("exams", item_id)
    if not exam:
        return fail("考试不存在")
    exam["status"] = 5
    exam["statusName"] = "已发布"
    return ok()


@admin_bp.get("/exam/<int:item_id>/publish-check")
@login_required
def exam_publish_check(item_id: int):
    subjects = store.list_by("exam_subjects", examId=item_id)
    template_count = len([item for item in store.list("templates") if item.get("examId") == item_id or item.get("paperId") in {subject.get("paperId") for subject in subjects}])
    return ok({
        "examId": item_id,
        "canPublish": bool(subjects),
        "classCount": len({item.get("classId") for item in store.list("exam_scores") if item.get("examId") == item_id}),
        "subjectCount": len(subjects),
        "completedPaperCount": len(store.list_by("answer_sheets", examId=item_id)),
        "publishedTemplateCount": template_count,
        "subjectWithQuestionCount": len(subjects),
        "missingItems": [] if subjects else ["未配置考试科目"],
    })


@admin_bp.post("/exam/<int:item_id>/unpublish")
@login_required
def exam_unpublish(item_id: int):
    exam = store.find_by_id("exams", item_id)
    if not exam:
        return fail("考试不存在")
    exam["status"] = 0
    exam["statusName"] = "草稿"
    return ok()


@admin_bp.get("/exam-subject/list/<int:exam_id>")
@login_required
def exam_subject_list(exam_id: int):
    return ok([_serialize("exam-subject", item) for item in store.list_by("exam_subjects", examId=exam_id)])


@admin_bp.get("/exam-subject/<int:item_id>")
@login_required
def exam_subject_detail(item_id: int):
    return _detail("exam-subject", item_id)


@admin_bp.post("/exam-subject")
@login_required
def exam_subject_create():
    payload = _json()
    payload.setdefault("sort", len(store.list_by("exam_subjects", examId=payload.get("examId"))) + 1)
    payload.setdefault("status", 1)
    return _create("exam-subject", payload)


@admin_bp.put("/exam-subject")
@login_required
def exam_subject_update():
    return _update("exam-subject")


@admin_bp.delete("/exam-subject/<int:item_id>")
@login_required
def exam_subject_delete(item_id: int):
    return _delete("exam-subject", item_id)


@admin_bp.post("/exam-subject/batch/<int:exam_id>")
@login_required
def exam_subject_batch_create(exam_id: int):
    payload = _json()
    for index, item in enumerate(payload or [], start=1):
        store.create("exam_subjects", {
            **item,
            "examId": exam_id,
            "sort": item.get("sort", index),
            "status": item.get("status", 1),
        })
    return ok()


@admin_bp.get("/knowledge-point/page")
@login_required
def knowledge_page():
    return _page("knowledge-point")


@admin_bp.get("/knowledge-point/<int:item_id>")
@login_required
def knowledge_detail(item_id: int):
    return _detail("knowledge-point", item_id)


@admin_bp.post("/knowledge-point")
@login_required
def knowledge_create():
    return _create("knowledge-point")


@admin_bp.put("/knowledge-point")
@login_required
def knowledge_update():
    return _update("knowledge-point")


@admin_bp.delete("/knowledge-point/<int:item_id>")
@login_required
def knowledge_delete(item_id: int):
    return _delete("knowledge-point", item_id)


@admin_bp.delete("/knowledge-point/batch")
@login_required
def knowledge_batch_delete():
    return _batch_delete("knowledge-point")


@admin_bp.get("/knowledge-point/tree")
@login_required
def knowledge_tree():
    roots = [item for item in store.list("knowledge_points") if item.get("parentId", 0) == 0]
    return ok([_serialize("knowledge-point", item) for item in roots])


@admin_bp.get("/knowledge-point/list")
@login_required
def knowledge_list():
    params = request.args.to_dict()
    school_id = params.get("schoolId")
    subject_name = params.get("subjectName")
    items = store.list("knowledge_points")
    if school_id:
        items = [item for item in items if str(item.get("schoolId")) == str(school_id)]
    if subject_name:
        items = [item for item in items if item.get("subjectName") == subject_name]
    return ok([_serialize("knowledge-point", item) for item in items])


