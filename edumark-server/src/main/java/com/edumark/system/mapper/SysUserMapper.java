package com.edumark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.system.dto.SysUserQueryDTO;
import com.edumark.system.entity.SysUser;
import com.edumark.system.vo.SysUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author EduMark
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（包含角色信息）
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 分页查询用户列表
     *
     * @param page  分页参数
     * @param query 查询条件
     * @return 用户分页列表
     */
    IPage<SysUserVO> selectUserPage(Page<SysUserVO> page, @Param("query") SysUserQueryDTO query);

    /**
     * 根据用户ID查询用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    SysUserVO selectUserById(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限编码列表
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);
}
