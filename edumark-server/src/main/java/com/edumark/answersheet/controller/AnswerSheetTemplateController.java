package com.edumark.answersheet.controller;

import com.edumark.answersheet.dto.AnswerSheetTemplateDTO;
import com.edumark.answersheet.dto.AnswerSheetTemplateQueryDTO;
import com.edumark.answersheet.service.AnswerSheetTemplateService;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateValidateVO;
import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 答题卡模板控制器
 *
 * @author EduMark
 */
@Tag(name = "答题卡模板管理")
@RestController
@RequestMapping("/answer-sheet-template")
public class AnswerSheetTemplateController {

    @Resource
    private AnswerSheetTemplateService templateService;

    @Operation(summary = "分页查询模板列表")
    @GetMapping("/page")
    public Result<PageResult<AnswerSheetTemplateVO>> page(AnswerSheetTemplateQueryDTO query) {
        return Result.success(templateService.pageQuery(query));
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public Result<AnswerSheetTemplateVO> getDetail(@PathVariable Long id) {
        return Result.success(templateService.getDetail(id));
    }

    @Operation(summary = "根据试卷ID获取模板")
    @GetMapping("/paper/{paperId}")
    public Result<AnswerSheetTemplateVO> getByPaperId(@PathVariable Long paperId) {
        return Result.success(templateService.getByPaperId(paperId));
    }

    @Operation(summary = "创建模板")
    @PostMapping
    public Result<Long> create(@RequestBody @Valid AnswerSheetTemplateDTO dto) {
        return Result.success(templateService.create(dto));
    }

    @Operation(summary = "更新模板")
    @PutMapping
    public Result<Void> update(@RequestBody @Valid AnswerSheetTemplateDTO dto) {
        templateService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success();
    }

    @Operation(summary = "根据试卷自动生成模板")
    @PostMapping("/generate/{paperId}")
    public Result<Long> generateFromPaper(@PathVariable Long paperId) {
        return Result.success(templateService.generateFromPaper(paperId));
    }

    @Operation(summary = "校验模板完整性")
    @GetMapping("/{id}/validate")
    public Result<AnswerSheetTemplateValidateVO> validate(@PathVariable Long id) {
        return Result.success(templateService.validateTemplate(id));
    }

    @Operation(summary = "发布模板（生成PDF）")
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        templateService.publish(id);
        return Result.success();
    }

    @Operation(summary = "获取PDF预览URL")
    @GetMapping("/{id}/preview")
    public Result<String> preview(@PathVariable Long id) {
        return Result.success(templateService.getPreviewUrl(id));
    }

    @Operation(summary = "获取PDF下载URL")
    @GetMapping("/{id}/download")
    public Result<String> download(@PathVariable Long id) {
        return Result.success(templateService.getDownloadUrl(id));
    }

    @Operation(summary = "上传模板图片")
    @PostMapping("/{id}/upload-image")
    public Result<String> uploadImage(
            @PathVariable Long id,
            @RequestParam String imagePath) {
        return Result.success(templateService.uploadTemplateImage(id, imagePath));
    }

    @Operation(summary = "保存四角定位配置")
    @PutMapping("/{id}/corner-config")
    public Result<Void> saveCornerConfig(
            @PathVariable Long id,
            @RequestBody Map<String, Object> cornerConfig) {
        templateService.saveCornerConfig(id, cornerConfig);
        return Result.success();
    }

    @Operation(summary = "保存区域正确答案")
    @PutMapping("/{id}/region/{regionId}/answers")
    public Result<Void> saveRegionAnswers(
            @PathVariable Long id,
            @PathVariable Long regionId,
            @RequestBody Map<String, String> correctAnswers) {
        templateService.saveRegionCorrectAnswers(id, regionId, correctAnswers);
        return Result.success();
    }

    @Operation(summary = "获取模板图片URL")
    @GetMapping("/{id}/image")
    public Result<String> getTemplateImage(@PathVariable Long id) {
        return Result.success(templateService.getTemplateImageUrl(id));
    }
}
