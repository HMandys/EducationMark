package com.edumark.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.edumark.system.entity.SysRolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色权限关联Mapper
 *
 * @author EduMark
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 删除角色的所有权限关联
     *
     * @param roleId 角色ID
     * @return 删除数量
     */
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID统计引用数量
     *
     * @param permissionId 权限ID
     * @return 引用数量
     */
    @org.apache.ibatis.annotations.Select("SELECT COUNT(1) FROM sys_role_permission WHERE permission_id = #{permissionId}")
    long countByPermissionId(@Param("permissionId") Long permissionId);
}
