package com.edumark.file.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.file.dto.AnswerSheetDTO;
import com.edumark.file.dto.AnswerSheetQueryDTO;
import com.edumark.file.dto.AnswerSheetUploadDTO;
import com.edumark.file.service.AnswerSheetService;
import com.edumark.file.vo.AnswerSheetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 答题卡管理控制器
 *
 * @author EduMark
 */
@Tag(name = "答题卡管理")
@RestController
@RequestMapping("/api/answer-sheet")
public class AnswerSheetController {

    @Resource
    private AnswerSheetService answerSheetService;

    @Operation(summary = "分页查询答题卡")
    @GetMapping("/page")
    public Result<PageResult<AnswerSheetVO>> page(AnswerSheetQueryDTO query) {
        return Result.success(answerSheetService.pageQuery(query));
    }

    @Operation(summary = "获取答题卡详情")
    @GetMapping("/{id}")
    public Result<AnswerSheetVO> getDetail(@PathVariable Long id) {
        return Result.success(answerSheetService.getDetail(id));
    }

    @Operation(summary = "创建答题卡")
    @PostMapping
    public Result<Long> create(@RequestBody AnswerSheetDTO dto) {
        return Result.success(answerSheetService.create(dto));
    }

    @Operation(summary = "更新答题卡")
    @PutMapping
    public Result<Void> update(@RequestBody AnswerSheetDTO dto) {
        answerSheetService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除答题卡")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        answerSheetService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除答题卡")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        answerSheetService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "上传答题卡图片")
    @PostMapping("/{id}/images")
    public Result<AnswerSheetVO> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        return Result.success(answerSheetService.uploadImages(id, files));
    }

    @Operation(summary = "上传答题卡（含图片）")
    @PostMapping("/upload")
    public Result<Long> uploadAnswerSheet(@RequestBody AnswerSheetUploadDTO dto) {
        return Result.success(answerSheetService.uploadAnswerSheet(dto));
    }

    @Operation(summary = "删除答题卡图片")
    @DeleteMapping("/image/{imageId}")
    public Result<Void> deleteImage(@PathVariable Long imageId) {
        answerSheetService.deleteImage(imageId);
        return Result.success();
    }

    @Operation(summary = "根据考试科目查询答题卡列表")
    @GetMapping("/list/{examSubjectId}")
    public Result<List<AnswerSheetVO>> listByExamSubject(@PathVariable Long examSubjectId) {
        return Result.success(answerSheetService.listByExamSubjectId(examSubjectId));
    }

    @Operation(summary = "更新答题卡状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        answerSheetService.updateStatus(id, status);
        return Result.success();
    }
}
