package com.edumark.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edumark.common.result.PageResult;
import com.edumark.system.dto.SysUserDTO;
import com.edumark.system.dto.SysUserQueryDTO;
import com.edumark.system.entity.SysUser;
import com.edumark.system.vo.SysUserVO;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author EduMark
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);

    /**
     * 分页查询用户列表
     *
     * @param query 查询条件
     * @return 用户分页列表
     */
    PageResult<SysUserVO> getUserPage(SysUserQueryDTO query);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    SysUserVO getUserById(Long userId);

    /**
     * 创建用户
     *
     * @param dto 用户信息
     * @return 用户ID
     */
    Long createUser(SysUserDTO dto);

    /**
     * 更新用户
     *
     * @param dto 用户信息
     */
    void updateUser(SysUserDTO dto);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 批量删除用户
     *
     * @param userIds 用户ID列表
     */
    void deleteUsers(List<Long> userIds);

    /**
     * 重置密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 启用/禁用用户
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateStatus(Long userId, Integer status);

    /**
     * 获取用户权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getPermissionCodes(Long userId);
}
