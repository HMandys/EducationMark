package com.edumark.marking.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.common.utils.SecurityUtils;
import com.edumark.marking.dto.ArbitrationSubmitDTO;
import com.edumark.marking.dto.MarkingSubmitDTO;
import com.edumark.marking.service.MarkingService;
import com.edumark.marking.vo.MarkingArbitrationVO;
import com.edumark.marking.vo.MarkingRecordVO;
import com.edumark.marking.vo.MarkingTaskAssignVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阅卷工作台Controller
 *
 * @author EduMark
 */
@Tag(name = "阅卷工作台")
@RestController
@RequestMapping("/marking")
public class MarkingController {

    @Resource
    private MarkingService markingService;

    @Operation(summary = "获取我的阅卷任务分配")
    @GetMapping("/my-assigns")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<List<MarkingTaskAssignVO>> getMyAssigns() {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return Result.success(markingService.getMyAssigns(teacherId));
    }

    @Operation(summary = "分页查询阅卷记录")
    @GetMapping("/records")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<PageResult<MarkingRecordVO>> pageRecords(
            @RequestParam Long taskId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return Result.success(markingService.pageRecords(taskId, teacherId, status, pageNum, pageSize));
    }

    @Operation(summary = "获取待阅卷的下一条记录")
    @GetMapping("/next-pending")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<MarkingRecordVO> getNextPending(@RequestParam Long taskId) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return Result.success(markingService.getNextPending(taskId, teacherId));
    }

    @Operation(summary = "获取阅卷记录详情")
    @GetMapping("/record/{recordId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<MarkingRecordVO> getRecordDetail(@PathVariable Long recordId) {
        return Result.success(markingService.getRecordDetail(recordId));
    }

    @Operation(summary = "提交评分")
    @PostMapping("/submit")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<Void> submitScore(@RequestBody MarkingSubmitDTO dto) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        markingService.submitScore(dto, teacherId);
        return Result.success();
    }

    @Operation(summary = "分页查询仲裁记录")
    @GetMapping("/arbitrations")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<PageResult<MarkingArbitrationVO>> pageArbitrations(
            @RequestParam Long taskId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(markingService.pageArbitrations(taskId, status, pageNum, pageSize));
    }

    @Operation(summary = "获取待仲裁的下一条记录")
    @GetMapping("/next-arbitration")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<MarkingArbitrationVO> getNextArbitration(@RequestParam Long taskId) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        return Result.success(markingService.getNextArbitration(taskId, teacherId));
    }

    @Operation(summary = "获取仲裁记录详情")
    @GetMapping("/arbitration/{arbitrationId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<MarkingArbitrationVO> getArbitrationDetail(@PathVariable Long arbitrationId) {
        return Result.success(markingService.getArbitrationDetail(arbitrationId));
    }

    @Operation(summary = "提交仲裁")
    @PostMapping("/arbitration/submit")
    @PreAuthorize("hasRole('TEACHER')")
    public Result<Void> submitArbitration(@RequestBody ArbitrationSubmitDTO dto) {
        Long teacherId = SecurityUtils.getCurrentUserId();
        markingService.submitArbitration(dto, teacherId);
        return Result.success();
    }

    @Operation(summary = "客观题自动判分")
    @PostMapping("/auto-mark/{examSubjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> autoMarkObjective(@PathVariable Long examSubjectId) {
        markingService.autoMarkObjective(examSubjectId);
        return Result.success();
    }
}
