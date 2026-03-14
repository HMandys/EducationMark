package com.edumark.school.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.school.dto.ClassInfoDTO;
import com.edumark.school.dto.ClassInfoQueryDTO;
import com.edumark.school.service.ClassInfoService;
import com.edumark.school.vo.ClassInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 *
 * @author EduMark
 */
@Tag(name = "班级管理")
@RestController
@RequestMapping("/api/class")
public class ClassInfoController {

    @Resource
    private ClassInfoService classInfoService;

    @Operation(summary = "分页查询班级")
    @GetMapping("/page")
    public Result<PageResult<ClassInfoVO>> page(ClassInfoQueryDTO query) {
        return Result.success(classInfoService.pageQuery(query));
    }

    @Operation(summary = "获取班级详情")
    @GetMapping("/{id}")
    public Result<ClassInfoVO> getDetail(@PathVariable Long id) {
        return Result.success(classInfoService.getDetail(id));
    }

    @Operation(summary = "创建班级")
    @PostMapping
    public Result<Long> create(@RequestBody ClassInfoDTO dto) {
        return Result.success(classInfoService.create(dto));
    }

    @Operation(summary = "更新班级")
    @PutMapping
    public Result<Void> update(@RequestBody ClassInfoDTO dto) {
        classInfoService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classInfoService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除班级")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        classInfoService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新班级状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        classInfoService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "根据年级ID获取班级列表")
    @GetMapping("/list/grade/{gradeId}")
    public Result<List<ClassInfoVO>> listByGradeId(@PathVariable Long gradeId) {
        return Result.success(classInfoService.listByGradeId(gradeId));
    }

    @Operation(summary = "根据学校ID获取班级列表")
    @GetMapping("/list/school/{schoolId}")
    public Result<List<ClassInfoVO>> listBySchoolId(@PathVariable Long schoolId) {
        return Result.success(classInfoService.listBySchoolId(schoolId));
    }
}
