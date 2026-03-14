package com.edumark.exam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.dto.ExamDTO;
import com.edumark.exam.dto.ExamQueryDTO;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.entity.ExamClass;
import com.edumark.exam.mapper.ExamClassMapper;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.service.ExamService;
import com.edumark.exam.vo.ExamVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 考试服务实现
 *
 * @author EduMark
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Resource
    private ExamClassMapper examClassMapper;

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
        exam.setStatus(status);
        updateById(exam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (exam.getStatus() != 4) {
            throw new BusinessException("只有已完成的考试才能发布");
        }
        exam.setStatus(5);
        updateById(exam);
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
}
