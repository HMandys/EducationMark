package com.edumark.exam.controller;

import com.edumark.common.result.Result;
import com.edumark.exam.dto.ExamSubjectDTO;
import com.edumark.exam.service.ExamSubjectService;
import com.edumark.exam.vo.ExamSubjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试科目管理控制器
 *
 * @author EduMark
 */
@Tag(name = "考试科目管理")
@RestController
@RequestMapping("/api/exam-subject")
public class ExamSubjectController {

    @Resource
    private ExamSubjectService examSubjectService;

    @Operation(summary = "根据考试ID查询科目列表")
    @GetMapping("/list/{examId}")
    public Result<List<ExamSubjectVO>> listByExamId(@PathVariable Long examId) {
        return Result.success(examSubjectService.listByExamId(examId));
    }

    @Operation(summary = "获取科目详情")
    @GetMapping("/{id}")
    public Result<ExamSubjectVO> getDetail(@PathVariable Long id) {
        return Result.success(examSubjectService.getDetail(id));
    }

    @Operation(summary = "创建科目")
    @PostMapping
    public Result<Long> create(@RequestBody ExamSubjectDTO dto) {
        return Result.success(examSubjectService.create(dto));
    }

    @Operation(summary = "更新科目")
    @PutMapping
    public Result<Void> update(@RequestBody ExamSubjectDTO dto) {
        examSubjectService.update(dto);
        return Result.success();
    }

    @Operation(summary = "删除科目")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examSubjectService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量添加科目")
    @PostMapping("/batch/{examId}")
    public Result<Void> batchCreate(@PathVariable Long examId, @RequestBody List<ExamSubjectDTO> subjects) {
        examSubjectService.batchCreate(examId, subjects);
        return Result.success();
    }
}
