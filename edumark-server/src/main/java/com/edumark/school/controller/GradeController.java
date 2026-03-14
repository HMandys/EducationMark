package com.edumark.school.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.school.dto.GradeDTO;
import com.edumark.school.dto.GradeQueryDTO;
import com.edumark.school.service.GradeService;
import com.edumark.school.vo.GradeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 年级管理控制器
 *
 * @author EduMark
 */
@Tag(name = "年级管理")
@RestController
@RequestMapping("/api/grade")
public class GradeController {

    @Resource
    private GradeService gradeService;

    @Operation(summary = "分页查询年级")
    @GetMapping("/page")
    public Result<PageResult<GradeVO>> page(GradeQueryDTO query) {
        return Result.success(gradeService.pageQuery(query));
    }

    @Operation(summary = "获取年级详情")
    @GetMapping("/{id}")
    public Result<GradeVO> getDetail(@PathVariable Long id) {
        return Result.success(gradeService.getDetail(id));
    }

    @Operation(summary = "创建年级")
    @PostMapping
    public Result<Long> create(@RequestBody GradeDTO dto) {
        return Result.success(gradeService.create(dto));
    }

    @Operation(summary = "更新年级")
    @PutMapping
    public Result<Void> update(@RequestBody GradeDTO dto) {
        gradeService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除年级")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除年级")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        gradeService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新年级状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        gradeService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "根据学校ID获取年级列表")
    @GetMapping("/list/{schoolId}")
    public Result<List<GradeVO>> listBySchoolId(@PathVariable Long schoolId) {
        return Result.success(gradeService.listBySchoolId(schoolId));
    }
}
