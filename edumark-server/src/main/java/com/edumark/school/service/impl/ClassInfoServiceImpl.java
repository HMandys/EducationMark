package com.edumark.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.ClassInfoDTO;
import com.edumark.school.dto.ClassInfoQueryDTO;
import com.edumark.school.entity.ClassInfo;
import com.edumark.school.mapper.ClassInfoMapper;
import com.edumark.school.service.ClassInfoService;
import com.edumark.school.vo.ClassInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 班级服务实现
 *
 * @author EduMark
 */
@Service
public class ClassInfoServiceImpl extends ServiceImpl<ClassInfoMapper, ClassInfo> implements ClassInfoService {

    @Override
    public PageResult<ClassInfoVO> pageQuery(ClassInfoQueryDTO query) {
        Page<ClassInfoVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ClassInfoVO getDetail(Long id) {
        ClassInfoVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("班级不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ClassInfoDTO dto) {
        // 检查同年级下班级编码是否重复
        if (StringUtils.hasText(dto.getCode())) {
            long count = lambdaQuery()
                    .eq(ClassInfo::getGradeId, dto.getGradeId())
                    .eq(ClassInfo::getCode, dto.getCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("该年级下班级编码已存在");
            }
        }

        ClassInfo classInfo = new ClassInfo();
        BeanUtils.copyProperties(dto, classInfo);
        if (classInfo.getStatus() == null) {
            classInfo.setStatus(1);
        }
        if (classInfo.getSort() == null) {
            classInfo.setSort(0);
        }
        save(classInfo);
        return classInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClassInfoDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("班级ID不能为空");
        }
        ClassInfo existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("班级不存在");
        }

        // 检查班级编码是否重复
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existing.getCode())) {
            long count = lambdaQuery()
                    .eq(ClassInfo::getGradeId, dto.getGradeId())
                    .eq(ClassInfo::getCode, dto.getCode())
                    .ne(ClassInfo::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("该年级下班级编码已存在");
            }
        }

        ClassInfo classInfo = new ClassInfo();
        BeanUtils.copyProperties(dto, classInfo);
        updateById(classInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ClassInfo classInfo = getById(id);
        if (classInfo == null) {
            throw new BusinessException("班级不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的班级");
        }
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        ClassInfo classInfo = getById(id);
        if (classInfo == null) {
            throw new BusinessException("班级不存在");
        }
        classInfo.setStatus(status);
        updateById(classInfo);
    }

    @Override
    public List<ClassInfoVO> listByGradeId(Long gradeId) {
        return baseMapper.selectListByGradeId(gradeId, 1);
    }

    @Override
    public List<ClassInfoVO> listBySchoolId(Long schoolId) {
        return baseMapper.selectListBySchoolId(schoolId, 1);
    }
}
