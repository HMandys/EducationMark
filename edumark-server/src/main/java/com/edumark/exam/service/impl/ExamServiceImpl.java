package com.edumark.exam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.answersheet.entity.AnswerSheetTemplate;
import com.edumark.answersheet.mapper.AnswerSheetTemplateMapper;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.dto.ExamDTO;
import com.edumark.exam.dto.ExamQueryDTO;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.entity.ExamClass;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.entity.PaperQuestion;
import com.edumark.exam.mapper.ExamClassMapper;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.mapper.PaperQuestionMapper;
import com.edumark.exam.service.ExamService;
import com.edumark.exam.vo.ExamPublishCheckVO;
import com.edumark.exam.vo.ExamVO;
import com.edumark.school.mapper.ClassInfoMapper;
import com.edumark.school.mapper.GradeMapper;
import com.edumark.school.vo.ClassInfoVO;
import com.edumark.school.vo.GradeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 考试服务实现
 *
 * @author EduMark
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Resource
    private ExamClassMapper examClassMapper;

    @Resource
    private GradeMapper gradeMapper;

    @Resource
    private ClassInfoMapper classInfoMapper;

    @Resource
    private ExamSubjectMapper examSubjectMapper;

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private PaperQuestionMapper paperQuestionMapper;

    @Resource
    private AnswerSheetTemplateMapper answerSheetTemplateMapper;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public PageResult<ExamVO> pageQuery(ExamQueryDTO query) {
        Page<ExamVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ExamVO getDetail(Long id) {
        ExamVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("考试不存在");
        }
        // 查询关联班级
        vo.setClasses(examClassMapper.selectListByExamId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ExamDTO dto) {
        validateExamDTO(dto);

        // 检查考试编码是否重复
        if (StringUtils.hasText(dto.getCode())) {
            long count = lambdaQuery()
                    .eq(Exam::getSchoolId, dto.getSchoolId())
                    .eq(Exam::getCode, dto.getCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("考试编码已存在");
            }
        }

        Exam exam = new Exam();
        BeanUtils.copyProperties(dto, exam);
        if (StringUtils.hasText(dto.getStartTime())) {
            exam.setStartTime(LocalDateTime.parse(dto.getStartTime(), DTF));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            exam.setEndTime(LocalDateTime.parse(dto.getEndTime(), DTF));
        }
        exam.setStatus(0); // 草稿状态
        exam.setTotalScore(0);
        exam.setStudentCount(0);
        save(exam);

        // 保存班级关联
        saveExamClasses(exam.getId(), dto.getClassIds());

        return exam.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExamDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("考试ID不能为空");
        }
        validateExamDTO(dto);
        Exam existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("考试不存在");
        }
        if (existing.getStatus() >= 2) {
            throw new BusinessException("考试进行中或已结束，不能修改");
        }

        // 检查考试编码是否重复
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existing.getCode())) {
            long count = lambdaQuery()
                    .eq(Exam::getSchoolId, dto.getSchoolId())
                    .eq(Exam::getCode, dto.getCode())
                    .ne(Exam::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("考试编码已存在");
            }
        }

        Exam exam = new Exam();
        BeanUtils.copyProperties(dto, exam);
        if (StringUtils.hasText(dto.getStartTime())) {
            exam.setStartTime(LocalDateTime.parse(dto.getStartTime(), DTF));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            exam.setEndTime(LocalDateTime.parse(dto.getEndTime(), DTF));
        }
        updateById(exam);

        // 更新班级关联
        examClassMapper.deleteByExamId(dto.getId());
        saveExamClasses(dto.getId(), dto.getClassIds());
    }

    private void saveExamClasses(Long examId, List<Long> classIds) {
        if (classIds != null && !classIds.isEmpty()) {
            for (Long classId : classIds) {
                ExamClass examClass = new ExamClass();
                examClass.setExamId(examId);
                examClass.setClassId(classId);
                examClassMapper.insert(examClass);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (exam.getStatus() >= 2) {
            throw new BusinessException("考试进行中或已结束，不能删除");
        }
        removeById(id);
        examClassMapper.deleteByExamId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的考试");
        }
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (status == null || status < 0 || status > 5) {
            throw new BusinessException("无效的考试状态");
        }
        int current = exam.getStatus() != null ? exam.getStatus() : 0;
        // 合法状态转换：只允许向前推进或从已发布撤回到已完成
        boolean valid = (status == current + 1)
                || (current == 5 && status == 4); // 撤回发布
        if (!valid) {
            throw new BusinessException("不允许从「" + getStatusName(current) + "」变更为「" + getStatusName(status) + "」");
        }
        exam.setStatus(status);
        updateById(exam);
    }

    private String getStatusName(int status) {
        return switch (status) {
            case 0 -> "草稿";
            case 1 -> "待考试";
            case 2 -> "考试中";
            case 3 -> "阅卷中";
            case 4 -> "已完成";
            case 5 -> "已发布";
            default -> "未知";
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        ExamPublishCheckVO checkVO = publishCheck(id);
        if (!Boolean.TRUE.equals(checkVO.getCanPublish())) {
            throw new BusinessException("考试配置未完成: " + String.join("；", checkVO.getMissingItems()));
        }
        if (exam.getStatus() != 4) {
            throw new BusinessException("只有已完成的考试才能发布");
        }
        exam.setStatus(5);
        updateById(exam);
    }

    @Override
    public ExamPublishCheckVO publishCheck(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        List<String> missingItems = new ArrayList<>();
        Long classCount = examClassMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.edumark.exam.entity.ExamClass>()
                        .eq(com.edumark.exam.entity.ExamClass::getExamId, id)
                        .eq(com.edumark.exam.entity.ExamClass::getDeleted, 0)
        );
        List<ExamSubject> subjects = examSubjectMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ExamSubject>()
                        .eq(ExamSubject::getExamId, id)
                        .eq(ExamSubject::getDeleted, 0)
        );

        int completedPaperCount = 0;
        int publishedTemplateCount = 0;
        int subjectWithQuestionCount = 0;

        if (classCount == null || classCount == 0) {
            missingItems.add("未配置参考班级");
        }
        if (subjects.isEmpty()) {
            missingItems.add("未配置考试科目");
        }

        for (ExamSubject subject : subjects) {
            Paper paper = paperMapper.selectByExamSubjectId(subject.getId());

            // 先检查答题卡模板（支持通过paperId或examId+subjectName查找）
            AnswerSheetTemplate template = null;
            if (paper != null) {
                template = answerSheetTemplateMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AnswerSheetTemplate>()
                                .eq(AnswerSheetTemplate::getPaperId, paper.getId())
                                .eq(AnswerSheetTemplate::getDeleted, 0)
                                .last("LIMIT 1")
                );
            }
            // 如果通过paperId找不到，尝试通过examId + subjectName查找
            if (template == null) {
                template = answerSheetTemplateMapper.selectOne(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AnswerSheetTemplate>()
                                .eq(AnswerSheetTemplate::getExamId, id)
                                .eq(AnswerSheetTemplate::getSubjectName, subject.getSubjectName())
                                .eq(AnswerSheetTemplate::getDeleted, 0)
                                .last("LIMIT 1")
                );
            }

            if (template != null && Objects.equals(template.getStatus(), 1)) {
                publishedTemplateCount++;
                // 有已发布的答题卡模板，试卷状态和题目不做强制检查
                if (paper != null && Objects.equals(paper.getStatus(), 1)) {
                    completedPaperCount++;
                }
                if (paper != null) {
                    Long questionCount = paperQuestionMapper.selectCount(
                            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaperQuestion>()
                                    .eq(PaperQuestion::getPaperId, paper.getId())
                                    .eq(PaperQuestion::getDeleted, 0)
                    );
                    if (questionCount != null && questionCount > 0) {
                        subjectWithQuestionCount++;
                    }
                }
            } else {
                // 没有已发布的答题卡模板，检查完整的配置链路
                if (template != null) {
                    missingItems.add(subject.getSubjectName() + "答题卡模板未发布");
                } else {
                    missingItems.add(subject.getSubjectName() + "未生成答题卡模板");
                }

                if (paper == null) {
                    missingItems.add(subject.getSubjectName() + "未创建试卷");
                    continue;
                }

                if (Objects.equals(paper.getStatus(), 1)) {
                    completedPaperCount++;
                } else {
                    missingItems.add(subject.getSubjectName() + "试卷未完成");
                }

                Long questionCount = paperQuestionMapper.selectCount(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaperQuestion>()
                                .eq(PaperQuestion::getPaperId, paper.getId())
                                .eq(PaperQuestion::getDeleted, 0)
                );
                if (questionCount != null && questionCount > 0) {
                    subjectWithQuestionCount++;
                } else {
                    missingItems.add(subject.getSubjectName() + "试卷未配置题目");
                }
            }
        }

        ExamPublishCheckVO vo = new ExamPublishCheckVO();
        vo.setExamId(id);
        vo.setCanPublish(missingItems.isEmpty());
        vo.setClassCount(classCount == null ? 0 : classCount.intValue());
        vo.setSubjectCount(subjects.size());
        vo.setCompletedPaperCount(completedPaperCount);
        vo.setPublishedTemplateCount(publishedTemplateCount);
        vo.setSubjectWithQuestionCount(subjectWithQuestionCount);
        vo.setMissingItems(missingItems);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublish(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (exam.getStatus() != 5) {
            throw new BusinessException("只有已发布的考试才能撤回");
        }
        exam.setStatus(4);
        updateById(exam);
    }

    private void validateExamDTO(ExamDTO dto) {
        if (dto.getSchoolId() == null) {
            throw new BusinessException("请选择所属学校");
        }
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException("请输入考试名称");
        }
        if (dto.getType() == null) {
            throw new BusinessException("请选择考试类型");
        }
        if (!StringUtils.hasText(dto.getAcademicYear())) {
            throw new BusinessException("请输入学年");
        }
        if (dto.getSemester() == null) {
            throw new BusinessException("请选择学期");
        }
        if (dto.getGradeId() == null) {
            throw new BusinessException("请选择年级");
        }
        if (dto.getClassIds() == null || dto.getClassIds().isEmpty()) {
            throw new BusinessException("请至少选择一个参考班级");
        }
        if (!StringUtils.hasText(dto.getStartTime()) || !StringUtils.hasText(dto.getEndTime())) {
            throw new BusinessException("请完整填写考试开始时间和结束时间");
        }

        LocalDateTime startTime = LocalDateTime.parse(dto.getStartTime(), DTF);
        LocalDateTime endTime = LocalDateTime.parse(dto.getEndTime(), DTF);
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("考试开始时间必须早于结束时间");
        }

        GradeVO grade = gradeMapper.selectVOById(dto.getGradeId());
        if (grade == null) {
            throw new BusinessException("所选年级不属于当前学校");
        }
        if (!Objects.equals(grade.getSchoolId(), dto.getSchoolId())) {
            throw new BusinessException("所选年级不属于当前学校");
        }

        List<Long> normalizedClassIds = dto.getClassIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (normalizedClassIds.isEmpty()) {
            throw new BusinessException("请至少选择一个参考班级");
        }

        for (Long classId : normalizedClassIds) {
            ClassInfoVO classInfo = classInfoMapper.selectVOById(classId);
            if (classInfo == null) {
                throw new BusinessException("存在无效的参考班级");
            }
            if (!Objects.equals(classInfo.getSchoolId(), dto.getSchoolId())) {
                throw new BusinessException("存在不属于当前学校的参考班级");
            }
            if (!Objects.equals(classInfo.getGradeId(), dto.getGradeId())) {
                throw new BusinessException("存在不属于当前年级的参考班级");
            }
        }

        dto.setClassIds(normalizedClassIds);
    }
}
