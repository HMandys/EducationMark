package com.edumark.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 系统权限（菜单）实体
 *
 * @author EduMark
 */
@TableName("sys_permission")
@Schema(description = "系统权限")
public class SysPermission extends BaseEntity {

    /**
     * 父级ID（0表示一级菜单）
     */
    @Schema(description = "父级ID")
    private Long parentId;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String permissionName;

    /**
     * 权限编码
     */
    @Schema(description = "权限编码")
    private String permissionCode;

    /**
     * 权限类型（1-目录，2-菜单，3-按钮）
     */
    @Schema(description = "权限类型")
    private Integer permissionType;

    /**
     * 路由路径
     */
    @Schema(description = "路由路径")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 是否显示（0-隐藏，1-显示）
     */
    @Schema(description = "是否显示")
    private Integer visible;

    /**
     * 状态（0-禁用，1-正常）
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 子级权限（非数据库字段）
     */
    @TableField(exist = false)
    @Schema(description = "子级权限")
    private List<SysPermission> children;

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }

    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }

    public Integer getPermissionType() { return permissionType; }
    public void setPermissionType(Integer permissionType) { this.permissionType = permissionType; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public List<SysPermission> getChildren() { return children; }
    public void setChildren(List<SysPermission> children) { this.children = children; }
}
