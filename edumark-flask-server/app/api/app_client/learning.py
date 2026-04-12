from app.api.app_client.common import *


@app_bp.get("/app/score/recent/<int:student_id>")
@login_required
def app_recent_scores(student_id: int):
    allowed, error = _ensure_student_access(student_id)
    if not allowed:
        return error
    limit = int(request.args.get("limit", 5))
    scores = [item for item in store.list("exam_scores") if item["studentId"] == student_id]
    scores = sorted(scores, key=lambda item: item.get("updateTime", ""), reverse=True)[:limit]
    return ok([_exam_score_payload(item) for item in scores])


@app_bp.get("/app/score/statistics/<int:exam_id>")
@login_required
def app_score_statistics(exam_id: int):
    class_id = request.args.get("classId")
    scores = [item for item in store.list("exam_scores") if item["examId"] == exam_id]
    if class_id:
        scores = [item for item in scores if str(item.get("classId")) == str(class_id)]
    if not scores:
        return ok([])
    return ok([{
        "examId": exam_id,
        "classId": class_id,
        "studentCount": len(scores),
        "maxScore": max(item["totalScore"] for item in scores),
        "minScore": min(item["totalScore"] for item in scores),
        "avgScore": round(sum(item["totalScore"] for item in scores) / len(scores), 2),
    }])


@app_bp.get("/app/answer-sheet/list/<int:exam_id>/<int:student_id>")
@login_required
def app_answer_sheet_list(exam_id: int, student_id: int):
    allowed, error = _ensure_student_access(student_id)
    if not allowed:
        return error
    sheets = [item for item in store.list("answer_sheets") if item["examId"] == exam_id and item["studentId"] == student_id]
    return ok([_answer_sheet_payload(sheet) for sheet in sheets])


@app_bp.get("/app/answer-sheet/<int:answer_sheet_id>")
@login_required
def app_answer_sheet_detail(answer_sheet_id: int):
    sheet = store.find_by_id("answer_sheets", answer_sheet_id)
    if not sheet:
        return fail("答题卡不存在")
    allowed, error = _ensure_student_access(sheet["studentId"])
    if not allowed:
        return error
    return ok(_answer_sheet_payload(sheet))


@app_bp.get("/app/answer-sheet/images/<int:answer_sheet_id>")
@login_required
def app_answer_sheet_images(answer_sheet_id: int):
    sheet = store.find_by_id("answer_sheets", answer_sheet_id)
    if not sheet:
        return fail("答题卡不存在")
    allowed, error = _ensure_student_access(sheet["studentId"])
    if not allowed:
        return error
    return ok([_answer_sheet_image_payload(item) for item in store.list("answer_sheet_images") if item["answerSheetId"] == answer_sheet_id])


@app_bp.get("/app/ai/report/<int:student_id>")
@login_required
def app_ai_report(student_id: int):
    allowed, error = _ensure_student_access(student_id)
    if not allowed:
        return error
    limit = int(request.args.get("limit", 10))
    recent = [item for item in store.list("exam_scores") if item["studentId"] == student_id]
    recent = sorted(recent, key=lambda item: item.get("updateTime", ""), reverse=True)[:limit]
    if not recent:
        return fail("暂无成绩数据，无法生成AI分析")

    subject_bucket: dict[str, list[float]] = defaultdict(list)
    for score in recent:
        for subject in _subject_scores(score["examId"], student_id):
            subject_bucket[subject["subjectName"]].append(float(subject["score"]))

    ordered = sorted(
        [{"name": name, "avg": round(sum(values) / len(values), 1)} for name, values in subject_bucket.items()],
        key=lambda item: item["avg"],
        reverse=True,
    )
    best = ordered[0]["name"] if ordered else "暂无"
    weakest = ordered[-1]["name"] if ordered else "暂无"
    latest = recent[0]
    avg_total = round(sum(item["totalScore"] for item in recent) / len(recent), 1)
    trend = "样本不足"
    if len(recent) >= 2:
        if recent[0]["totalScore"] > recent[1]["totalScore"]:
            trend = "较上次有所提升"
        elif recent[0]["totalScore"] < recent[1]["totalScore"]:
            trend = "较上次有所回落"
        else:
            trend = "与上次基本持平"

    enabled_provider = next((item for item in store.list("ai_providers") if item.get("enabled") == 1 and item.get("isDefault") == 1), None)
    return ok({
        "studentId": student_id,
        "studentName": latest["studentName"],
        "summary": f"最近 {len(recent)} 次考试平均总分 {avg_total} 分，当前趋势为“{trend}”。优势学科偏向 {best}，建议重点关注 {weakest}。",
        "strengths": [
            f"{best} 表现相对稳定，属于当前优势学科。",
            "最近考试整体分数处于上升或稳定区间。",
        ],
        "weaknesses": [
            f"{weakest} 相对薄弱，建议优先补齐基础题失分。",
            "若大题失分偏多，建议加强限时训练与复盘。",
        ],
        "suggestions": [
            f"先巩固 {weakest} 的核心知识点，再做针对性练习。",
            "每次考试后整理错题，按知识点复盘失分原因。",
            "保持优势学科训练频率，避免高分科目回落。",
        ],
        "latestTotalScore": latest["totalScore"],
        "averageTotalScore": avg_total,
        "trend": trend,
        "providerName": enabled_provider.get("providerName") if enabled_provider else "Mock AI",
        "protocol": enabled_provider.get("protocol") if enabled_provider else "openai-compatible",
        "model": enabled_provider.get("model") if enabled_provider else "mock-model",
        "generatedAt": store.now(),
    })
