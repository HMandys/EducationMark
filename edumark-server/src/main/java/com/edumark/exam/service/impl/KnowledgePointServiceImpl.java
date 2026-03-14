package com.edumark.exam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.dto.KnowledgePointDTO;
import com.edumark.exam.dto.KnowledgePointQueryDTO;
import com.edumark.exam.entity.KnowledgePoint;
import com.edumark.exam.mapper.KnowledgePointMapper;
import com.edumark.exam.service.KnowledgePointService;
import com.edumark.exam.vo.KnowledgePointVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识点服务实现
 *
 * @author EduMark
 */
@Service
public class KnowledgePointServiceImpl extends ServiceImpl<KnowledgePointMapper, KnowledgePoint> implements KnowledgePointService {

    @Override
    public PageResult<KnowledgePointVO> pageQuery(KnowledgePointQueryDTO query) {
        Page<KnowledgePointVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        baseMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public KnowledgePointVO getDetail(Long id) {
        KnowledgePointVO vo = baseMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("知识点不存在");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(KnowledgePointDTO dto) {
        // 检查编码是否重复
        if (StringUtils.hasText(dto.getCode())) {
            long count = lambdaQuery()
                    .eq(KnowledgePoint::getSchoolId, dto.getSchoolId())
                    .eq(KnowledgePoint::getSubjectName, dto.getSubjectName())
                    .eq(KnowledgePoint::getCode, dto.getCode())
                    .count();
            if (count > 0) {
                throw new BusinessException("知识点编码已存在");
            }
        }

        KnowledgePoint kp = new KnowledgePoint();
        BeanUtils.copyProperties(dto, kp);

        // 计算层级和路径
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            KnowledgePoint parent = getById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父级知识点不存在");
            }
            kp.setLevel(parent.getLevel() + 1);
            kp.setPath(parent.getPath() + "/" + dto.getName());
        } else {
            kp.setParentId(0L);
            kp.setLevel(1);
            kp.setPath(dto.getName());
        }

        if (kp.getStatus() == null) {
            kp.setStatus(1);
        }
        if (kp.getSort() == null) {
            kp.setSort(0);
        }
        save(kp);
        return kp.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(KnowledgePointDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("知识点ID不能为空");
        }
        KnowledgePoint existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("知识点不存在");
        }

        // 检查编码是否重复
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existing.getCode())) {
            long count = lambdaQuery()
                    .eq(KnowledgePoint::getSchoolId, dto.getSchoolId())
                    .eq(KnowledgePoint::getSubjectName, dto.getSubjectName())
                    .eq(KnowledgePoint::getCode, dto.getCode())
                    .ne(KnowledgePoint::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("知识点编码已存在");
            }
        }

        KnowledgePoint kp = new KnowledgePoint();
        BeanUtils.copyProperties(dto, kp);
        updateById(kp);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        KnowledgePoint kp = getById(id);
        if (kp == null) {
            throw new BusinessException("知识点不存在");
        }
        // 检查是否有子知识点
        long count = lambdaQuery().eq(KnowledgePoint::getParentId, id).count();
        if (count > 0) {
            throw new BusinessException("存在子知识点，不能删除");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的知识点");
        }
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public List<KnowledgePointVO> getTree(Long schoolId, String subjectName) {
        List<KnowledgePointVO> all = baseMapper.selectTree(schoolId, subjectName);
        return buildTree(all);
    }

    @Override
    public List<KnowledgePointVO> listBySubject(Long schoolId, String subjectName) {
        return baseMapper.selectTree(schoolId, subjectName);
    }

    private List<KnowledgePointVO> buildTree(List<KnowledgePointVO> all) {
        Map<Long, List<KnowledgePointVO>> parentMap = all.stream()
                .collect(Collectors.groupingBy(KnowledgePointVO::getParentId));

        List<KnowledgePointVO> roots = parentMap.getOrDefault(0L, new ArrayList<>());
        for (KnowledgePointVO root : roots) {
            buildChildren(root, parentMap);
        }
        return roots;
    }

    private void buildChildren(KnowledgePointVO parent, Map<Long, List<KnowledgePointVO>> parentMap) {
        List<KnowledgePointVO> children = parentMap.get(parent.getId());
        if (children != null) {
            parent.setChildren(children);
            for (KnowledgePointVO child : children) {
                buildChildren(child, parentMap);
            }
        }
    }
}
