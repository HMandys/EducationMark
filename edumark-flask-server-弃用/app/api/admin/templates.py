from app.api.admin.common import *

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


