package com.edumark.file.controller;

import com.edumark.common.result.Result;
import com.edumark.file.service.CropService;
import com.edumark.file.vo.CropProgressVO;
import com.edumark.file.vo.CropResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 裁题管理Controller
 *
 * @author EduMark
 */
@Tag(name = "裁题管理")
@RestController
@RequestMapping("/file/crop")
public class CropController {

    @Resource
    private CropService cropService;

    @Operation(summary = "批量裁题 - 按考试科目")
    @PostMapping("/batch/{examSubjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CropProgressVO> batchCrop(@PathVariable Long examSubjectId) {
        return Result.success(cropService.batchCropByExamSubject(examSubjectId));
    }

    @Operation(summary = "裁题 - 按答题卡")
    @PostMapping("/answer-sheet/{answerSheetId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CropResultVO> cropByAnswerSheet(@PathVariable Long answerSheetId) {
        return Result.success(cropService.cropByAnswerSheet(answerSheetId));
    }

    @Operation(summary = "获取裁题进度")
    @GetMapping("/progress/{examSubjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CropProgressVO> getCropProgress(@PathVariable Long examSubjectId) {
        return Result.success(cropService.getCropProgress(examSubjectId));
    }

    @Operation(summary = "重新裁题 - 按题目")
    @PostMapping("/recrop/{answerSheetId}/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<String> recropQuestion(@PathVariable Long answerSheetId, @PathVariable Long questionId) {
        return Result.success(cropService.recropQuestion(answerSheetId, questionId));
    }

    @Operation(summary = "获取裁题统计")
    @GetMapping("/statistics/{examSubjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<CropProgressVO> getCropStatistics(@PathVariable Long examSubjectId) {
        return Result.success(cropService.getCropStatistics(examSubjectId));
    }

    @Operation(summary = "检查裁题是否完成")
    @GetMapping("/completed/{examSubjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Boolean> isCropCompleted(@PathVariable Long examSubjectId) {
        return Result.success(cropService.isCropCompleted(examSubjectId));
    }
}
