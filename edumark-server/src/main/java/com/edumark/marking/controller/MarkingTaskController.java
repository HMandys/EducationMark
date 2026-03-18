package com.edumark.marking.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.marking.dto.MarkingTaskAssignDTO;
import com.edumark.marking.dto.MarkingTaskQueryDTO;
import com.edumark.marking.service.MarkingTaskService;
import com.edumark.marking.vo.MarkingTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅卷任务管理Controller
 *
 * @author EduMark
 */
@Tag(name = "阅卷任务管理")
@RestController
@RequestMapping("/marking/task")
public class MarkingTaskController {

    @Resource
    private MarkingTaskService markingTaskService;

    @Operation(summary = "分页查询阅卷任务")
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<PageResult<MarkingTaskVO>> page(MarkingTaskQueryDTO query) {
        return Result.success(markingTaskService.pageQuery(query));
    }

    @Operation(summary = "获取阅卷任务详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<MarkingTaskVO> getDetail(@PathVariable Long id) {
        return Result.success(markingTaskService.getDetail(id));
    }

    @Operation(summary = "生成阅卷任务")
    @PostMapping("/generate/{examSubjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> generate(@PathVariable Long examSubjectId) {
        markingTaskService.generateTasks(examSubjectId);
        return Result.success();
    }

    @Operation(summary = "删除阅卷任务")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        markingTaskService.delete(id);
        return Result.success();
    }

    @Operation(summary = "分配阅卷任务")
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assign(@RequestBody MarkingTaskAssignDTO dto) {
        markingTaskService.assignTask(dto);
        return Result.success();
    }

    @Operation(summary = "开始阅卷任务")
    @PostMapping("/start/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> start(@PathVariable Long id) {
        markingTaskService.startTask(id);
        return Result.success();
    }

    @Operation(summary = "完成阅卷任务")
    @PostMapping("/complete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> complete(@PathVariable Long id) {
        markingTaskService.completeTask(id);
        return Result.success();
    }

    @Operation(summary = "根据考试科目ID查询阅卷任务列表")
    @GetMapping("/list/{examSubjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<MarkingTaskVO>> listByExamSubjectId(@PathVariable Long examSubjectId) {
        return Result.success(markingTaskService.listByExamSubjectId(examSubjectId));
    }
}
