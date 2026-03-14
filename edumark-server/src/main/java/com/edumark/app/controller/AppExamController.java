package com.edumark.app.controller;

import com.edumark.common.result.Result;
import com.edumark.score.service.ScoreService;
import com.edumark.score.vo.ExamScoreVO;
import com.edumark.score.vo.ScoreStatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * App考试与成绩控制器
 *
 * @author EduMark
 */
@Tag(name = "App-考试与成绩")
@RestController
@RequestMapping("/api/app")
public class AppExamController {

    @Resource
    private ScoreService scoreService;

    @Operation(summary = "获取学生的考试列表")
    @GetMapping("/exam/list/{studentId}")
    public Result<List<Map<String, Object>>> getExamList(@PathVariable Long studentId) {
        // TODO: 查询学生所在班级参加的已发布成绩的考试列表
        List<Map<String, Object>> list = new ArrayList<>();
        return Result.success(list);
    }

    @Operation(summary = "获取考试详情")
    @GetMapping("/exam/{examId}")
    public Result<Map<String, Object>> getExamDetail(@PathVariable Long examId) {
        // TODO: 查询考试详情
        return Result.success(null);
    }

    @Operation(summary = "获取学生的考试成绩")
    @GetMapping("/score/{examId}/{studentId}")
    public Result<ExamScoreVO> getStudentExamScore(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        return Result.success(scoreService.getStudentExamScore(examId, studentId));
    }

    @Operation(summary = "获取学生的最新成绩列表")
    @GetMapping("/score/recent/{studentId}")
    public Result<List<ExamScoreVO>> getRecentScores(
            @PathVariable Long studentId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        // TODO: 查询学生最近的成绩列表
        List<ExamScoreVO> list = new ArrayList<>();
        return Result.success(list);
    }

    @Operation(summary = "获取考试统计信息")
    @GetMapping("/score/statistics/{examId}")
    public Result<List<ScoreStatisticsVO>> getExamStatistics(
            @PathVariable Long examId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId) {
        return Result.success(scoreService.getStatistics(examId, null, classId));
    }
}
