package com.edumark.exam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.exam.dto.ExamSubjectDTO;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.entity.Paper;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.exam.mapper.PaperMapper;
import com.edumark.exam.service.ExamSubjectService;
import com.edumark.exam.vo.ExamSubjectVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 考试科目服务实现
 *
 * @author EduMark
 */
@Service
public class ExamSubjectServiceImpl extends ServiceImpl<ExamSubjectMapper, ExamSubject> implements ExamSubjectService {

    @Resource
    private PaperMapper paperMapper;

    @Resource
    private ExamMapper examMapper;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<ExamSubjectVO> listByExamId(Long examId) {
        List<ExamSubjectVO> list = baseMapper.selectListByExamId(examId);
        // 查询每个科目的试卷ID
        for (ExamSubjectVO vo : list) {
            Paper paper = paperMapper.selectByExamSubjectId(vo.getId());
            if (paper != null) {
                vo.setPaperId(paper.getId());
            }
        }
        return list;
    }

    @Override
    public ExamSubjectVO getDetail(Long id) {
        ExamSubjectVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("科目不存在");
        }
        Paper paper = paperMapper.selectByExamSubjectId(id);
        if (paper != null) {
            vo.setPaperId(paper.getId());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ExamSubjectDTO dto) {
        // 检查同一考试下科目编码是否重复
        if (StringUtils.hasText(dto.getSubjectCode())) {
            long count = lambdaQuery()
                    .eq(ExamSubject::getExamId, dto.getExamId())
                    .eq(ExamSubject::getSubjectCode, dto.getSubjectCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("科目编码已存在");
            }
        }

        ExamSubject subject = new ExamSubject();
        BeanUtils.copyProperties(dto, subject);
        if (StringUtils.hasText(dto.getStartTime())) {
            subject.setStartTime(LocalDateTime.parse(dto.getStartTime(), DTF));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            subject.setEndTime(LocalDateTime.parse(dto.getEndTime(), DTF));
        }
        if (subject.getStatus() == null) {
            subject.setStatus(1);
        }
        if (subject.getSort() == null) {
            subject.setSort(0);
        }
        save(subject);

        // 自动创建试卷
        createDefaultPaper(subject);
        refreshExamSummary(subject.getExamId());

        return subject.getId();
    }

    private void createDefaultPaper(ExamSubject subject) {
        Paper paper = new Paper();
        paper.setExamSubjectId(subject.getId());
        paper.setName(subject.getSubjectName() + "试卷");
        paper.setType(1); // A卷
        paper.setTotalScore(subject.getFullScore());
        paper.setQuestionCount(0);
        paper.setObjectiveCount(0);
        paper.setSubjectiveCount(0);
        paper.setStatus(0); // 草稿
        paperMapper.insert(paper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ExamSubjectDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("科目ID不能为空");
        }
        ExamSubject existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("科目不存在");
        }

        // 检查科目编码是否重复
        if (StringUtils.hasText(dto.getSubjectCode()) && !dto.getSubjectCode().equals(existing.getSubjectCode())) {
            long count = lambdaQuery()
                    .eq(ExamSubject::getExamId, dto.getExamId())
                    .eq(ExamSubject::getSubjectCode, dto.getSubjectCode())
                    .ne(ExamSubject::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("科目编码已存在");
            }
        }

        ExamSubject subject = new ExamSubject();
        BeanUtils.copyProperties(dto, subject);
        if (StringUtils.hasText(dto.getStartTime())) {
            subject.setStartTime(LocalDateTime.parse(dto.getStartTime(), DTF));
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            subject.setEndTime(LocalDateTime.parse(dto.getEndTime(), DTF));
        }
        updateById(subject);
        refreshExamSummary(subject.getExamId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ExamSubject subject = getById(id);
        if (subject == null) {
            throw new BusinessException("科目不存在");
        }
        removeById(id);
        refreshExamSummary(subject.getExamId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreate(Long examId, List<ExamSubjectDTO> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return;
        }
        int sort = 1;
        for (ExamSubjectDTO dto : subjects) {
            dto.setExamId(examId);
            dto.setSort(sort++);
            create(dto);
        }
    }

    private void refreshExamSummary(Long examId) {
        if (examId == null) {
            return;
        }
        Integer totalScore = lambdaQuery()
                .eq(ExamSubject::getExamId, examId)
                .eq(ExamSubject::getDeleted, 0)
                .list()
                .stream()
                .map(ExamSubject::getFullScore)
                .filter(java.util.Objects::nonNull)
                .reduce(0, Integer::sum);

        Exam exam = new Exam();
        exam.setId(examId);
        exam.setTotalScore(totalScore);
        examMapper.updateById(exam);
    }
}
