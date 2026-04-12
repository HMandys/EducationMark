package com.edumark.app.controller;

import com.edumark.app.service.AppAiAnalysisService;
import com.edumark.app.vo.AppAiReportVO;
import com.edumark.common.result.Result;
import com.edumark.security.service.LoginUserDetails;
import com.edumark.security.utils.SecurityUtils;
import com.edumark.school.mapper.ParentStudentBindMapper;
import com.edumark.system.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * App AI 分析控制器
 *
 * @author EduMark
 */
@Tag(name = "App-AI分析")
@RestController
@RequestMapping("/app/ai")
public class AppAiController {

    @Resource
    private AppAiAnalysisService appAiAnalysisService;

    @Resource
    private ParentStudentBindMapper parentStudentBindMapper;

    @Operation(summary = "获取学生 AI 学情分析")
    @GetMapping("/report/{studentId}")
    public Result<AppAiReportVO> getStudentReport(
            @PathVariable Long studentId,
            @Parameter(description = "最近考试数量") @RequestParam(defaultValue = "10") Integer limit) {
        validateStudentAccess(studentId);
        return Result.success(appAiAnalysisService.generateStudentReport(studentId, limit));
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
