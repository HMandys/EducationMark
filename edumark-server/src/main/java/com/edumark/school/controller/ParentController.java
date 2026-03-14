package com.edumark.school.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.school.dto.ParentBindDTO;
import com.edumark.school.dto.ParentDTO;
import com.edumark.school.dto.ParentQueryDTO;
import com.edumark.school.service.ParentService;
import com.edumark.school.vo.ParentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家长管理控制器
 *
 * @author EduMark
 */
@Tag(name = "家长管理")
@RestController
@RequestMapping("/api/parent")
public class ParentController {

    @Resource
    private ParentService parentService;

    @Operation(summary = "分页查询家长")
    @GetMapping("/page")
    public Result<PageResult<ParentVO>> page(ParentQueryDTO query) {
        return Result.success(parentService.pageQuery(query));
    }

    @Operation(summary = "获取家长详情")
    @GetMapping("/{id}")
    public Result<ParentVO> getDetail(@PathVariable Long id) {
        return Result.success(parentService.getDetail(id));
    }

    @Operation(summary = "创建家长")
    @PostMapping
    public Result<Long> create(@RequestBody ParentDTO dto) {
        return Result.success(parentService.create(dto));
    }

    @Operation(summary = "更新家长")
    @PutMapping
    public Result<Void> update(@RequestBody ParentDTO dto) {
        parentService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除家长")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        parentService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除家长")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        parentService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新家长状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        parentService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "家长绑定学生")
    @PostMapping("/{parentId}/bind")
    public Result<Void> bindStudent(@PathVariable Long parentId, @RequestBody ParentBindDTO dto) {
        parentService.bindStudent(parentId, dto);
        return Result.success();
    }

    @Operation(summary = "家长解绑学生")
    @DeleteMapping("/{parentId}/unbind/{studentId}")
    public Result<Void> unbindStudent(@PathVariable Long parentId, @PathVariable Long studentId) {
        parentService.unbindStudent(parentId, studentId);
        return Result.success();
    }

    @Operation(summary = "获取家长绑定的学生列表")
    @GetMapping("/{parentId}/students")
    public Result<List<ParentVO.StudentBindVO>> getBoundStudents(@PathVariable Long parentId) {
        return Result.success(parentService.getBoundStudents(parentId));
    }
}
