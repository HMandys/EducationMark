package com.edumark.app.controller;

import com.edumark.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * App答题卡控制器
 *
 * @author EduMark
 */
@Tag(name = "App-答题卡查看")
@RestController
@RequestMapping("/app/answer-sheet")
public class AppAnswerSheetController {

    @Operation(summary = "获取学生的答题卡列表")
    @GetMapping("/list/{examId}/{studentId}")
    public Result<List<Map<String, Object>>> getAnswerSheetList(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        // TODO: 查询学生在该考试中的答题卡列表（按科目分组）
        // 每个答题卡包含：id, examSubjectId, subjectName, totalScore, images[]
        List<Map<String, Object>> list = new ArrayList<>();
        return Result.success(list);
    }

    @Operation(summary = "获取答题卡详情")
    @GetMapping("/{answerSheetId}")
    public Result<Map<String, Object>> getAnswerSheetDetail(@PathVariable Long answerSheetId) {
        // TODO: 查询答题卡详情，包含图片列表
        return Result.success(null);
    }

    @Operation(summary = "获取答题卡图片列表")
    @GetMapping("/images/{answerSheetId}")
    public Result<List<Map<String, Object>>> getAnswerSheetImages(@PathVariable Long answerSheetId) {
        // TODO: 查询答题卡图片列表
        // 返回签名后的MinIO图片URL
        List<Map<String, Object>> list = new ArrayList<>();
        return Result.success(list);
    }
}
