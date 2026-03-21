package com.edumark.marking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetDetail;
import com.edumark.file.mapper.AnswerSheetDetailMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.marking.dto.ArbitrationSubmitDTO;
import com.edumark.marking.dto.MarkingSubmitDTO;
import com.edumark.marking.entity.MarkingArbitration;
import com.edumark.marking.entity.MarkingRecord;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.entity.MarkingTaskAssign;
import com.edumark.marking.mapper.MarkingArbitrationMapper;
import com.edumark.marking.mapper.MarkingRecordMapper;
import com.edumark.marking.mapper.MarkingTaskAssignMapper;
import com.edumark.marking.mapper.MarkingTaskMapper;
import com.edumark.marking.service.MarkingService;
import com.edumark.marking.service.MarkingTaskService;
import com.edumark.marking.vo.MarkingArbitrationVO;
import com.edumark.marking.vo.MarkingRecordVO;
import com.edumark.marking.vo.MarkingTaskAssignVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阅卷服务实现
 *
 * @author EduMark
 */
@Service
public class MarkingServiceImpl implements MarkingService {

    @Resource
    private MarkingTaskMapper markingTaskMapper;

    @Resource
    private MarkingTaskAssignMapper markingTaskAssignMapper;

    @Resource
    private MarkingRecordMapper markingRecordMapper;

    @Resource
    private MarkingArbitrationMapper markingArbitrationMapper;

    @Resource
    private MarkingTaskService markingTaskService;

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerSheetDetailMapper answerSheetDetailMapper;

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private AnswerSheetDetailService answerSheetDetailService;

    @Override
    public List<MarkingTaskAssignVO> getMyAssigns(Long teacherId) {
        return markingTaskAssignMapper.selectListByTeacherId(teacherId);
    }

    @Override
    public PageResult<MarkingRecordVO> pageRecords(Long taskId, Long teacherId, Integer status, int pageNum, int pageSize) {
        Page<MarkingRecordVO> page = new Page<>(pageNum, pageSize);
        markingRecordMapper.selectPageVO(page, taskId, teacherId, status);
        return PageResult.of(page);
    }

    @Override
    public MarkingRecordVO getNextPending(Long taskId, Long teacherId) {
        MarkingRecordVO vo = markingRecordMapper.selectNextPending(taskId, teacherId);
        fillQuestionPreview(vo);
        return vo;
    }

    @Override
    public MarkingRecordVO getRecordDetail(Long recordId) {
        MarkingRecordVO vo = markingRecordMapper.selectVOById(recordId);
        fillQuestionPreview(vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitScore(MarkingSubmitDTO dto, Long teacherId) {
        MarkingRecord record = markingRecordMapper.selectById(dto.getRecordId());
        if (record == null) {
            throw new BusinessException("阅卷记录不存在");
        }
        if (!record.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权操作此记录");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException("该记录已完成评分");
        }

        // 更新阅卷记录
        record.setScore(dto.getScore());
        record.setComment(dto.getComment());
        record.setMarkingTime(LocalDateTime.now());
        record.setStatus(1);
        markingRecordMapper.updateById(record);

        // 更新教师阅卷数量
        MarkingTaskAssign assign = markingTaskAssignMapper.selectOne(
                new LambdaQueryWrapper<MarkingTaskAssign>()
                        .eq(MarkingTaskAssign::getTaskId, record.getTaskId())
                        .eq(MarkingTaskAssign::getTeacherId, teacherId)
        );
        if (assign != null) {
            assign.setCompletedCount(assign.getCompletedCount() + 1);
            markingTaskAssignMapper.updateById(assign);
        }

        // 获取任务信息
        MarkingTask task = markingTaskMapper.selectById(record.getTaskId());

        // 处理双评逻辑
        if (task.getEnableDoubleMarking() == 1) {
            handleDoubleMarking(record, task);
        } else {
            // 单评模式，直接更新答题卡得分
            updateAnswerSheetScore(record.getAnswerSheetId(), record.getQuestionId(), dto.getScore());
        }

        // 更新任务进度
        markingTaskService.updateProgress(record.getTaskId());
    }

    /**
     * 处理双评逻辑
     */
    private void handleDoubleMarking(MarkingRecord currentRecord, MarkingTask task) {
        // 查询同一答卷同一题目的所有阅卷记录
        List<MarkingRecord> records = markingRecordMapper.selectByAnswerSheetAndQuestion(
                currentRecord.getAnswerSheetId(), currentRecord.getQuestionId());

        // 检查是否两评都完成
        boolean allCompleted = records.stream().allMatch(r -> r.getStatus() == 1);
        if (!allCompleted || records.size() < 2) {
            return;
        }

        MarkingRecord first = records.get(0);
        MarkingRecord second = records.get(1);

        // 计算分差
        int diff = Math.abs(first.getScore() - second.getScore());
        Integer threshold = task.getDoubleMarkingThreshold() != null ? task.getDoubleMarkingThreshold() : 0;

        if (diff <= threshold) {
            // 分差在阈值内，取平均分
            int avgScore = (first.getScore() + second.getScore()) / 2;
            updateAnswerSheetScore(currentRecord.getAnswerSheetId(), currentRecord.getQuestionId(), avgScore);
        } else {
            // 分差超过阈值，创建仲裁记录
            createArbitration(currentRecord, first, second, task, diff);
        }
    }

    /**
     * 创建仲裁记录
     */
    private void createArbitration(MarkingRecord currentRecord, MarkingRecord first, MarkingRecord second, MarkingTask task, int diff) {
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
        arbitration.setStatus(0); // 待仲裁
        markingArbitrationMapper.insert(arbitration);
    }

    /**
     * 更新答题卡得分
     */
    private void updateAnswerSheetScore(Long answerSheetId, Long questionId, Integer score) {
        answerSheetDetailService.updateQuestionScore(answerSheetId, questionId, score, true);
    }

    @Override
    public PageResult<MarkingArbitrationVO> pageArbitrations(Long taskId, Integer status, int pageNum, int pageSize) {
        Page<MarkingArbitrationVO> page = new Page<>(pageNum, pageSize);
        markingArbitrationMapper.selectPageVO(page, taskId, status);
        return PageResult.of(page);
    }

    @Override
    public MarkingArbitrationVO getNextArbitration(Long taskId, Long teacherId) {
        return markingArbitrationMapper.selectNextPending(taskId, teacherId);
    }

    @Override
    public MarkingArbitrationVO getArbitrationDetail(Long arbitrationId) {
        return markingArbitrationMapper.selectVOById(arbitrationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitArbitration(ArbitrationSubmitDTO dto, Long teacherId) {
        MarkingArbitration arbitration = markingArbitrationMapper.selectById(dto.getArbitrationId());
        if (arbitration == null) {
            throw new BusinessException("仲裁记录不存在");
        }
        if (arbitration.getStatus() != 0) {
            throw new BusinessException("该记录已完成仲裁");
        }

        // 更新仲裁记录
        arbitration.setArbitrationTeacherId(teacherId);
        arbitration.setArbitrationScore(dto.getScore());
        arbitration.setArbitrationComment(dto.getComment());
        arbitration.setArbitrationTime(LocalDateTime.now());
        arbitration.setStatus(1);
        markingArbitrationMapper.updateById(arbitration);

        // 更新答题卡得分
        updateAnswerSheetScore(arbitration.getAnswerSheetId(), arbitration.getQuestionId(), dto.getScore());

        // 更新任务进度
        markingTaskService.updateProgress(arbitration.getTaskId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoMarkObjective(Long examSubjectId) {
        // 查询该科目的试卷
        Paper paper = paperMapper.selectOne(
                new LambdaQueryWrapper<Paper>()
                        .eq(Paper::getExamSubjectId, examSubjectId)
                        .eq(Paper::getStatus, 1)
                        .last("LIMIT 1")
        );
        if (paper == null) {
            return;
        }

        // 查询试卷的客观题
        List<PaperQuestion> questions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paper.getId())
                        .eq(PaperQuestion::getIsObjective, 1) // 客观题
        );

        // 查询该科目的所有答题卡
        List<AnswerSheet> answerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getDeleted, 0)
        );

        // 为每份答题卡的每道客观题评分
        for (AnswerSheet answerSheet : answerSheets) {
            answerSheetDetailService.recognizeObjectiveAnswers(answerSheet.getId());
        }
    }

    private void fillQuestionPreview(MarkingRecordVO vo) {
        if (vo == null || vo.getAnswerSheetId() == null || vo.getQuestionId() == null) {
            return;
        }
        try {
            String previewUrl = answerSheetDetailService.getQuestionPreviewUrl(vo.getAnswerSheetId(), vo.getQuestionId());
            if (previewUrl != null && !previewUrl.isBlank()) {
                vo.setAnswerImageUrl(previewUrl);
            }
        } catch (BusinessException ignored) {
            // 题图预览不存在时退回原始整卷图
        }
    }
}
