package com.edumark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.enums.ResultCode;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysRoleDTO;
import com.edumark.system.dto.SysRoleQueryDTO;
import com.edumark.system.entity.SysRolePermission;
import com.edumark.system.entity.SysRole;
import com.edumark.system.mapper.SysPermissionMapper;
import com.edumark.system.mapper.SysRoleMapper;
import com.edumark.system.mapper.SysRolePermissionMapper;
import com.edumark.system.service.SysRoleService;
import com.edumark.system.vo.SysRoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现
 *
 * @author EduMark
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    public SysRoleServiceImpl(SysPermissionMapper permissionMapper, SysRolePermissionMapper rolePermissionMapper) {
        this.permissionMapper = permissionMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public PageResult<SysRoleVO> getRolePage(SysRoleQueryDTO query) {
        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getRoleName() != null && !query.getRoleName().isBlank(), SysRole::getRoleName, query.getRoleName())
                .like(query.getRoleCode() != null && !query.getRoleCode().isBlank(), SysRole::getRoleCode, query.getRoleCode())
                .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
                .eq(query.getDataScope() != null, SysRole::getDataScope, query.getDataScope())
                .orderByAsc(SysRole::getSort)
                .orderByDesc(SysRole::getCreateTime);
        IPage<SysRole> resultPage = page(page, wrapper);

        List<SysRoleVO> voList = new ArrayList<>();
        for (SysRole role : resultPage.getRecords()) {
            voList.add(toRoleVO(role, true));
        }

        return new PageResult<>(voList, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
    }

    @Override
    public List<SysRoleVO> getAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSort);
        List<SysRole> roles = list(wrapper);

        List<SysRoleVO> voList = new ArrayList<>();
        for (SysRole role : roles) {
            voList.add(toRoleVO(role, true));
        }
        return voList;
    }

    @Override
    public SysRoleVO getRoleById(Long roleId) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }
        return toRoleVO(role, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(SysRoleDTO dto) {
        SysRole existRole = getByRoleCode(dto.getRoleCode());
        if (existRole != null) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleCode(dto.getRoleCode());
        role.setDescription(dto.getDescription());
        role.setSort(dto.getSort() == null ? 0 : dto.getSort());
        role.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        role.setDataScope(dto.getDataScope() == null ? 2 : dto.getDataScope());
        save(role);

        saveRolePermissions(role.getId(), dto.getPermissionIds());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRoleDTO dto) {
        SysRole role = getById(dto.getId());
        if (role == null) {
            throw new BusinessException(ResultCode.ROLE_NOT_FOUND);
        }

        SysRole existRole = getByRoleCode(dto.getRoleCode());
        if (existRole != null && !existRole.getId().equals(dto.getId())) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole updateRole = new SysRole();
        updateRole.setId(dto.getId());
        updateRole.setRoleName(dto.getRoleName());
        updateRole.setRoleCode(dto.getRoleCode());
        updateRole.setDescription(dto.getDescription());
        updateRole.setSort(dto.getSort());
        updateRole.setStatus(dto.getStatus());
        updateRole.setDataScope(dto.getDataScope());
        updateById(updateRole);

        rolePermissionMapper.deleteByRoleId(dto.getId());
        saveRolePermissions(dto.getId(), dto.getPermissionIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        removeById(roleId);
        rolePermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    public void updateStatus(Long roleId, Integer status) {
        SysRole role = new SysRole();
        role.setId(roleId);
        role.setStatus(status);
        updateById(role);
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public SysRole getByRoleCode(String roleCode) {
        return baseMapper.selectByRoleCode(roleCode);
    }

    private SysRoleVO toRoleVO(SysRole role, boolean includePermissions) {
        SysRoleVO vo = new SysRoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleCode(role.getRoleCode());
        vo.setDescription(role.getDescription());
        vo.setSort(role.getSort());
        vo.setStatus(role.getStatus());
        vo.setDataScope(role.getDataScope());
        vo.setCreateTime(role.getCreateTime());
        if (includePermissions) {
            vo.setPermissionIds(permissionMapper.selectPermissionIdsByRoleId(role.getId()));
        }
        return vo;
    }

    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (Long permissionId : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        }
    }
}
