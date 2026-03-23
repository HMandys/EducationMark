package com.edumark.marking.controller;

import com.edumark.common.result.Result;
import com.edumark.marking.service.MarkingAccessService;
import com.edumark.marking.service.MarkingTaskService;
import com.edumark.marking.vo.MarkingItemVO;
import com.edumark.marking.vo.MarkingSessionVO;
import com.edumark.marking.vo.MarkingTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 阅卷码访问Controller（免登录阅卷）
 *
 * @author EduMark
 */
@Tag(name = "阅卷码访问")
@RestController
@RequestMapping("/marking/access")
public class MarkingAccessController {

    @Resource
    private MarkingAccessService markingAccessService;

    @Resource
    private MarkingTaskService markingTaskService;

    @Operation(summary = "阅卷码登录")
    @PostMapping("/login")
    public Result<MarkingSessionVO> login(@RequestBody AccessCodeRequest request) {
        MarkingSessionVO session = markingAccessService.validateAndLogin(request.getAccessCode());
        return Result.success(session);
    }

    @Operation(summary = "获取任务信息")
    @GetMapping("/task")
    public Result<MarkingSessionVO> getTaskInfo(@RequestHeader("X-Session-Token") String sessionToken) {
        return Result.success(markingAccessService.getTaskInfo(sessionToken));
    }

    @Operation(summary = "获取下一份待阅记录")
    @GetMapping("/next")
    public Result<MarkingItemVO> getNextItem(@RequestHeader("X-Session-Token") String sessionToken) {
        MarkingItemVO item = markingAccessService.getNextItem(sessionToken);
        return Result.success(item);
    }

    @Operation(summary = "提交评分")
    @PostMapping("/submit")
    public Result<Boolean> submitScore(
            @RequestHeader("X-Session-Token") String sessionToken,
            @RequestBody ScoreSubmitRequest request) {
        boolean success = markingAccessService.submitScore(
                sessionToken,
                request.getRecordId(),
                request.getScore(),
                request.getComment()
        );
        return Result.success(success);
    }

    @Operation(summary = "跳过当前记录")
    @PostMapping("/skip")
    public Result<Boolean> skipRecord(
            @RequestHeader("X-Session-Token") String sessionToken,
            @RequestBody SkipRequest request) {
        boolean success = markingAccessService.skipRecord(sessionToken, request.getRecordId());
        return Result.success(success);
    }

    @Operation(summary = "生成阅卷码")
    @PostMapping("/generate/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<MarkingTaskVO> generateAccessCode(@PathVariable Long taskId) {
        return Result.success(markingAccessService.generateAccessCode(taskId));
    }

    @Operation(summary = "刷新阅卷码有效期")
    @PostMapping("/refresh/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refreshAccessCode(@PathVariable Long taskId, @RequestParam(defaultValue = "24") int hours) {
        markingAccessService.refreshAccessCodeExpireTime(taskId, hours);
        return Result.success();
    }

    @Operation(summary = "获取任务详情（含阅卷码）")
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<MarkingTaskVO> getTaskWithAccessCode(@PathVariable Long taskId) {
        return Result.success(markingTaskService.getDetail(taskId));
    }

    /**
     * 阅卷码请求
     */
    public static class AccessCodeRequest {
        private String accessCode;

        public String getAccessCode() { return accessCode; }
        public void setAccessCode(String accessCode) { this.accessCode = accessCode; }
    }

    /**
     * 评分提交请求
     */
    public static class ScoreSubmitRequest {
        private Long recordId;
        private Integer score;
        private String comment;

        public Long getRecordId() { return recordId; }
        public void setRecordId(Long recordId) { this.recordId = recordId; }

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    /**
     * 跳过请求
     */
    public static class SkipRequest {
        private Long recordId;

        public Long getRecordId() { return recordId; }
        public void setRecordId(Long recordId) { this.recordId = recordId; }
    }
}
