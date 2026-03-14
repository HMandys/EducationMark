package com.edumark.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.TeacherDTO;
import com.edumark.school.dto.TeacherQueryDTO;
import com.edumark.school.entity.Teacher;
import com.edumark.school.mapper.TeacherMapper;
import com.edumark.school.service.TeacherService;
import com.edumark.school.vo.TeacherVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 教师服务实现
 *
 * @author EduMark
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public PageResult<TeacherVO> pageQuery(TeacherQueryDTO query) {
        Page<TeacherVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public TeacherVO getDetail(Long id) {
        TeacherVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("教师不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TeacherDTO dto) {
        // 检查同学校下工号是否重复
        if (StringUtils.hasText(dto.getJobNumber())) {
            long count = lambdaQuery()
                    .eq(Teacher::getSchoolId, dto.getSchoolId())
                    .eq(Teacher::getJobNumber, dto.getJobNumber())
                    .count();
            if (count > 0) {
                throw new BusinessException("该学校下教师工号已存在");
            }
        }

        // 检查手机号是否重复
        if (StringUtils.hasText(dto.getPhone())) {
            long count = lambdaQuery()
                    .eq(Teacher::getPhone, dto.getPhone())
                    .count();
            if (count > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(dto, teacher);
        if (teacher.getStatus() == null) {
            teacher.setStatus(1);
        }
        save(teacher);

        // TODO: 如果需要创建用户账号，调用用户服务创建

        return teacher.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TeacherDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("教师ID不能为空");
        }
        Teacher existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("教师不存在");
        }

        // 检查工号是否重复
        if (StringUtils.hasText(dto.getJobNumber()) && !dto.getJobNumber().equals(existing.getJobNumber())) {
            long count = lambdaQuery()
                    .eq(Teacher::getSchoolId, dto.getSchoolId())
                    .eq(Teacher::getJobNumber, dto.getJobNumber())
                    .ne(Teacher::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("该学校下教师工号已存在");
            }
        }

        // 检查手机号是否重复
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(existing.getPhone())) {
            long count = lambdaQuery()
                    .eq(Teacher::getPhone, dto.getPhone())
                    .ne(Teacher::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(dto, teacher);
        updateById(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Teacher teacher = getById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的教师");
        }
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Teacher teacher = getById(id);
        if (teacher == null) {
            throw new BusinessException("教师不存在");
        }
        teacher.setStatus(status);
        updateById(teacher);
    }

    @Override
    public List<TeacherVO> listBySchoolId(Long schoolId) {
        return baseMapper.selectListBySchoolId(schoolId, 1);
    }

    @Override
    public Teacher getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
}
