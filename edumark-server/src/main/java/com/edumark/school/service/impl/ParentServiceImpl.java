package com.edumark.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.ParentBindDTO;
import com.edumark.school.dto.ParentDTO;
import com.edumark.school.dto.ParentQueryDTO;
import com.edumark.school.entity.Parent;
import com.edumark.school.entity.ParentStudentBind;
import com.edumark.school.entity.Student;
import com.edumark.school.mapper.ParentMapper;
import com.edumark.school.mapper.ParentStudentBindMapper;
import com.edumark.school.mapper.StudentMapper;
import com.edumark.school.service.ParentService;
import com.edumark.school.vo.ParentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 家长服务实现
 *
 * @author EduMark
 */
@Service
public class ParentServiceImpl extends ServiceImpl<ParentMapper, Parent> implements ParentService {

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private ParentStudentBindMapper parentStudentBindMapper;

    @Override
    public PageResult<ParentVO> pageQuery(ParentQueryDTO query) {
        Page<ParentVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        // 查询绑定的学生
        for (ParentVO vo : page.getRecords()) {
            List<ParentVO.StudentBindVO> students = parentStudentBindMapper.selectStudentsByParentId(vo.getId());
            vo.setStudents(students);
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ParentVO getDetail(Long id) {
        ParentVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("家长不存在");
        }
        // 查询绑定的学生
        List<ParentVO.StudentBindVO> students = parentStudentBindMapper.selectStudentsByParentId(id);
        vo.setStudents(students);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ParentDTO dto) {
        // 检查手机号是否重复
        if (StringUtils.hasText(dto.getPhone())) {
            Parent existing = baseMapper.selectByPhone(dto.getPhone());
            if (existing != null) {
                throw new BusinessException("手机号已存在");
            }
        }

        Parent parent = new Parent();
        BeanUtils.copyProperties(dto, parent);
        if (parent.getStatus() == null) {
            parent.setStatus(1);
        }
        save(parent);
        return parent.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ParentDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("家长ID不能为空");
        }
        Parent existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("家长不存在");
        }

        // 检查手机号是否重复
        if (StringUtils.hasText(dto.getPhone()) && !dto.getPhone().equals(existing.getPhone())) {
            Parent byPhone = baseMapper.selectByPhone(dto.getPhone());
            if (byPhone != null && !byPhone.getId().equals(dto.getId())) {
                throw new BusinessException("手机号已存在");
            }
        }

        Parent parent = new Parent();
        BeanUtils.copyProperties(dto, parent);
        updateById(parent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Parent parent = getById(id);
        if (parent == null) {
            throw new BusinessException("家长不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的家长");
        }
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Parent parent = getById(id);
        if (parent == null) {
            throw new BusinessException("家长不存在");
        }
        parent.setStatus(status);
        updateById(parent);
    }

    @Override
    public Parent getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindStudent(Long parentId, ParentBindDTO dto) {
        // 验证家长存在
        Parent parent = getById(parentId);
        if (parent == null) {
            throw new BusinessException("家长不存在");
        }

        // 根据学号和姓名查找学生
        Student student = studentMapper.selectByNumberAndName(dto.getStudentNumber(), dto.getStudentName());
        if (student == null) {
            throw new BusinessException("未找到匹配的学生信息");
        }

        // 验证绑定码
        if (!dto.getBindCode().equalsIgnoreCase(student.getBindCode())) {
            throw new BusinessException("绑定码错误");
        }

        // 检查是否已绑定
        ParentStudentBind existingBind = parentStudentBindMapper.selectByParentAndStudent(parentId, student.getId());
        if (existingBind != null) {
            throw new BusinessException("已绑定该学生");
        }

        // 创建绑定关系
        ParentStudentBind bind = new ParentStudentBind();
        bind.setParentId(parentId);
        bind.setStudentId(student.getId());
        bind.setRelation(dto.getRelation() != null ? dto.getRelation() : 9);
        bind.setIsPrimary(0);
        parentStudentBindMapper.insert(bind);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindStudent(Long parentId, Long studentId) {
        ParentStudentBind bind = parentStudentBindMapper.selectByParentAndStudent(parentId, studentId);
        if (bind == null) {
            throw new BusinessException("未找到绑定关系");
        }
        parentStudentBindMapper.deleteById(bind.getId());
    }

    @Override
    public List<ParentVO.StudentBindVO> getBoundStudents(Long parentId) {
        return parentStudentBindMapper.selectStudentsByParentId(parentId);
    }
}
