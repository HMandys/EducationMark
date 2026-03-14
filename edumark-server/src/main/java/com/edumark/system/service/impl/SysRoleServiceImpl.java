package com.edumark.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.system.entity.SysRole;
import com.edumark.system.mapper.SysRoleMapper;
import com.edumark.system.service.SysRoleService;
import com.edumark.system.vo.SysRoleVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现
 *
 * @author EduMark
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Override
    public List<SysRoleVO> getAllRoles() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSort);
        List<SysRole> roles = list(wrapper);

        List<SysRoleVO> voList = new ArrayList<>();
        for (SysRole role : roles) {
            SysRoleVO vo = new SysRoleVO();
            vo.setId(role.getId());
            vo.setRoleName(role.getRoleName());
            vo.setRoleCode(role.getRoleCode());
            vo.setDescription(role.getDescription());
            vo.setSort(role.getSort());
            vo.setStatus(role.getStatus());
            vo.setDataScope(role.getDataScope());
            vo.setCreateTime(role.getCreateTime());
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public SysRole getByRoleCode(String roleCode) {
        return baseMapper.selectByRoleCode(roleCode);
    }
}
