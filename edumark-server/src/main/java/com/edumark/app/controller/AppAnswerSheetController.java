package com.edumark.app.controller;

import com.edumark.common.result.Result;
import com.edumark.file.mapper.AnswerSheetImageMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.AnswerSheetImageVO;
import com.edumark.file.vo.AnswerSheetVO;
import com.edumark.security.service.LoginUserDetails;
import com.edumark.security.utils.SecurityUtils;
import com.edumark.school.mapper.ParentStudentBindMapper;
import com.edumark.system.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * App答题卡控制器
 *
 * @author EduMark
 */
@Tag(name = "App-答题卡查看")
@RestController
@RequestMapping("/app/answer-sheet")
public class AppAnswerSheetController {

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerSheetImageMapper answerSheetImageMapper;

    @Resource
    private ParentStudentBindMapper parentStudentBindMapper;

    @Resource
    private FileService fileService;

    @Operation(summary = "获取学生的答题卡列表")
    @GetMapping("/list/{examId}/{studentId}")
    public Result<List<AnswerSheetVO>> getAnswerSheetList(
            @PathVariable Long examId,
            @PathVariable Long studentId) {
        validateStudentAccess(studentId);
        List<AnswerSheetVO> list = answerSheetMapper
                .selectPageVO(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 200), buildQuery(examId, studentId))
                .getRecords();
        list.forEach(this::fillImages);
        return Result.success(list);
    }

    @Operation(summary = "获取答题卡详情")
    @GetMapping("/{answerSheetId}")
    public Result<AnswerSheetVO> getAnswerSheetDetail(@PathVariable Long answerSheetId) {
        AnswerSheetVO detail = answerSheetMapper.selectVOById(answerSheetId);
        if (detail == null) {
            throw new com.edumark.common.exception.BusinessException("答题卡不存在");
        }
        validateStudentAccess(detail.getStudentId());
        fillImages(detail);
        return Result.success(detail);
    }

    @Operation(summary = "获取答题卡图片列表")
    @GetMapping("/images/{answerSheetId}")
    public Result<List<AnswerSheetImageVO>> getAnswerSheetImages(@PathVariable Long answerSheetId) {
        AnswerSheetVO detail = answerSheetMapper.selectVOById(answerSheetId);
        if (detail == null) {
            throw new com.edumark.common.exception.BusinessException("答题卡不存在");
        }
        validateStudentAccess(detail.getStudentId());
        return Result.success(normalizeImages(answerSheetImageMapper.selectListByAnswerSheetId(answerSheetId)));
    }

    private com.edumark.file.dto.AnswerSheetQueryDTO buildQuery(Long examId, Long studentId) {
        com.edumark.file.dto.AnswerSheetQueryDTO query = new com.edumark.file.dto.AnswerSheetQueryDTO();
        query.setPageNum(1);
        query.setPageSize(200);
        query.setExamId(examId);
        query.setStudentId(studentId);
        return query;
    }

    private void fillImages(AnswerSheetVO sheet) {
        sheet.setImages(normalizeImages(answerSheetImageMapper.selectListByAnswerSheetId(sheet.getId())));
    }

    private List<AnswerSheetImageVO> normalizeImages(List<AnswerSheetImageVO> images) {
        return images.stream().peek(image -> {
            if ((image.getImageUrl() == null || image.getImageUrl().isBlank()) && image.getImagePath() != null && !image.getImagePath().isBlank()) {
                image.setImageUrl(fileService.getUrl(image.getImagePath()));
            }
        }).collect(Collectors.toList());
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
