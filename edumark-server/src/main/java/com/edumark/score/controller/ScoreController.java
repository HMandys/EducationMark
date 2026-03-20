package com.edumark.score.controller;

import com.edumark.common.result.PageResult;
import com.edumark.common.result.Result;
import com.edumark.score.dto.ScoreQueryDTO;
import com.edumark.score.service.ScoreService;
import com.edumark.score.vo.ExamScoreVO;
import com.edumark.score.vo.ScorePublishCheckVO;
import com.edumark.score.vo.ScoreStatisticsVO;
import com.edumark.score.vo.SubjectScoreVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 成绩管理控制器
 *
 * @author EduMark
 */
@Tag(name = "成绩管理")
@RestController
@RequestMapping("/score")
public class ScoreController {

    @Resource
    private ScoreService scoreService;

    @Operation(summary = "分页查询考试成绩")
    @GetMapping("/exam/page")
    public Result<PageResult<ExamScoreVO>> pageExamScores(ScoreQueryDTO query) {
        return Result.success(scoreService.pageExamScores(query));
    }

    @Operation(summary = "分页查询科目成绩")
    @GetMapping("/subject/page")
    public Result<PageResult<SubjectScoreVO>> pageSubjectScores(ScoreQueryDTO query) {
        return Result.success(scoreService.pageSubjectScores(query));
    }

    @Operation(summary = "查询学生考试成绩详情")
    @GetMapping("/student/{examId}/{studentId}")
    public Result<ExamScoreVO> getStudentExamScore(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        return Result.success(scoreService.getStudentExamScore(examId, studentId));
    }

    @Operation(summary = "查询统计数据")
    @GetMapping("/statistics/{examId}")
    public Result<List<ScoreStatisticsVO>> getStatistics(
            @PathVariable Long examId,
            @Parameter(description = "科目ID") @RequestParam(required = false) Long examSubjectId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId) {
        return Result.success(scoreService.getStatistics(examId, examSubjectId, classId));
    }

    @Operation(summary = "查询成绩发布前检查")
    @GetMapping("/publish-check/{examId}")
    public Result<ScorePublishCheckVO> getPublishCheck(@PathVariable Long examId) {
        return Result.success(scoreService.getPublishCheck(examId));
    }

    @Operation(summary = "汇总成绩")
    @PostMapping("/aggregate/{examId}")
    public Result<Void> aggregateScores(@PathVariable Long examId) {
        scoreService.aggregateScores(examId);
        return Result.success();
    }

    @Operation(summary = "计算排名")
    @PostMapping("/ranking/{examId}")
    public Result<Void> calculateRanking(@PathVariable Long examId) {
        scoreService.calculateRanking(examId);
        return Result.success();
    }

    @Operation(summary = "计算统计")
    @PostMapping("/statistics/{examId}")
    public Result<Void> calculateStatistics(@PathVariable Long examId) {
        scoreService.calculateStatistics(examId);
        return Result.success();
    }

    @Operation(summary = "发布成绩")
    @PostMapping("/publish/{examId}")
    public Result<Void> publish(
            @PathVariable Long examId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        scoreService.publish(examId, userId);
        return Result.success();
    }

    @Operation(summary = "撤回成绩")
    @PostMapping("/unpublish/{examId}")
    public Result<Void> unpublish(
            @PathVariable Long examId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        scoreService.unpublish(examId, userId);
        return Result.success();
    }

    @Operation(summary = "导出成绩Excel")
    @GetMapping("/export/{examId}")
    public void exportExcel(
            @PathVariable Long examId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            HttpServletResponse response) throws IOException {
        byte[] data = scoreService.exportExcel(examId, classId);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode("成绩表.xlsx", StandardCharsets.UTF_8));
        response.getOutputStream().write(data);
    }
}
