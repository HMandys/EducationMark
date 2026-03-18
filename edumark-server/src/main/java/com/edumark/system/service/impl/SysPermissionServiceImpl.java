package com.edumark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysPermissionDTO;
import com.edumark.system.dto.SysPermissionQueryDTO;
import com.edumark.system.entity.SysPermission;
import com.edumark.system.mapper.SysPermissionMapper;
import com.edumark.system.mapper.SysRolePermissionMapper;
import com.edumark.system.service.SysPermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限服务实现
 *
 * @author EduMark
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysRolePermissionMapper rolePermissionMapper;

    public SysPermissionServiceImpl(SysRolePermissionMapper rolePermissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public PageResult<SysPermission> getPermissionPage(SysPermissionQueryDTO query) {
        Page<SysPermission> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPermissionName()), SysPermission::getPermissionName, query.getPermissionName())
                .like(StringUtils.hasText(query.getPermissionCode()), SysPermission::getPermissionCode, query.getPermissionCode())
                .eq(query.getPermissionType() != null, SysPermission::getPermissionType, query.getPermissionType())
                .eq(query.getStatus() != null, SysPermission::getStatus, query.getStatus())
                .eq(query.getVisible() != null, SysPermission::getVisible, query.getVisible())
                .eq(query.getParentId() != null, SysPermission::getParentId, query.getParentId())
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId);
        page(page, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public SysPermission getPermissionById(Long id) {
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    @Override
    public List<SysPermission> getPermissionTree() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId);
        List<SysPermission> permissions = list(wrapper);
        return buildTree(permissions, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(SysPermissionDTO dto) {
        validatePermissionCode(dto.getPermissionCode(), null);
        validateParent(dto.getParentId(), dto.getId());

        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(dto, permission);
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        if (permission.getSort() == null) {
            permission.setSort(0);
        }
        if (permission.getVisible() == null) {
            permission.setVisible(1);
        }
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        save(permission);
        return permission.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(SysPermissionDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("权限ID不能为空");
        }
        SysPermission existing = getById(dto.getId());
        if (existing == null) {
            throw new BusinessException("权限不存在");
        }

        validatePermissionCode(dto.getPermissionCode(), dto.getId());
        validateParent(dto.getParentId(), dto.getId());

        SysPermission permission = new SysPermission();
        BeanUtils.copyProperties(dto, permission);
        if (permission.getParentId() == null) {
            permission.setParentId(0L);
        }
        updateById(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        long childCount = lambdaQuery().eq(SysPermission::getParentId, id).count();
        if (childCount > 0) {
            throw new BusinessException("存在子权限，不能删除");
        }
        long usedCount = rolePermissionMapper.countByPermissionId(id);
        if (usedCount > 0) {
            throw new BusinessException("权限已分配给角色，不能删除");
        }
        removeById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        SysPermission updatePermission = new SysPermission();
        updatePermission.setId(id);
        updatePermission.setStatus(status);
        updateById(updatePermission);
    }

    private List<SysPermission> buildTree(List<SysPermission> permissions, Long parentId) {
        List<SysPermission> result = new ArrayList<>();
        for (SysPermission permission : permissions) {
            Long currentParentId = permission.getParentId() == null ? 0L : permission.getParentId();
            if (!currentParentId.equals(parentId)) {
                continue;
            }
            permission.setChildren(buildTree(permissions, permission.getId()));
            result.add(permission);
        }
        return result;
    }

    private void validatePermissionCode(String permissionCode, Long excludeId) {
        if (!StringUtils.hasText(permissionCode)) {
            return;
        }
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermissionCode, permissionCode);
        if (excludeId != null) {
            wrapper.ne(SysPermission::getId, excludeId);
        }
        long count = count(wrapper);
        if (count > 0) {
            throw new BusinessException("权限编码已存在");
        }
    }

    private void validateParent(Long parentId, Long currentId) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        if (currentId != null && parentId.equals(currentId)) {
            throw new BusinessException("父级权限不能选择自身");
        }
        SysPermission parent = getById(parentId);
        if (parent == null) {
            throw new BusinessException("父级权限不存在");
        }
    }
}
