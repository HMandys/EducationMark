package com.edumark.marking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetDetail;
import com.edumark.file.mapper.AnswerSheetDetailMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.marking.dto.MarkingTaskAssignDTO;
import com.edumark.marking.dto.MarkingTaskQueryDTO;
import com.edumark.marking.entity.MarkingRecord;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.entity.MarkingTaskAssign;
import com.edumark.marking.mapper.MarkingRecordMapper;
import com.edumark.marking.mapper.MarkingTaskAssignMapper;
import com.edumark.marking.mapper.MarkingTaskMapper;
import com.edumark.marking.service.MarkingTaskService;
import com.edumark.marking.vo.MarkingTaskVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 阅卷任务服务实现
 *
 * @author EduMark
 */
@Service
public class MarkingTaskServiceImpl extends ServiceImpl<MarkingTaskMapper, MarkingTask> implements MarkingTaskService {

    private static final int DETAIL_STATUS_SUBJECTIVE_ANOMALY = 3;

    @Resource
    private MarkingTaskAssignMapper markingTaskAssignMapper;

    @Resource
    private MarkingRecordMapper markingRecordMapper;

    @Resource
    private ExamSubjectMapper examSubjectMapper;

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerSheetDetailMapper answerSheetDetailMapper;

    @Resource
    private AnswerSheetDetailService answerSheetDetailService;

    @Override
    public PageResult<MarkingTaskVO> pageQuery(MarkingTaskQueryDTO query) {
        Page<MarkingTaskVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query.getExamId(), query.getExamSubjectId(), query.getStatus());
        return PageResult.of(page);
    }

    @Override
    public MarkingTaskVO getDetail(Long id) {
        MarkingTaskVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        // 查询分配的教师列表
        vo.setAssigns(markingTaskAssignMapper.selectListByTaskId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateTasks(Long examSubjectId) {
        // 检查考试科目是否存在
        ExamSubject examSubject = examSubjectMapper.selectById(examSubjectId);
        if (examSubject == null) {
            throw new BusinessException("考试科目不存在");
        }

        // 检查是否已生成过阅卷任务
        Long existCount = lambdaQuery()
                .eq(MarkingTask::getExamSubjectId, examSubjectId)
                .count();
        if (existCount > 0) {
            throw new BusinessException("该科目已生成过阅卷任务");
        }

        // 查询该科目的试卷
        Paper paper = paperMapper.selectOne(
                new LambdaQueryWrapper<Paper>()
                        .eq(Paper::getExamSubjectId, examSubjectId)
                        .eq(Paper::getStatus, 1)
                        .last("LIMIT 1")
        );
        if (paper == null) {
            throw new BusinessException("该科目暂无已完成的试卷");
        }

        // 查询试卷的主观题
        List<PaperQuestion> questions = paperQuestionMapper.selectList(
                new LambdaQueryWrapper<PaperQuestion>()
                        .eq(PaperQuestion::getPaperId, paper.getId())
                        .eq(PaperQuestion::getIsObjective, 0) // 主观题
        );

        // 确保已识别成功的答题卡都初始化了题目明细
        List<AnswerSheet> readyAnswerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getDeleted, 0)
                        .eq(AnswerSheet::getStatus, 2)
                        .isNotNull(AnswerSheet::getStudentId)
        );
        for (AnswerSheet answerSheet : readyAnswerSheets) {
            answerSheetDetailService.initializeQuestionDetails(answerSheet.getId());
        }

        // 为每道主观题创建阅卷任务
        for (PaperQuestion question : questions) {
            Long detailCount = answerSheetDetailMapper.selectCount(
                    new LambdaQueryWrapper<AnswerSheetDetail>()
                            .eq(AnswerSheetDetail::getQuestionId, question.getId())
                            .eq(AnswerSheetDetail::getDeleted, 0)
            );
            if (detailCount == 0) {
                continue;
            }

            MarkingTask task = new MarkingTask();
            task.setExamId(examSubject.getExamId());
            task.setExamSubjectId(examSubjectId);
            task.setQuestionId(question.getId());
            task.setName("第" + question.getQuestionNo() + "题阅卷任务");
            task.setTaskType(2); // 主观题
            task.setTotalCount(detailCount.intValue());
            task.setCompletedCount(0);
            task.setPendingCount(detailCount.intValue());
            task.setEnableDoubleMarking(question.getEnableDoubleMarking() != null ? question.getEnableDoubleMarking() : 0);
            task.setDoubleMarkingThreshold(question.getDoubleMarkingThreshold());
            task.setStatus(0); // 未开始
            save(task);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        MarkingTask task = getById(id);
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只能删除未开始的任务");
        }
        // 删除任务分配
        markingTaskAssignMapper.deleteByTaskId(id);
        // 删除任务
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(MarkingTaskAssignDTO dto) {
        MarkingTask task = getById(dto.getTaskId());
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只能分配未开始的任务");
        }

        // 删除原有分配
        markingTaskAssignMapper.deleteByTaskId(dto.getTaskId());

        // 创建新的分配
        for (MarkingTaskAssignDTO.TeacherAssign ta : dto.getAssigns()) {
            MarkingTaskAssign assign = new MarkingTaskAssign();
            assign.setTaskId(dto.getTaskId());
            assign.setTeacherId(ta.getTeacherId());
            assign.setAssignCount(ta.getAssignCount() != null ? ta.getAssignCount() : 0);
            assign.setCompletedCount(0);
            assign.setMarkingRole(ta.getMarkingRole() != null ? ta.getMarkingRole() : 1);
            assign.setStatus(0);
            markingTaskAssignMapper.insert(assign);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startTask(Long id) {
        MarkingTask task = getById(id);
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只能开始未开始的任务");
        }

        // 检查是否已分配教师
        Long assignCount = markingTaskAssignMapper.selectCount(
                new LambdaQueryWrapper<MarkingTaskAssign>()
                        .eq(MarkingTaskAssign::getTaskId, id)
        );
        if (assignCount == 0) {
            throw new BusinessException("请先分配阅卷教师");
        }

        // 生成阅卷记录，并同步可进入阅卷的有效数量
        int eligibleCount = generateMarkingRecords(task);
        if (eligibleCount <= 0) {
            throw new BusinessException("当前没有可进入阅卷的主观题明细，请先处理异常项");
        }

        // 更新状态为进行中
        task.setTotalCount(eligibleCount);
        task.setCompletedCount(0);
        task.setPendingCount(eligibleCount);
        task.setStatus(1);
        updateById(task);
    }

    /**
     * 生成阅卷记录
     */
    private int generateMarkingRecords(MarkingTask task) {
        List<AnswerSheetDetail> details = answerSheetDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerSheetDetail>()
                        .eq(AnswerSheetDetail::getQuestionId, task.getQuestionId())
                        .eq(AnswerSheetDetail::getDeleted, 0)
                        .ne(AnswerSheetDetail::getStatus, DETAIL_STATUS_SUBJECTIVE_ANOMALY)
                        .orderByAsc(AnswerSheetDetail::getAnswerSheetId)
                        .orderByAsc(AnswerSheetDetail::getId)
        );

        // 查询分配的教师列表（按角色分组）
        List<MarkingTaskAssign> firstMarkers = markingTaskAssignMapper.selectList(
                new LambdaQueryWrapper<MarkingTaskAssign>()
                        .eq(MarkingTaskAssign::getTaskId, task.getId())
                        .eq(MarkingTaskAssign::getMarkingRole, 1)
        );

        List<MarkingTaskAssign> secondMarkers = null;
        if (task.getEnableDoubleMarking() == 1) {
            secondMarkers = markingTaskAssignMapper.selectList(
                    new LambdaQueryWrapper<MarkingTaskAssign>()
                            .eq(MarkingTaskAssign::getTaskId, task.getId())
                            .eq(MarkingTaskAssign::getMarkingRole, 2)
            );
        }

        if (firstMarkers.isEmpty()) {
            throw new BusinessException("任务未分配一评教师");
        }

        // 获取题目满分
        PaperQuestion question = paperQuestionMapper.selectById(task.getQuestionId());
        Integer fullScore = question != null ? question.getScore() : 0;

        int firstIndex = 0;
        int secondIndex = 0;
        int eligibleCount = 0;

        for (AnswerSheetDetail detail : details) {
            AnswerSheet answerSheet = answerSheetMapper.selectById(detail.getAnswerSheetId());
            if (answerSheet == null || answerSheet.getStudentId() == null) {
                continue;
            }
            eligibleCount++;

            // 一评
            MarkingTaskAssign firstAssign = firstMarkers.get(firstIndex % firstMarkers.size());
            createMarkingRecord(task, answerSheet, firstAssign.getTeacherId(), 1, fullScore);
            firstAssign.setAssignCount(firstAssign.getAssignCount() + 1);
            markingTaskAssignMapper.updateById(firstAssign);
            firstIndex++;

            // 双评模式下创建二评记录
            if (task.getEnableDoubleMarking() == 1 && secondMarkers != null && !secondMarkers.isEmpty()) {
                MarkingTaskAssign secondAssign = secondMarkers.get(secondIndex % secondMarkers.size());
                createMarkingRecord(task, answerSheet, secondAssign.getTeacherId(), 2, fullScore);
                secondAssign.setAssignCount(secondAssign.getAssignCount() + 1);
                markingTaskAssignMapper.updateById(secondAssign);
                secondIndex++;
            }
        }
        return eligibleCount;
    }

    /**
     * 创建阅卷记录
     */
    private void createMarkingRecord(MarkingTask task, AnswerSheet answerSheet, Long teacherId, int markingRole, Integer fullScore) {
        MarkingRecord record = new MarkingRecord();
        record.setTaskId(task.getId());
        record.setAnswerSheetId(answerSheet.getId());
        record.setQuestionId(task.getQuestionId());
        record.setStudentId(answerSheet.getStudentId());
        record.setTeacherId(teacherId);
        record.setMarkingRole(markingRole);
        record.setFullScore(fullScore);
        record.setStatus(0); // 待评
        markingRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long id) {
        MarkingTask task = getById(id);
        if (task == null) {
            throw new BusinessException("阅卷任务不存在");
        }
        if (task.getStatus() != 1) {
            throw new BusinessException("只能完成进行中的任务");
        }

        // 检查是否还有未完成的阅卷记录
        Long pendingCount = markingRecordMapper.selectCount(
                new LambdaQueryWrapper<MarkingRecord>()
                        .eq(MarkingRecord::getTaskId, id)
                        .eq(MarkingRecord::getStatus, 0)
        );
        if (pendingCount > 0) {
            throw new BusinessException("还有未完成的阅卷记录，无法完成任务");
        }

        task.setStatus(2);
        updateById(task);
    }

    @Override
    public List<MarkingTaskVO> listByExamSubjectId(Long examSubjectId) {
        return baseMapper.selectListByExamSubjectId(examSubjectId);
    }

    @Override
    public void updateProgress(Long taskId) {
        // 统计已完成的阅卷记录数
        Long completedCount = markingRecordMapper.selectCount(
                new LambdaQueryWrapper<MarkingRecord>()
                        .eq(MarkingRecord::getTaskId, taskId)
                        .eq(MarkingRecord::getStatus, 1)
        );

        Long pendingCount = markingRecordMapper.selectCount(
                new LambdaQueryWrapper<MarkingRecord>()
                        .eq(MarkingRecord::getTaskId, taskId)
                        .eq(MarkingRecord::getStatus, 0)
        );

        lambdaUpdate()
                .eq(MarkingTask::getId, taskId)
                .set(MarkingTask::getCompletedCount, completedCount.intValue())
                .set(MarkingTask::getPendingCount, pendingCount.intValue())
                .update();
    }
}
