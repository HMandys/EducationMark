package com.edumark.school.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.school.dto.TeacherDTO;
import com.edumark.school.dto.TeacherQueryDTO;
import com.edumark.school.service.TeacherService;
import com.edumark.school.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师管理控制器
 *
 * @author EduMark
 */
@Tag(name = "教师管理")
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    @Operation(summary = "分页查询教师")
    @GetMapping("/page")
    public Result<PageResult<TeacherVO>> page(TeacherQueryDTO query) {
        return Result.success(teacherService.pageQuery(query));
    }

    @Operation(summary = "获取教师详情")
    @GetMapping("/{id}")
    public Result<TeacherVO> getDetail(@PathVariable Long id) {
        return Result.success(teacherService.getDetail(id));
    }

    @Operation(summary = "创建教师")
    @PostMapping
    public Result<Long> create(@RequestBody TeacherDTO dto) {
        return Result.success(teacherService.create(dto));
    }

    @Operation(summary = "更新教师")
    @PutMapping
    public Result<Void> update(@RequestBody TeacherDTO dto) {
        teacherService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teacherService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除教师")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        teacherService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新教师状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        teacherService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "根据学校ID获取教师列表")
    @GetMapping("/list/{schoolId}")
    public Result<List<TeacherVO>> listBySchoolId(@PathVariable Long schoolId) {
        return Result.success(teacherService.listBySchoolId(schoolId));
    }
}
