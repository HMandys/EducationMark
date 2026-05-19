from app.api.admin.common import *

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

