package com.edumark.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.school.dto.SchoolDTO;
import com.edumark.school.dto.SchoolQueryDTO;
import com.edumark.school.entity.School;
import com.edumark.school.mapper.SchoolMapper;
import com.edumark.school.service.SchoolService;
import com.edumark.school.vo.SchoolVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 学校服务实现
 *
 * @author EduMark
 */
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    @Override
    public PageResult<SchoolVO> pageQuery(SchoolQueryDTO query) {
        Page<SchoolVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public SchoolVO getDetail(Long id) {
        SchoolVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("学校不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SchoolDTO dto) {
        // 检查学校编码是否重复
        if (StringUtils.hasText(dto.getCode())) {
            long count = lambdaQuery().eq(School::getCode, dto.getCode()).count();
            if (count > 0) {
                throw new BusinessException("学校编码已存在");
            }
        }

        School school = new School();
        BeanUtils.copyProperties(dto, school);
        if (!StringUtils.hasText(school.getContactPhone()) && StringUtils.hasText(dto.getPhone())) {
            school.setContactPhone(dto.getPhone());
        }
        if (school.getStatus() == null) {
            school.setStatus(1);
        }
        save(school);
        return school.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SchoolDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("学校ID不能为空");
        }
        School existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("学校不存在");
        }

        // 检查学校编码是否重复
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existing.getCode())) {
            long count = lambdaQuery().eq(School::getCode, dto.getCode()).ne(School::getId, dto.getId()).count();
            if (count > 0) {
                throw new BusinessException("学校编码已存在");
            }
        }

        School school = new School();
        BeanUtils.copyProperties(dto, school);
        if (!StringUtils.hasText(school.getContactPhone()) && StringUtils.hasText(dto.getPhone())) {
            school.setContactPhone(dto.getPhone());
        }
        updateById(school);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        School school = getById(id);
        if (school == null) {
            throw new BusinessException("学校不存在");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的学校");
        }
        removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        School school = getById(id);
        if (school == null) {
            throw new BusinessException("学校不存在");
        }
        school.setStatus(status);
        updateById(school);
    }

    @Override
    public List<SchoolVO> listForSelect() {
        return baseMapper.selectListForSelect(1);
    }
}
