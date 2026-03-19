package com.edumark.exam.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.exam.dto.ExamDTO;
import com.edumark.exam.dto.ExamQueryDTO;
import com.edumark.exam.service.ExamService;
import com.edumark.exam.vo.ExamPublishCheckVO;
import com.edumark.exam.vo.ExamVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试管理控制器
 *
 * @author EduMark
 */
@Tag(name = "考试管理")
@RestController
@RequestMapping("/exam")
public class ExamController {

    @Resource
    private ExamService examService;

    @Operation(summary = "分页查询考试")
    @GetMapping("/page")
    public Result<PageResult<ExamVO>> page(ExamQueryDTO query) {
        return Result.success(examService.pageQuery(query));
    }

    @Operation(summary = "获取考试详情")
    @GetMapping("/{id}")
    public Result<ExamVO> getDetail(@PathVariable Long id) {
        return Result.success(examService.getDetail(id));
    }

    @Operation(summary = "创建考试")
    @PostMapping
    public Result<Long> create(@RequestBody ExamDTO dto) {
        return Result.success(examService.create(dto));
    }

    @Operation(summary = "更新考试")
    @PutMapping
    public Result<Void> update(@RequestBody ExamDTO dto) {
        examService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除考试")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除考试")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        examService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新考试状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        examService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "发布考试")
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        examService.publish(id);
        return Result.success();
    }

    @Operation(summary = "发布前检查")
    @GetMapping("/{id}/publish-check")
    public Result<ExamPublishCheckVO> publishCheck(@PathVariable Long id) {
        return Result.success(examService.publishCheck(id));
    }

    @Operation(summary = "撤回发布")
    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublish(@PathVariable Long id) {
        examService.unpublish(id);
        return Result.success();
    }
}
