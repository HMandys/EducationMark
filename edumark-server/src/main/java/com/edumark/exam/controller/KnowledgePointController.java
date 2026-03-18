package com.edumark.exam.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.exam.dto.KnowledgePointDTO;
import com.edumark.exam.dto.KnowledgePointQueryDTO;
import com.edumark.exam.service.KnowledgePointService;
import com.edumark.exam.vo.KnowledgePointVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识点管理控制器
 *
 * @author EduMark
 */
@Tag(name = "知识点管理")
@RestController
@RequestMapping("/knowledge-point")
public class KnowledgePointController {

    @Resource
    private KnowledgePointService knowledgePointService;

    @Operation(summary = "分页查询知识点")
    @GetMapping("/page")
    public Result<PageResult<KnowledgePointVO>> page(KnowledgePointQueryDTO query) {
        return Result.success(knowledgePointService.pageQuery(query));
    }

    @Operation(summary = "获取知识点详情")
    @GetMapping("/{id}")
    public Result<KnowledgePointVO> getDetail(@PathVariable Long id) {
        return Result.success(knowledgePointService.getDetail(id));
    }

    @Operation(summary = "创建知识点")
    @PostMapping
    public Result<Long> create(@RequestBody KnowledgePointDTO dto) {
        return Result.success(knowledgePointService.create(dto));
    }

    @Operation(summary = "更新知识点")
    @PutMapping
    public Result<Void> update(@RequestBody KnowledgePointDTO dto) {
        knowledgePointService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除知识点")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        knowledgePointService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除知识点")
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        knowledgePointService.deleteBatch(ids);
        return Result.success();
    }

    @Operation(summary = "获取知识点树")
    @GetMapping("/tree")
    public Result<List<KnowledgePointVO>> getTree(
            @RequestParam Long schoolId,
            @RequestParam String subjectName) {
        return Result.success(knowledgePointService.getTree(schoolId, subjectName));
    }

    @Operation(summary = "根据科目获取知识点列表")
    @GetMapping("/list")
    public Result<List<KnowledgePointVO>> listBySubject(
            @RequestParam Long schoolId,
            @RequestParam String subjectName) {
        return Result.success(knowledgePointService.listBySubject(schoolId, subjectName));
    }
}
