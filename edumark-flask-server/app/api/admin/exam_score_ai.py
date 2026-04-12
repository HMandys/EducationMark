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


@admin_bp.get("/score/exam/page")
@login_required
def score_exam_page():
    items, total, page_num, page_size = store.page("exam_scores", request.args.to_dict(), ["examName", "studentName", "studentNumber", "className"])
    return page_result([_exam_score_payload(item) for item in items], total, page_num, page_size)


@admin_bp.get("/score/subject/page")
@login_required
def score_subject_page():
    items, total, page_num, page_size = store.page("subject_scores", request.args.to_dict(), ["subjectName", "studentName", "studentNumber"])
    return page_result([_subject_score_payload(item) for item in items], total, page_num, page_size)


@admin_bp.get("/score/student/<int:exam_id>/<int:student_id>")
@login_required
def score_student_detail(exam_id: int, student_id: int):
    item = next((score for score in store.list("exam_scores") if score["examId"] == exam_id and score["studentId"] == student_id), None)
    if not item:
        return fail("成绩不存在")
    return ok(_exam_score_payload(item))


@admin_bp.get("/score/statistics/<int:exam_id>")
@login_required
def score_statistics(exam_id: int):
    class_id = request.args.get("classId")
    exam_subject_id = request.args.get("examSubjectId")
    if exam_subject_id:
        scores = [item for item in store.list("subject_scores") if item["examId"] == exam_id and str(item.get("examSubjectId")) == str(exam_subject_id)]
    else:
        scores = [item for item in store.list("exam_scores") if item["examId"] == exam_id]
    if class_id:
        scores = [item for item in scores if str(item.get("classId")) == str(class_id)]
    if not scores:
        return ok([])
    score_field = "score" if exam_subject_id else "totalScore"
    total_score = sum(item[score_field] for item in scores)
    max_score = max(item[score_field] for item in scores)
    min_score = min(item[score_field] for item in scores)
    full_score = scores[0].get("fullScore") if exam_subject_id else None
    pass_line = 0.6 * full_score if full_score else 180
    excellent_line = 0.85 * full_score if full_score else 255
    segment_list = [
        {"range": "90-100", "count": len([item for item in scores if item[score_field] >= (90 if exam_subject_id else 270)])},
        {"range": "80-89", "count": len([item for item in scores if (80 if exam_subject_id else 240) <= item[score_field] < (90 if exam_subject_id else 270)])},
        {"range": "60-79", "count": len([item for item in scores if (60 if exam_subject_id else 180) <= item[score_field] < (80 if exam_subject_id else 240)])},
        {"range": "0-59", "count": len([item for item in scores if item[score_field] < (60 if exam_subject_id else 180)])},
    ]
    return ok([{
        "id": 1,
        "examId": exam_id,
        "examName": (store.find_by_id("exams", exam_id) or {}).get("name"),
        "examSubjectId": int(exam_subject_id) if exam_subject_id else None,
        "subjectName": (store.find_by_id("exam_subjects", exam_subject_id) or {}).get("subjectName") if exam_subject_id else None,
        "classId": int(class_id) if class_id else None,
        "className": _class_name(class_id) if class_id else None,
        "statType": 1 if exam_subject_id and class_id else 2 if class_id else 3 if exam_subject_id else 4,
        "studentCount": len(scores),
        "fullScore": full_score,
        "maxScore": max_score,
        "minScore": min_score,
        "avgScore": round(total_score / len(scores), 2),
        "passCount": len([item for item in scores if item[score_field] >= pass_line]),
        "passRate": round(len([item for item in scores if item[score_field] >= pass_line]) * 100 / len(scores), 2),
        "excellentCount": len([item for item in scores if item[score_field] >= excellent_line]),
        "excellentRate": round(len([item for item in scores if item[score_field] >= excellent_line]) * 100 / len(scores), 2),
        "scoreSegments": {segment["range"]: segment["count"] for segment in segment_list},
        "segmentList": segment_list,
    }])


@admin_bp.get("/score/publish-check/<int:exam_id>")
@login_required
def score_publish_check(exam_id: int):
    answer_sheet_count = len(store.list_by("answer_sheets", examId=exam_id))
    subject_score_count = len([item for item in store.list("subject_scores") if item.get("examId") == exam_id])
    return ok({
        "examId": exam_id,
        "examName": (store.find_by_id("exams", exam_id) or {}).get("name"),
        "examStatus": (store.find_by_id("exams", exam_id) or {}).get("status"),
        "canPublish": True,
        "subjectCount": len(store.list_by("exam_subjects", examId=exam_id)),
        "answerSheetCount": answer_sheet_count,
        "completedAnswerSheetCount": answer_sheet_count,
        "pendingRecognitionCount": 0,
        "recognitionExceptionCount": 0,
        "pendingMarkingAnswerSheetCount": 0,
        "markingTaskCount": len(store.list("marking_tasks")),
        "unfinishedTaskCount": 0,
        "pendingArbitrationCount": 0,
        "examScoreCount": len(store.list_by("exam_scores", examId=exam_id)),
        "subjectScoreCount": subject_score_count,
        "statisticsCount": 1 if subject_score_count else 0,
        "blockingItems": [],
        "warningItems": [],
    })


@admin_bp.post("/score/aggregate/<int:exam_id>")
@login_required
def score_aggregate(exam_id: int):
    return ok(message="成绩汇总完成")


@admin_bp.post("/score/ranking/<int:exam_id>")
@login_required
def score_ranking(exam_id: int):
    return ok(message="排名计算完成")


@admin_bp.post("/score/statistics/<int:exam_id>")
@login_required
def score_statistics_generate(exam_id: int):
    return ok(message="统计分析完成")


@admin_bp.post("/score/publish/<int:exam_id>")
@login_required
def score_publish(exam_id: int):
    return ok(message="成绩发布成功")


@admin_bp.post("/score/unpublish/<int:exam_id>")
@login_required
def score_unpublish(exam_id: int):
    return ok(message="成绩取消发布成功")


@admin_bp.get("/score/export/<int:exam_id>")
@login_required
def score_export(exam_id: int):
    return ok(f"/exports/score-{exam_id}.xlsx")


@admin_bp.get("/system/ai-marking/provider/page")
@login_required
def ai_provider_page():
    items, total, page_num, page_size = store.page("ai_providers", request.args.to_dict(), ["providerName", "protocol", "model"])
    for item in items:
        api_key = item.get("apiKey", "")
        item["apiKeyMasked"] = f"{api_key[:4]}****{api_key[-4:]}" if api_key else ""
        item["hasApiKey"] = bool(api_key)
        item["updateTime"] = item.get("updateTime") or item.get("createTime")
    return page_result(items, total, page_num, page_size)


@admin_bp.post("/system/ai-marking/provider")
@login_required
def ai_provider_create():
    payload = _json()
    if payload.get("isDefault") == 1:
        for item in store.list("ai_providers"):
            item["isDefault"] = 0
    payload.setdefault("enabled", 0)
    payload.setdefault("priority", 0)
    item = store.create("ai_providers", payload)
    return ok(item["id"])


@admin_bp.put("/system/ai-marking/provider")
@login_required
def ai_provider_update():
    payload = _json()
    if payload.get("isDefault") == 1:
        for item in store.list("ai_providers"):
            if item["id"] != payload.get("id"):
                item["isDefault"] = 0
    item = store.update("ai_providers", payload)
    if not item:
        return fail("提供商不存在")
    return ok()


@admin_bp.delete("/system/ai-marking/provider/<int:item_id>")
@login_required
def ai_provider_delete(item_id: int):
    store.delete("ai_providers", item_id)
    return ok()


@admin_bp.get("/system/ai-marking/record/page")
@login_required
def ai_record_page():
    items, total, page_num, page_size = store.page("ai_records", request.args.to_dict(), ["providerName", "protocol", "model", "errorMessage"])
    for item in items:
        item.setdefault("questionNo", item.get("questionNo"))
        item.setdefault("status", 1)
    return page_result(items, total, page_num, page_size)


@admin_bp.get("/system/ai-marking/policy")
@login_required
def ai_policy_detail():
    return ok(store.policy)


@admin_bp.put("/system/ai-marking/policy")
@login_required
def ai_policy_update():
    store.policy.update(_json())
    return ok()


