package com.edumark.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.GradeDTO;
import com.edumark.school.dto.GradeQueryDTO;
import com.edumark.school.entity.Grade;
import com.edumark.school.mapper.GradeMapper;
import com.edumark.school.service.GradeService;
import com.edumark.school.vo.GradeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 年级服务实现
 *
 * @author EduMark
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Override
    public PageResult<GradeVO> pageQuery(GradeQueryDTO query) {
        Page<GradeVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public GradeVO getDetail(Long id) {
        GradeVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("年级不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(GradeDTO dto) {
        // 检查同学校下年级编码是否重复
        if (StringUtils.hasText(dto.getCode())) {
            long count = lambdaQuery()
                    .eq(Grade::getSchoolId, dto.getSchoolId())
                    .eq(Grade::getCode, dto.getCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("该学校下年级编码已存在");
            }
        }

        Grade grade = new Grade();
        BeanUtils.copyProperties(dto, grade);
        if (grade.getSort() == null) {
            grade.setSort(dto.getGradeNum() != null ? dto.getGradeNum() : 0);
        }
        if (grade.getStatus() == null) {
            grade.setStatus(1);
        }
        save(grade);
        return grade.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(GradeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("年级ID不能为空");
        }
        Grade existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("年级不存在");
        }

        // 检查年级编码是否重复
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existing.getCode())) {
            long count = lambdaQuery()
                    .eq(Grade::getSchoolId, dto.getSchoolId())
                    .eq(Grade::getCode, dto.getCode())
                    .ne(Grade::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("该学校下年级编码已存在");
            }
        }

        Grade grade = new Grade();
        BeanUtils.copyProperties(dto, grade);
        if (grade.getSort() == null && dto.getGradeNum() != null) {
            grade.setSort(dto.getGradeNum());
        }
        updateById(grade);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Grade grade = getById(id);
        if (grade == null) {
            throw new BusinessException("年级不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的年级");
        }
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Grade grade = getById(id);
        if (grade == null) {
            throw new BusinessException("年级不存在");
        }
        grade.setStatus(status);
        updateById(grade);
    }

    @Override
    public List<GradeVO> listBySchoolId(Long schoolId) {
        return baseMapper.selectListBySchoolId(schoolId, 1);
    }
}
