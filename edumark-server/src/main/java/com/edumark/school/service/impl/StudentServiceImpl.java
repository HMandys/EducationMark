package com.edumark.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.StudentDTO;
import com.edumark.school.dto.StudentQueryDTO;
import com.edumark.school.entity.Student;
import com.edumark.school.mapper.StudentMapper;
import com.edumark.school.service.StudentService;
import com.edumark.school.vo.StudentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * 学生服务实现
 *
 * @author EduMark
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Override
    public PageResult<StudentVO> pageQuery(StudentQueryDTO query) {
        Page<StudentVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public StudentVO getDetail(Long id) {
        StudentVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("学生不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StudentDTO dto) {
        // 检查同学校下学号是否重复
        if (StringUtils.hasText(dto.getStudentNumber())) {
            long count = lambdaQuery()
                    .eq(Student::getSchoolId, dto.getSchoolId())
                    .eq(Student::getStudentNumber, dto.getStudentNumber())
                    .count();
            if (count > 0) {
                throw new BusinessException("该学校下学号已存在");
            }
        }

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        if (student.getStatus() == null) {
            student.setStatus(1);
        }
        // 生成绑定码
        student.setBindCode(generateBindCode());
        save(student);

        // TODO: 如果需要创建用户账号，调用用户服务创建

        return student.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StudentDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("学生ID不能为空");
        }
        Student existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("学生不存在");
        }

        // 检查学号是否重复
        if (StringUtils.hasText(dto.getStudentNumber()) && !dto.getStudentNumber().equals(existing.getStudentNumber())) {
            long count = lambdaQuery()
                    .eq(Student::getSchoolId, dto.getSchoolId())
                    .eq(Student::getStudentNumber, dto.getStudentNumber())
                    .ne(Student::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("该学校下学号已存在");
            }
        }

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);
        updateById(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的学生");
        }
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        student.setStatus(status);
        updateById(student);
    }

    @Override
    public List<StudentVO> listByClassId(Long classId) {
        return baseMapper.selectListByClassId(classId, 1);
    }

    @Override
    public Student getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String refreshBindCode(Long id) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        String newBindCode = generateBindCode();
        student.setBindCode(newBindCode);
        updateById(student);
        return newBindCode;
    }

    /**
     * 生成6位绑定码
     */
    private String generateBindCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
    }
}
