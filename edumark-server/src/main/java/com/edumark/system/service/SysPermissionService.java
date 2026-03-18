package com.edumark.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysPermissionDTO;
import com.edumark.system.dto.SysPermissionQueryDTO;
import com.edumark.system.entity.SysPermission;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author EduMark
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 分页查询权限
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<SysPermission> getPermissionPage(SysPermissionQueryDTO query);

    /**
     * 获取权限详情
     *
     * @param id 权限ID
     * @return 权限详情
     */
    SysPermission getPermissionById(Long id);

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    List<SysPermission> getPermissionTree();

    /**
     * 创建权限
     *
     * @param dto 请求参数
     * @return 权限ID
     */
    Long createPermission(SysPermissionDTO dto);

    /**
     * 更新权限
     *
     * @param dto 请求参数
     */
    void updatePermission(SysPermissionDTO dto);

    /**
     * 删除权限
     *
     * @param id 权限ID
     */
    void deletePermission(Long id);

    /**
     * 更新权限状态
     *
     * @param id 权限ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
}
