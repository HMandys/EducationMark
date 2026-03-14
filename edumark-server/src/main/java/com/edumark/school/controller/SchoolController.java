package com.edumark.school.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.school.dto.SchoolDTO;
import com.edumark.school.dto.SchoolQueryDTO;
import com.edumark.school.service.SchoolService;
import com.edumark.school.vo.SchoolVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学校管理控制器
 *
 * @author EduMark
 */
@Tag(name = "学校管理")
@RestController
@RequestMapping("/api/school")
public class SchoolController {

    @Resource
    private SchoolService schoolService;

    @Operation(summary = "分页查询学校")
    @GetMapping("/page")
    public Result<PageResult<SchoolVO>> page(SchoolQueryDTO query) {
        return Result.success(schoolService.pageQuery(query));
    }

    @Operation(summary = "获取学校详情")
    @GetMapping("/{id}")
    public Result<SchoolVO> getDetail(@PathVariable Long id) {
        return Result.success(schoolService.getDetail(id));
    }

    @Operation(summary = "创建学校")
    @PostMapping
    public Result<Long> create(@RequestBody SchoolDTO dto) {
        return Result.success(schoolService.create(dto));
    }

    @Operation(summary = "更新学校")
    @PutMapping
    public Result<Void> update(@RequestBody SchoolDTO dto) {
        schoolService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除学校")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        schoolService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除学校")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        schoolService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "更新学校状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        schoolService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取学校下拉列表")
    @GetMapping("/select")
    public Result<List<SchoolVO>> listForSelect() {
        return Result.success(schoolService.listForSelect());
    }
}
