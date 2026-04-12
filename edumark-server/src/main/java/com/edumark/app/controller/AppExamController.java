package com.edumark.app.controller;

import com.edumark.common.result.Result;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.vo.ExamVO;
import com.edumark.score.mapper.ExamScoreMapper;
import com.edumark.score.mapper.SubjectScoreMapper;
import com.edumark.score.service.ScoreService;
import com.edumark.score.vo.ExamScoreVO;
import com.edumark.score.vo.ScoreStatisticsVO;
import com.edumark.score.vo.SubjectScoreVO;
import com.edumark.security.service.LoginUserDetails;
import com.edumark.security.utils.SecurityUtils;
import com.edumark.school.mapper.ParentStudentBindMapper;
import com.edumark.system.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * App考试与成绩控制器
 *
 * @author EduMark
 */
@Tag(name = "App-考试与成绩")
@RestController
@RequestMapping("/app")
public class AppExamController {

    @Resource
    private ScoreService scoreService;

    @Resource
    private ExamMapper examMapper;

    @Resource
    private ExamScoreMapper examScoreMapper;

    @Resource
    private SubjectScoreMapper subjectScoreMapper;

    @Resource
    private ParentStudentBindMapper parentStudentBindMapper;

    @Operation(summary = "获取学生的考试列表")
    @GetMapping("/exam/list/{studentId}")
    public Result<List<ExamVO>> getExamList(@PathVariable Long studentId) {
        validateStudentAccess(studentId);
        return Result.success(examMapper.selectPublishedListByStudentId(studentId));
    }

    @Operation(summary = "获取考试详情")
    @GetMapping("/exam/{examId}")
    public Result<ExamVO> getExamDetail(@PathVariable Long examId) {
        ExamVO exam = examMapper.selectVOById(examId);
        if (exam == null) {
            throw new com.edumark.common.exception.BusinessException("考试不存在");
        }
        return Result.success(exam);
    }

    @Operation(summary = "获取考试详情（指定学生）")
    @GetMapping("/exam/{examId}/{studentId}")
    public Result<ExamVO> getExamDetailByStudent(@PathVariable Long examId, @PathVariable Long studentId) {
        validateStudentAccess(studentId);
        ExamScoreVO score = examScoreMapper.selectVOByExamAndStudent(examId, studentId);
        if (score == null) {
            throw new com.edumark.common.exception.BusinessException("未找到该学生的考试成绩");
        }
        return Result.success(examMapper.selectVOById(examId));
    }

    @Operation(summary = "获取学生的考试成绩")
    @GetMapping("/score/{examId}/{studentId}")
    public Result<ExamScoreVO> getStudentExamScore(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        validateStudentAccess(studentId);
        return Result.success(scoreService.getStudentExamScore(examId, studentId));
    }

    @Operation(summary = "获取学生的最新成绩列表")
    @GetMapping("/score/recent/{studentId}")
    public Result<List<ExamScoreVO>> getRecentScores(
            @PathVariable Long studentId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        validateStudentAccess(studentId);
        int safeLimit = limit == null || limit <= 0 ? 5 : Math.min(limit, 20);
        List<ExamScoreVO> list = examScoreMapper.selectRecentByStudentId(studentId, safeLimit);
        for (ExamScoreVO score : list) {
            List<SubjectScoreVO> subjects = subjectScoreMapper.selectListByExamAndStudent(score.getExamId(), studentId);
            score.setSubjectScores(subjects);
        }
        return Result.success(list);
    }

    @Operation(summary = "获取考试统计信息")
    @GetMapping("/score/statistics/{examId}")
    public Result<List<ScoreStatisticsVO>> getExamStatistics(
            @PathVariable Long examId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId) {
        return Result.success(scoreService.getStatistics(examId, null, classId));
    }

    private void validateStudentAccess(Long studentId) {
        LoginUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null || currentUser.getUser() == null) {
            throw new com.edumark.common.exception.BusinessException("未登录");
        }

        SysUser user = currentUser.getUser();
        if (user.getStudentId() != null) {
            if (!user.getStudentId().equals(studentId)) {
                throw new com.edumark.common.exception.BusinessException("无权查看该学生数据");
            }
            return;
        }

        if (user.getParentId() != null) {
            if (parentStudentBindMapper.selectByParentAndStudent(user.getParentId(), studentId) == null) {
                throw new com.edumark.common.exception.BusinessException("无权查看该学生数据");
            }
            return;
        }

        throw new com.edumark.common.exception.BusinessException("当前账号未绑定学生信息");
    }
}
