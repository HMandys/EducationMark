package com.edumark.marking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edumark.common.exception.BusinessException;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.marking.entity.MarkingArbitration;
import com.edumark.marking.entity.MarkingRecord;
import com.edumark.marking.entity.MarkingSession;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.entity.MarkingTaskAssign;
import com.edumark.marking.mapper.MarkingArbitrationMapper;
import com.edumark.marking.mapper.MarkingRecordMapper;
import com.edumark.marking.mapper.MarkingSessionMapper;
import com.edumark.marking.mapper.MarkingTaskAssignMapper;
import com.edumark.marking.mapper.MarkingTaskMapper;
import com.edumark.marking.service.MarkingAccessService;
import com.edumark.marking.service.MarkingTaskService;
import com.edumark.marking.vo.MarkingItemVO;
import com.edumark.marking.vo.MarkingRecordVO;
import com.edumark.marking.vo.MarkingSessionVO;
import com.edumark.marking.vo.MarkingTaskVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 阅卷访问服务实现
 *
 * @author EduMark
 */
@Service
public class MarkingAccessServiceImpl implements MarkingAccessService {

    private static final int ACCESS_CODE_LENGTH = 8;
    private static final int SESSION_EXPIRE_HOURS = 24;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Resource
    private MarkingTaskMapper markingTaskMapper;

    @Resource
    private MarkingSessionMapper markingSessionMapper;

    @Resource
    private MarkingRecordMapper markingRecordMapper;

    @Resource
    private MarkingArbitrationMapper markingArbitrationMapper;

    @Resource
    private MarkingTaskAssignMapper markingTaskAssignMapper;

    @Resource
    private ExamMapper examMapper;

    @Resource
    private ExamSubjectMapper examSubjectMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private AnswerSheetDetailService answerSheetDetailService;

    @Resource
    private MarkingTaskService markingTaskService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarkingTaskVO generateAccessCode(Long taskId) {
        MarkingTask task = markingTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        if (task.getStatus() == null || task.getStatus() != 1) {
            throw new BusinessException("只有进行中的阅卷任务才能生成阅卷码");
        }

        LocalDateTime expireTime = LocalDateTime.now().plusHours(SESSION_EXPIRE_HOURS);
        String firstAccessCode = generateUniqueCode(null);

        task.setAccessCode(firstAccessCode);
        task.setSecondAccessCode(task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1
                ? generateUniqueCode(firstAccessCode)
                : null);
        task.setAccessCodeExpireTime(expireTime);
        markingTaskMapper.updateById(task);

        return markingTaskService.getDetail(taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarkingSessionVO validateAndLogin(String accessCode) {
        if (accessCode == null || accessCode.length() != ACCESS_CODE_LENGTH) {
            throw new BusinessException("阅卷码格式不正确");
        }

        MarkingTask task = markingTaskMapper.selectOne(
                new LambdaQueryWrapper<MarkingTask>()
                        .and(wrapper -> wrapper
                                .eq(MarkingTask::getAccessCode, accessCode)
                                .or()
                                .eq(MarkingTask::getSecondAccessCode, accessCode))
                        .last("LIMIT 1")
        );

        if (task == null) {
            throw new BusinessException("阅卷码无效");
        }

        if (task.getAccessCodeExpireTime() != null && task.getAccessCodeExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("阅卷码已过期，请重新生成");
        }

        if (task.getStatus() == null || task.getStatus() != 1) {
            throw new BusinessException("该阅卷任务未开始或已完成");
        }

        Integer markingRole = resolveMarkingRole(task, accessCode);
        String sessionToken = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expireTime = LocalDateTime.now().plusHours(SESSION_EXPIRE_HOURS);

        MarkingSession session = new MarkingSession();
        session.setTaskId(task.getId());
        session.setAccessCode(accessCode);
        session.setMarkingRole(markingRole);
        session.setSessionToken(sessionToken);
        session.setExpireTime(expireTime);
        markingSessionMapper.insert(session);

        return buildSessionVO(task, sessionToken, markingRole);
    }

    @Override
    public boolean validateSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }
        MarkingSession session = markingSessionMapper.selectBySessionToken(sessionToken);
        return session != null && session.getExpireTime() != null && session.getExpireTime().isAfter(LocalDateTime.now());
    }

    @Override
    public Long getTaskIdByToken(String sessionToken) {
        return getValidSession(sessionToken).getTaskId();
    }

    @Override
    public MarkingSessionVO getTaskInfo(String sessionToken) {
        MarkingSession session = getValidSession(sessionToken);
        MarkingTask task = markingTaskMapper.selectById(session.getTaskId());
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        return buildSessionVO(task, sessionToken, session.getMarkingRole());
    }

    @Override
    public MarkingItemVO getNextItem(String sessionToken) {
        MarkingSession session = getValidSession(sessionToken);
        Long taskId = session.getTaskId();
        Integer markingRole = session.getMarkingRole();

        MarkingRecordVO recordVO = markingRecordMapper.selectNextPendingByTaskIdAndRole(taskId, markingRole);
        if (recordVO == null) {
            return null;
        }

        MarkingItemVO item = new MarkingItemVO();
        item.setRecordId(recordVO.getId());
        item.setAnswerSheetId(recordVO.getAnswerSheetId());
        item.setQuestionId(recordVO.getQuestionId());
        item.setFullScore(recordVO.getFullScore());
        item.setQuestionNo(recordVO.getQuestionNo());

        String questionImageUrl = recordVO.getAnswerImageUrl();
        try {
            String previewUrl = answerSheetDetailService.getQuestionPreviewUrl(recordVO.getAnswerSheetId(), recordVO.getQuestionId());
            if (previewUrl != null && !previewUrl.isBlank()) {
                questionImageUrl = previewUrl;
            }
        } catch (BusinessException ignored) {
            // 题图预览不存在时退回整卷图
        }
        item.setQuestionImage(questionImageUrl);

        Long totalCount = countRoleRecords(taskId, markingRole, null);
        Long pendingIndex = markingRecordMapper.selectPendingIndexByRole(taskId, markingRole, recordVO.getId());

        item.setCurrentIndex(pendingIndex != null ? pendingIndex.intValue() : 0);
        item.setTotalCount(totalCount != null ? totalCount.intValue() : 0);

        return item;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitScore(String sessionToken, Long recordId, Integer score, String comment, String annotations) {
        MarkingSession session = getValidSession(sessionToken);
        Long taskId = session.getTaskId();
        Integer markingRole = session.getMarkingRole();

        MarkingRecord record = markingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("阅卷记录不存在");
        }
        if (!record.getTaskId().equals(taskId)) {
            throw new BusinessException("无权操作此记录");
        }
        if (record.getMarkingRole() == null || !record.getMarkingRole().equals(markingRole)) {
            throw new BusinessException("当前阅卷码无权提交此评阅角色的记录");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException("该记录已完成评分");
        }
        if (score == null || score < 0) {
            throw new BusinessException("分数不能为空或小于0");
        }
        if (record.getFullScore() != null && score > record.getFullScore()) {
            throw new BusinessException("分数不能超过满分" + record.getFullScore());
        }

        record.setScore(score);
        record.setComment(comment);
        record.setAnnotations(annotations);
        record.setMarkingTime(LocalDateTime.now());
        record.setStatus(1);
        markingRecordMapper.updateById(record);
        syncAssignProgress(record);

        MarkingTask task = markingTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }

        if (task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1) {
            handleDoubleMarking(record, task);
        } else {
            answerSheetDetailService.updateQuestionScore(record.getAnswerSheetId(), record.getQuestionId(), score, true);
        }

        markingTaskService.updateProgress(taskId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAccessCodeExpireTime(Long taskId, int hours) {
        MarkingTask task = markingTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        task.setAccessCodeExpireTime(LocalDateTime.now().plusHours(hours));
        markingTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean skipRecord(String sessionToken, Long recordId) {
        MarkingSession session = getValidSession(sessionToken);
        Long taskId = session.getTaskId();
        Integer markingRole = session.getMarkingRole();

        MarkingRecord record = markingRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("阅卷记录不存在");
        }
        if (!record.getTaskId().equals(taskId)) {
            throw new BusinessException("无权操作此记录");
        }
        if (record.getMarkingRole() == null || !record.getMarkingRole().equals(markingRole)) {
            throw new BusinessException("当前阅卷码无权跳过此记录");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException("该记录已完成评分，无法跳过");
        }

        // 增加跳过次数，使其排到后面
        int skipCount = record.getSkipCount() != null ? record.getSkipCount() : 0;
        record.setSkipCount(skipCount + 1);
        markingRecordMapper.updateById(record);

        return true;
    }

    private void handleDoubleMarking(MarkingRecord currentRecord, MarkingTask task) {
        List<MarkingRecord> records = markingRecordMapper.selectByAnswerSheetAndQuestion(
                currentRecord.getAnswerSheetId(), currentRecord.getQuestionId());
        if (records.size() < 2) {
            return;
        }

        MarkingRecord first = records.stream()
                .filter(record -> record.getMarkingRole() != null && record.getMarkingRole() == 1)
                .findFirst()
                .orElse(null);
        MarkingRecord second = records.stream()
                .filter(record -> record.getMarkingRole() != null && record.getMarkingRole() == 2)
                .findFirst()
                .orElse(null);

        if (first == null || second == null || first.getStatus() != 1 || second.getStatus() != 1) {
            return;
        }

        int diff = Math.abs(first.getScore() - second.getScore());
        int threshold = task.getDoubleMarkingThreshold() != null ? task.getDoubleMarkingThreshold() : 0;

        if (diff <= threshold) {
            // 四舍五入计算平均分，避免整数除法丢失精度
            int avgScore = (int) Math.round((first.getScore() + second.getScore()) / 2.0);
            answerSheetDetailService.updateQuestionScore(currentRecord.getAnswerSheetId(), currentRecord.getQuestionId(), avgScore, true);
            return;
        }

        Long arbitrationCount = markingArbitrationMapper.selectCount(
                new LambdaQueryWrapper<MarkingArbitration>()
                        .eq(MarkingArbitration::getTaskId, task.getId())
                        .eq(MarkingArbitration::getAnswerSheetId, currentRecord.getAnswerSheetId())
                        .eq(MarkingArbitration::getQuestionId, currentRecord.getQuestionId())
        );
        if (arbitrationCount != null && arbitrationCount > 0) {
            return;
        }

        MarkingArbitration arbitration = new MarkingArbitration();
        arbitration.setTaskId(task.getId());
        arbitration.setAnswerSheetId(currentRecord.getAnswerSheetId());
        arbitration.setQuestionId(currentRecord.getQuestionId());
        arbitration.setStudentId(currentRecord.getStudentId());
        arbitration.setFirstMarkingId(first.getId());
        arbitration.setFirstScore(first.getScore());
        arbitration.setFirstTeacherId(first.getTeacherId());
        arbitration.setSecondMarkingId(second.getId());
        arbitration.setSecondScore(second.getScore());
        arbitration.setSecondTeacherId(second.getTeacherId());
        arbitration.setScoreDiff(diff);
        arbitration.setStatus(0);
        markingArbitrationMapper.insert(arbitration);
    }

    private void syncAssignProgress(MarkingRecord record) {
        MarkingTaskAssign assign = markingTaskAssignMapper.selectOne(
                new LambdaQueryWrapper<MarkingTaskAssign>()
                        .eq(MarkingTaskAssign::getTaskId, record.getTaskId())
                        .eq(MarkingTaskAssign::getTeacherId, record.getTeacherId())
                        .eq(MarkingTaskAssign::getMarkingRole, record.getMarkingRole())
                        .last("LIMIT 1")
        );
        if (assign == null) {
            return;
        }
        int completedCount = assign.getCompletedCount() != null ? assign.getCompletedCount() : 0;
        assign.setCompletedCount(completedCount + 1);
        markingTaskAssignMapper.updateById(assign);
    }

    private MarkingSessionVO buildSessionVO(MarkingTask task, String sessionToken, Integer markingRole) {
        MarkingSessionVO vo = new MarkingSessionVO();
        vo.setSessionToken(sessionToken);
        vo.setTaskId(task.getId());
        vo.setMarkingRole(markingRole);
        vo.setMarkingRoleName(getMarkingRoleName(markingRole));

        Long totalCount = countRoleRecords(task.getId(), markingRole, null);
        Long completedCount = countRoleRecords(task.getId(), markingRole, 1);
        int total = totalCount != null ? totalCount.intValue() : 0;
        int completed = completedCount != null ? completedCount.intValue() : 0;
        vo.setTotalCount(total);
        vo.setCompletedCount(completed);
        vo.setPendingCount(Math.max(total - completed, 0));

        ExamSubject examSubject = examSubjectMapper.selectById(task.getExamSubjectId());
        if (examSubject != null) {
            vo.setSubjectName(examSubject.getSubjectName());
            Exam exam = examMapper.selectById(examSubject.getExamId());
            if (exam != null) {
                vo.setExamName(exam.getName());
            }
        }

        Integer templateQuestionNo = resolveTemplateQuestionNo(task.getQuestionId());
        if (templateQuestionNo != null) {
            vo.setQuestionNo(String.valueOf(templateQuestionNo));
            MarkingRecord record = markingRecordMapper.selectOne(
                    new LambdaQueryWrapper<MarkingRecord>()
                            .eq(MarkingRecord::getTaskId, task.getId())
                            .last("LIMIT 1")
            );
            if (record != null) {
                vo.setFullScore(record.getFullScore());
            }
        } else {
            PaperQuestion question = paperQuestionMapper.selectById(task.getQuestionId());
            if (question != null) {
                vo.setQuestionNo(question.getQuestionNo());
                vo.setFullScore(question.getScore());
            }
        }

        return vo;
    }

    private Integer resolveTemplateQuestionNo(Long questionId) {
        if (questionId == null || questionId >= 0) {
            return null;
        }
        return Math.toIntExact(-questionId);
    }

    private Long countRoleRecords(Long taskId, Integer markingRole, Integer status) {
        LambdaQueryWrapper<MarkingRecord> wrapper = new LambdaQueryWrapper<MarkingRecord>()
                .eq(MarkingRecord::getTaskId, taskId)
                .eq(MarkingRecord::getMarkingRole, markingRole);
        if (status != null) {
            wrapper.eq(MarkingRecord::getStatus, status);
        }
        return markingRecordMapper.selectCount(wrapper);
    }

    private MarkingSession getValidSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            throw new BusinessException("会话无效或已过期");
        }
        MarkingSession session = markingSessionMapper.selectBySessionToken(sessionToken);
        if (session == null || session.getExpireTime() == null || session.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("会话无效或已过期");
        }
        MarkingTask task = markingTaskMapper.selectById(session.getTaskId());
        if (task == null || task.getStatus() == null || task.getStatus() != 1) {
            throw new BusinessException("会话无效或已过期");
        }
        if (task.getAccessCodeExpireTime() != null && task.getAccessCodeExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("会话无效或已过期");
        }
        String currentAccessCode = session.getMarkingRole() != null && session.getMarkingRole() == 2
                ? task.getSecondAccessCode()
                : task.getAccessCode();
        if (currentAccessCode == null || !currentAccessCode.equals(session.getAccessCode())) {
            throw new BusinessException("会话无效或已过期");
        }
        return session;
    }

    private Integer resolveMarkingRole(MarkingTask task, String accessCode) {
        if (accessCode.equals(task.getAccessCode())) {
            return 1;
        }
        if (task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1 && accessCode.equals(task.getSecondAccessCode())) {
            return 2;
        }
        throw new BusinessException("阅卷码无效");
    }

    private String generateUniqueCode(String excludedCode) {
        for (int i = 0; i < 50; i++) {
            String code = generateRandomCode();
            if (!code.equals(excludedCode) && !accessCodeExists(code)) {
                return code;
            }
        }
        throw new BusinessException("阅卷码生成失败，请重试");
    }

    private boolean accessCodeExists(String accessCode) {
        Long count = markingTaskMapper.selectCount(
                new LambdaQueryWrapper<MarkingTask>()
                        .and(wrapper -> wrapper
                                .eq(MarkingTask::getAccessCode, accessCode)
                                .or()
                                .eq(MarkingTask::getSecondAccessCode, accessCode))
        );
        return count != null && count > 0;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(ACCESS_CODE_LENGTH);
        for (int i = 0; i < ACCESS_CODE_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private String getMarkingRoleName(Integer markingRole) {
        if (markingRole == null) {
            return "未知";
        }
        if (markingRole == 1) {
            return "一评";
        }
        if (markingRole == 2) {
            return "二评";
        }
        if (markingRole == 3) {
            return "仲裁";
        }
        return "未知";
    }
}
