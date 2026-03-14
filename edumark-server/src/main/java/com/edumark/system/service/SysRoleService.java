package com.edumark.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.system.entity.SysRole;
import com.edumark.system.vo.SysRoleVO;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author EduMark
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<SysRoleVO> getAllRoles();

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);

    /**
     * 根据角色编码获取角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    SysRole getByRoleCode(String roleCode);
}
