package com.edumark.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysRoleDTO;
import com.edumark.system.dto.SysRoleQueryDTO;
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
     * 分页查询角色
     *
     * @param query 查询参数
     * @return 角色分页数据
     */
    PageResult<SysRoleVO> getRolePage(SysRoleQueryDTO query);

    /**
     * 获取所有角色列表
     *
     * @return 角色列表
     */
    List<SysRoleVO> getAllRoles();

    /**
     * 获取角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    SysRoleVO getRoleById(Long roleId);

    /**
     * 创建角色
     *
     * @param dto 角色信息
     * @return 角色ID
     */
    Long createRole(SysRoleDTO dto);

    /**
     * 更新角色
     *
     * @param dto 角色信息
     */
    void updateRole(SysRoleDTO dto);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 状态
     */
    void updateStatus(Long roleId, Integer status);

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
