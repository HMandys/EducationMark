package com.edumark.marking.service.impl;

fimport com.edumark.answersheet.service.AnswerSheetTemplateService;
import com.edumark.answersheet.vo.AnswerSheetRegionVO;
import com.edumark.answersheet.vo.AnswerSheetTemplateVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetDetail;
import com.edumark.file.mapper.AnswerSheetDetailMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.marking.dto.MarkingTaskAssignDTO;
import com.edumark.marking.entity.MarkingArbitration;
import com.edumark.marking.dto.MarkingTaskQueryDTO;
import com.edumark.marking.entity.MarkingRecord;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.entity.MarkingTaskAssign;
import com.edumark.marking.mapper.MarkingArbitrationMapper;
import com.edumark.marking.mapper.MarkingRecordMapper;
import com.edumark.marking.mapper.MarkingTaskAssignMapper;
import com.edumark.marking.mapper.MarkingTaskMapper;
import com.edumark.marking.service.MarkingTaskService;
import com.edumark.marking.vo.MarkingTaskVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private MarkingArbitrationMapper markingArbitrationMapper;

    @Resource
    private ExamMapper examMapper;

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

    @Resource
    private AnswerSheetTemplateService answerSheetTemplateService;

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

        // 查询该科目的试卷（可选，没有试卷也可以创建综合阅卷任务）
        Paper paper = paperMapper.selectOne(
                new LambdaQueryWrapper<Paper>()
                        .eq(Paper::getExamSubjectId, examSubjectId)
                        .eq(Paper::getStatus, 1)
                        .last("LIMIT 1")
        );

        // 查询待阅卷的答题卡
        List<AnswerSheet> readyAnswerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getDeleted, 0)
                        .eq(AnswerSheet::getStatus, 2)
                        .isNotNull(AnswerSheet::getStudentId)
        );

        if (readyAnswerSheets.isEmpty()) {
            throw new BusinessException("该科目暂无待阅卷的答题卡，请先上传并识别答题卡");
        }

        // 确保已识别成功的答题卡都初始化了题目明细
        for (AnswerSheet answerSheet : readyAnswerSheets) {
            answerSheetDetailService.initializeQuestionDetails(answerSheet.getId());
        }

        // 查询试卷的主观题
        List<PaperQuestion> questions = new ArrayList<>();
        if (paper != null) {
            questions = paperQuestionMapper.selectList(
                    new LambdaQueryWrapper<PaperQuestion>()
                            .eq(PaperQuestion::getPaperId, paper.getId())
                            .eq(PaperQuestion::getIsObjective, 0) // 主观题
            );
        }

        AnswerSheetTemplateVO template = answerSheetTemplateService.getByExamSubjectId(examSubjectId);
        Set<Long> aiManagedQuestionIds = getAiManagedQuestionIds(template);

        // 没有试卷主观题时，改为按答题卡模板里的主观题题号拆分单题任务
        if (questions.isEmpty()) {
            createTemplateQuestionTasks(examSubject, readyAnswerSheets, template);
            return;
        }

        // 为每道主观题创建阅卷任务
        for (PaperQuestion question : questions) {
            boolean aiManagedQuestion = aiManagedQuestionIds.contains(question.getId());
            List<AnswerSheetDetail> manualDetails = answerSheetDetailMapper.selectList(
                    new LambdaQueryWrapper<AnswerSheetDetail>()
                            .eq(AnswerSheetDetail::getQuestionId, question.getId())
                            .eq(AnswerSheetDetail::getDeleted, 0)
            );
            if (aiManagedQuestion) {
                manualDetails = manualDetails.stream()
                        .filter(detail -> detail.getStatus() == null || detail.getStatus() != 1)
                        .toList();
            }
            if (manualDetails.isEmpty()) {
                continue;
            }

            MarkingTask task = new MarkingTask();
            task.setExamId(examSubject.getExamId());
            task.setExamSubjectId(examSubjectId);
            task.setQuestionId(question.getId());
            task.setName("第" + question.getQuestionNo() + "题阅卷任务");
            task.setTaskType(2); // 主观题
            task.setTotalCount(manualDetails.size());
            task.setCompletedCount(0);
            task.setPendingCount(manualDetails.size());
            task.setEnableDoubleMarking(question.getEnableDoubleMarking() != null ? question.getEnableDoubleMarking() : 0);
            task.setDoubleMarkingThreshold(question.getDoubleMarkingThreshold());
            task.setStatus(0); // 未开始
            save(task);
        }
    }

    private void createTemplateQuestionTasks(ExamSubject examSubject, List<AnswerSheet> readyAnswerSheets, AnswerSheetTemplateVO template) {
        if (readyAnswerSheets == null || readyAnswerSheets.isEmpty()) {
            throw new BusinessException("该科目暂无待阅卷的答题卡，请先上传并识别答题卡");
        }

        List<Long> answerSheetIds = readyAnswerSheets.stream()
                .map(AnswerSheet::getId)
                .toList();

        List<AnswerSheetDetail> subjectiveDetails = answerSheetDetailMapper.selectList(
                new LambdaQueryWrapper<AnswerSheetDetail>()
                        .in(AnswerSheetDetail::getAnswerSheetId, answerSheetIds)
                        .eq(AnswerSheetDetail::getDeleted, 0)
                        .isNotNull(AnswerSheetDetail::getQuestionNo)
                        .and(wrapper -> wrapper
                                .ne(AnswerSheetDetail::getIsObjective, 1)
                                .or()
                                .isNull(AnswerSheetDetail::getIsObjective))
                        .orderByAsc(AnswerSheetDetail::getQuestionNo)
                        .orderByAsc(AnswerSheetDetail::getId)
        );

        Set<Integer> aiManagedQuestionNos = getAiManagedQuestionNos(template);
        subjectiveDetails = subjectiveDetails.stream()
                .filter(detail -> !aiManagedQuestionNos.contains(detail.getQuestionNo())
                        || detail.getStatus() == null
                        || detail.getStatus() != 1)
                .toList();

        Map<Integer, List<AnswerSheetDetail>> detailMapByQuestionNo = subjectiveDetails.stream()
                .filter(detail -> detail.getQuestionNo() != null)
                .collect(Collectors.groupingBy(
                        AnswerSheetDetail::getQuestionNo,
                        java.util.TreeMap::new,
                        Collectors.toList()
                ));

        if (detailMapByQuestionNo.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, List<AnswerSheetDetail>> entry : detailMapByQuestionNo.entrySet()) {
            Integer questionNo = entry.getKey();
            List<AnswerSheetDetail> details = entry.getValue();
            int totalCount = details.size();
            if (questionNo == null || totalCount <= 0) {
                continue;
            }

            MarkingTask task = new MarkingTask();
            task.setExamId(examSubject.getExamId());
            task.setExamSubjectId(examSubject.getId());
            task.setQuestionId(toTemplateQuestionId(questionNo));
            task.setName("第" + questionNo + "题阅卷任务");
            task.setTaskType(2);
            task.setTotalCount(totalCount);
            task.setCompletedCount(0);
            task.setPendingCount(totalCount);
            task.setEnableDoubleMarking(0);
            task.setDoubleMarkingThreshold(null);
            task.setStatus(0);
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
        if (task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1) {
            Long arbitrationAssignCount = markingTaskAssignMapper.selectCount(
                    new LambdaQueryWrapper<MarkingTaskAssign>()
                            .eq(MarkingTaskAssign::getTaskId, id)
                            .eq(MarkingTaskAssign::getMarkingRole, 3)
            );
            if (arbitrationAssignCount == null || arbitrationAssignCount == 0) {
                throw new BusinessException("双评任务请先分配仲裁教师");
            }
        }

        // 生成阅卷记录，并同步可进入阅卷的有效数量
        int eligibleCount;
        if (task.getQuestionId() == null) {
            // 综合阅卷：按答题卡生成阅卷记录
            eligibleCount = generateMarkingRecordsForComprehensive(task);
        } else {
            // 按题目生成阅卷记录
            eligibleCount = generateMarkingRecords(task);
        }
        if (eligibleCount <= 0) {
            throw new BusinessException("当前没有可阅卷的答题卡，请先上传并识别答题卡");
        }

        // 将该科目的"待阅卷"答题卡状态更新为"阅卷中"
        updateAnswerSheetsStatusToMarking(task.getExamSubjectId());

        // 任务真正开始时，同步将考试推进到"阅卷中"
        updateExamStatusToMarking(task.getExamId());

        // 更新状态为进行中
        task.setTotalCount(eligibleCount);
        task.setCompletedCount(0);
        task.setPendingCount(eligibleCount);
        task.setStatus(1);
        updateById(task);
    }

    /**
     * 将该科目的"待阅卷"答题卡状态更新为"阅卷中"
     */
    private void updateAnswerSheetsStatusToMarking(Long examSubjectId) {
        answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, examSubjectId)
                        .eq(AnswerSheet::getStatus, 2)
                        .eq(AnswerSheet::getDeleted, 0)
        ).forEach(sheet -> {
            sheet.setStatus(3);
            answerSheetMapper.updateById(sheet);
        });
    }

    private void updateExamStatusToMarking(Long examId) {
        if (examId == null) {
            return;
        }
        Exam exam = examMapper.selectById(examId);
        if (exam == null || exam.getStatus() == null) {
            return;
        }
        if (exam.getStatus() < 3) {
            exam.setStatus(3);
            exam.setUpdateTime(LocalDateTime.now());
            examMapper.updateById(exam);
        }
    }

    /**
     * 生成阅卷记录
     */
    private int generateMarkingRecords(MarkingTask task) {
        LambdaQueryWrapper<AnswerSheetDetail> detailQuery = new LambdaQueryWrapper<AnswerSheetDetail>()
                .eq(AnswerSheetDetail::getDeleted, 0)
                .ne(AnswerSheetDetail::getStatus, DETAIL_STATUS_SUBJECTIVE_ANOMALY)
                .orderByAsc(AnswerSheetDetail::getAnswerSheetId)
                .orderByAsc(AnswerSheetDetail::getId);

        Integer templateQuestionNo = resolveTemplateQuestionNo(task.getQuestionId());
        boolean templateDrivenTask = templateQuestionNo != null;
        if (templateDrivenTask) {
            detailQuery.eq(AnswerSheetDetail::getQuestionNo, templateQuestionNo)
                    .and(wrapper -> wrapper
                            .ne(AnswerSheetDetail::getIsObjective, 1)
                            .or()
                            .isNull(AnswerSheetDetail::getIsObjective));
        } else {
            detailQuery.eq(AnswerSheetDetail::getQuestionId, task.getQuestionId());
        }

        List<AnswerSheetDetail> details = answerSheetDetailMapper.selectList(detailQuery);
        AnswerSheetTemplateVO template = task.getExamSubjectId() != null
                ? answerSheetTemplateService.getByExamSubjectId(task.getExamSubjectId())
                : null;
        details = filterAiManagedCompletedDetails(details, task, template);

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
        if (task.getEnableDoubleMarking() == 1 && (secondMarkers == null || secondMarkers.isEmpty())) {
            throw new BusinessException("双评任务未分配二评教师");
        }

        Integer fullScore = 0;
        if (!templateDrivenTask) {
            PaperQuestion question = paperQuestionMapper.selectById(task.getQuestionId());
            fullScore = question != null ? question.getScore() : 0;
        }

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
            Integer recordFullScore = templateDrivenTask
                    ? (detail.getFullScore() != null ? detail.getFullScore() : 0)
                    : fullScore;

            createMarkingRecord(task, answerSheet, firstAssign.getTeacherId(), 1, recordFullScore);
            firstAssign.setAssignCount(firstAssign.getAssignCount() + 1);
            markingTaskAssignMapper.updateById(firstAssign);
            firstIndex++;

            // 双评模式下创建二评记录
            if (task.getEnableDoubleMarking() == 1 && secondMarkers != null && !secondMarkers.isEmpty()) {
                MarkingTaskAssign secondAssign = secondMarkers.get(secondIndex % secondMarkers.size());
                createMarkingRecord(task, answerSheet, secondAssign.getTeacherId(), 2, recordFullScore);
                secondAssign.setAssignCount(secondAssign.getAssignCount() + 1);
                markingTaskAssignMapper.updateById(secondAssign);
                secondIndex++;
            }
        }
        return eligibleCount;
    }

    private List<AnswerSheetDetail> filterAiManagedCompletedDetails(List<AnswerSheetDetail> details,
                                                                    MarkingTask task,
                                                                    AnswerSheetTemplateVO template) {
        if (details == null || details.isEmpty() || template == null) {
            return details;
        }
        Integer templateQuestionNo = resolveTemplateQuestionNo(task.getQuestionId());
        if (templateQuestionNo != null && getAiManagedQuestionNos(template).contains(templateQuestionNo)) {
            return details.stream()
                    .filter(detail -> detail.getStatus() == null || detail.getStatus() != 1)
                    .toList();
        }
        if (task.getQuestionId() != null && task.getQuestionId() > 0 && getAiManagedQuestionIds(template).contains(task.getQuestionId())) {
            return details.stream()
                    .filter(detail -> detail.getStatus() == null || detail.getStatus() != 1)
                    .toList();
        }
        return details;
    }

    private Set<Long> getAiManagedQuestionIds(AnswerSheetTemplateVO template) {
        if (template == null || template.getRegions() == null) {
            return Set.of();
        }
        Set<Long> result = new HashSet<>();
        for (AnswerSheetRegionVO region : template.getRegions()) {
            if (!isAiManagedFillBlankRegion(region) || region.getQuestionIds() == null) {
                continue;
            }
            result.addAll(region.getQuestionIds());
        }
        return result;
    }

    private Set<Integer> getAiManagedQuestionNos(AnswerSheetTemplateVO template) {
        if (template == null || template.getRegions() == null) {
            return Set.of();
        }
        Set<Integer> result = new HashSet<>();
        for (AnswerSheetRegionVO region : template.getRegions()) {
            if (!isAiManagedFillBlankRegion(region)) {
                continue;
            }
            if (region.getQuestionStart() != null) {
                result.add(region.getQuestionStart());
            }
        }
        return result;
    }

    private boolean isAiManagedFillBlankRegion(AnswerSheetRegionVO region) {
        if (region == null || !Integer.valueOf(2).equals(region.getRegionType()) || region.getConfig() == null) {
            return false;
        }
        Object enabled = region.getConfig().get("enableAiMarking");
        return enabled instanceof Boolean && (Boolean) enabled;
    }

    /**
     * 综合阅卷：按答题卡生成阅卷记录（不依赖题目明细）
     */
    private int generateMarkingRecordsForComprehensive(MarkingTask task) {
        // 查询待阅卷的答题卡
        List<AnswerSheet> answerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, task.getExamSubjectId())
                        .eq(AnswerSheet::getDeleted, 0)
                        .in(AnswerSheet::getStatus, 2, 3) // 待阅卷或阅卷中
                        .isNotNull(AnswerSheet::getStudentId)
                        .orderByAsc(AnswerSheet::getId)
        );

        // 查询分配的教师
        List<MarkingTaskAssign> firstMarkers = markingTaskAssignMapper.selectList(
                new LambdaQueryWrapper<MarkingTaskAssign>()
                        .eq(MarkingTaskAssign::getTaskId, task.getId())
                        .eq(MarkingTaskAssign::getMarkingRole, 1)
        );
        if (firstMarkers.isEmpty()) {
            throw new BusinessException("任务未分配评阅教师");
        }

        int firstIndex = 0;
        int eligibleCount = 0;

        for (AnswerSheet answerSheet : answerSheets) {
            eligibleCount++;
            MarkingTaskAssign firstAssign = firstMarkers.get(firstIndex % firstMarkers.size());
            createMarkingRecord(task, answerSheet, firstAssign.getTeacherId(), 1, 0);
            firstAssign.setAssignCount(firstAssign.getAssignCount() + 1);
            markingTaskAssignMapper.updateById(firstAssign);
            firstIndex++;
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

    private Long toTemplateQuestionId(Integer questionNo) {
        return questionNo == null ? null : -questionNo.longValue();
    }

    private Integer resolveTemplateQuestionNo(Long questionId) {
        if (questionId == null || questionId >= 0) {
            return null;
        }
        return Math.toIntExact(-questionId);
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
        if (task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1) {
            Long pendingArbitrationCount = markingArbitrationMapper.selectCount(
                    new LambdaQueryWrapper<MarkingArbitration>()
                            .eq(MarkingArbitration::getTaskId, id)
                            .eq(MarkingArbitration::getStatus, 0)
            );
            if (pendingArbitrationCount != null && pendingArbitrationCount > 0) {
                throw new BusinessException("还有待仲裁记录，无法完成任务");
            }
        }

        task.setStatus(2);
        updateById(task);

        // 任务完成后，检查并更新关联答题卡的状态
        updateAnswerSheetsStatusAfterTaskComplete(task);

        // 检查并更新考试状态
        updateExamStatusIfAllTasksCompleted(task);
    }

    /**
     * 阅卷任务完成后，更新关联答题卡的状态
     * 检查答题卡的所有阅卷任务是否都已完成，如果是则将答题卡状态更新为"已完成"
     */
    private void updateAnswerSheetsStatusAfterTaskComplete(MarkingTask completedTask) {
        // 查询该考试科目的所有阅卷任务
        List<MarkingTask> allTasks = baseMapper.selectList(
                new LambdaQueryWrapper<MarkingTask>()
                        .eq(MarkingTask::getExamSubjectId, completedTask.getExamSubjectId())
                        .eq(MarkingTask::getDeleted, 0)
        );

        // 检查是否所有任务都已完成
        boolean allTasksCompleted = allTasks.stream()
                .allMatch(t -> t.getStatus() != null && t.getStatus() == 2);

        if (!allTasksCompleted) {
            return;
        }

        // 所有任务都完成，将该科目的所有"阅卷中"答题卡更新为"已完成"
        List<AnswerSheet> markingAnswerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamSubjectId, completedTask.getExamSubjectId())
                        .eq(AnswerSheet::getStatus, 3)
                        .eq(AnswerSheet::getDeleted, 0)
        );

        for (AnswerSheet answerSheet : markingAnswerSheets) {
            answerSheet.setStatus(4);
            answerSheetMapper.updateById(answerSheet);
        }
    }

    /**
     * 检查考试的所有阅卷任务是否都已完成，如果是则更新考试状态为"已完成"
     */
    private void updateExamStatusIfAllTasksCompleted(MarkingTask completedTask) {
        // 1. 查询该考试的所有阅卷任务
        List<MarkingTask> allExamTasks = baseMapper.selectList(
                new LambdaQueryWrapper<MarkingTask>()
                        .eq(MarkingTask::getExamId, completedTask.getExamId())
                        .eq(MarkingTask::getDeleted, 0)
        );

        // 2. 检查是否所有任务都已完成（status=2）
        boolean allTasksCompleted = allExamTasks.stream()
                .allMatch(t -> t.getStatus() != null && t.getStatus() == 2);

        if (!allTasksCompleted) {
            return;  // 还有未完成的任务
        }

        // 3. 所有任务都完成，更新考试状态
        Exam exam = examMapper.selectById(completedTask.getExamId());
        if (exam != null && exam.getStatus() != null && exam.getStatus() < 4) {
            // 只要还没到"已完成/已发布"，就按任务完成结果自动收口到"已完成"
            exam.setStatus(4);
            exam.setUpdateTime(LocalDateTime.now());
            examMapper.updateById(exam);
        }
    }

    @Override
    public List<MarkingTaskVO> listByExamSubjectId(Long examSubjectId) {
        return baseMapper.selectListByExamSubjectId(examSubjectId);
    }

    @Override
    public void updateProgress(Long taskId) {
        MarkingTask task = getById(taskId);
        if (task == null) {
            return;
        }

        List<MarkingRecord> records = markingRecordMapper.selectList(
                new LambdaQueryWrapper<MarkingRecord>()
                        .eq(MarkingRecord::getTaskId, taskId)
        );
        Map<Long, List<MarkingRecord>> recordsByAnswerSheet = records.stream()
                .collect(Collectors.groupingBy(MarkingRecord::getAnswerSheetId));

        Set<Long> pendingArbitrationAnswerSheetIds = new HashSet<>(
                markingArbitrationMapper.selectList(
                        new LambdaQueryWrapper<MarkingArbitration>()
                                .eq(MarkingArbitration::getTaskId, taskId)
                                .eq(MarkingArbitration::getStatus, 0)
                ).stream().map(MarkingArbitration::getAnswerSheetId).collect(Collectors.toSet())
        );

        int completedSampleCount = 0;
        for (List<MarkingRecord> sampleRecords : recordsByAnswerSheet.values()) {
            if (sampleRecords.isEmpty()) {
                continue;
            }

            Long answerSheetId = sampleRecords.get(0).getAnswerSheetId();
            boolean firstCompleted = sampleRecords.stream().anyMatch(record ->
                    record.getMarkingRole() != null && record.getMarkingRole() == 1 && record.getStatus() != null && record.getStatus() == 1);

            if (task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1) {
                boolean secondCompleted = sampleRecords.stream().anyMatch(record ->
                        record.getMarkingRole() != null && record.getMarkingRole() == 2 && record.getStatus() != null && record.getStatus() == 1);
                if (firstCompleted && secondCompleted && !pendingArbitrationAnswerSheetIds.contains(answerSheetId)) {
                    completedSampleCount++;
                }
            } else if (firstCompleted) {
                completedSampleCount++;
            }
        }

        int totalCount = task.getTotalCount() != null ? task.getTotalCount() : recordsByAnswerSheet.size();
        int pendingCount = Math.max(totalCount - completedSampleCount, 0);

        lambdaUpdate()
                .eq(MarkingTask::getId, taskId)
                .set(MarkingTask::getCompletedCount, completedSampleCount)
                .set(MarkingTask::getPendingCount, pendingCount)
                .update();

        autoCompleteTaskIfFinished(task, pendingCount);
    }

    private void autoCompleteTaskIfFinished(MarkingTask task, int pendingCount) {
        if (task == null || task.getId() == null || task.getStatus() == null || task.getStatus() != 1) {
            return;
        }
        if (pendingCount > 0) {
            return;
        }

        Long pendingRecordCount = markingRecordMapper.selectCount(
                new LambdaQueryWrapper<MarkingRecord>()
                        .eq(MarkingRecord::getTaskId, task.getId())
                        .eq(MarkingRecord::getStatus, 0)
        );
        if (pendingRecordCount != null && pendingRecordCount > 0) {
            return;
        }

        if (task.getEnableDoubleMarking() != null && task.getEnableDoubleMarking() == 1) {
            Long pendingArbitrationCount = markingArbitrationMapper.selectCount(
                    new LambdaQueryWrapper<MarkingArbitration>()
                            .eq(MarkingArbitration::getTaskId, task.getId())
                            .eq(MarkingArbitration::getStatus, 0)
            );
            if (pendingArbitrationCount != null && pendingArbitrationCount > 0) {
                return;
            }
        }

        task.setStatus(2);
        updateById(task);
        updateAnswerSheetsStatusAfterTaskComplete(task);
        updateExamStatusIfAllTasksCompleted(task);
    }
}
