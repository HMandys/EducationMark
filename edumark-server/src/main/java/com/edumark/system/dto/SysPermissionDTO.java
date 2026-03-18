package com.edumark.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * 权限请求参数
 *
 * @author EduMark
 */
@Schema(description = "权限请求参数")
public class SysPermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "权限ID")
    private Long id;

    @Schema(description = "父级ID")
    private Long parentId;

    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称", required = true)
    private String permissionName;

    @Schema(description = "权限编码")
    private String permissionCode;

    @NotNull(message = "权限类型不能为空")
    @Schema(description = "权限类型", required = true)
    private Integer permissionType;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否显示")
    private Integer visible;

    @Schema(description = "状态")
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}
