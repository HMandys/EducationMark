package com.edumark.school.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.school.dto.StudentDTO;
import com.edumark.school.dto.StudentQueryDTO;
import com.edumark.school.service.StudentService;
import com.edumark.school.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生管理控制器
 *
 * @author EduMark
 */
@Tag(name = "学生管理")
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    @Operation(summary = "分页查询学生")
    @GetMapping("/page")
    public Result<PageResult<StudentVO>> page(StudentQueryDTO query) {
        return Result.success(studentService.pageQuery(query));
    }

    @Operation(summary = "获取学生详情")
    @GetMapping("/{id}")
    public Result<StudentVO> getDetail(@PathVariable Long id) {
        return Result.success(studentService.getDetail(id));
    }

    @Operation(summary = "创建学生")
    @PostMapping
    public Result<Long> create(@RequestBody StudentDTO dto) {
        return Result.success(studentService.create(dto));
    }

    @Operation(summary = "更新学生")
    @PutMapping
    public Result<Void> update(@RequestBody StudentDTO dto) {
        studentService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除学生")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新学生状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        studentService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "根据班级ID获取学生列表")
    @GetMapping("/list/{classId}")
    public Result<List<StudentVO>> listByClassId(@PathVariable Long classId) {
        return Result.success(studentService.listByClassId(classId));
    }

    @Operation(summary = "刷新学生绑定码")
    @PostMapping("/{id}/refresh-bind-code")
    public Result<String> refreshBindCode(@PathVariable Long id) {
        return Result.success(studentService.refreshBindCode(id));
    }
}
