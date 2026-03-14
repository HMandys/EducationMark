package com.edumark.system.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户查询DTO
 *
 * @author EduMark
 */
@Schema(description = "用户查询参数")
public class SysUserQueryDTO extends PageQuery {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户类型")
    private Integer userType;

    @Schema(description = "学校ID")
    private Long schoolId;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "状态")
    private Integer status;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getUserType() { return userType; }
    public void setUserType(Integer userType) { this.userType = userType; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
