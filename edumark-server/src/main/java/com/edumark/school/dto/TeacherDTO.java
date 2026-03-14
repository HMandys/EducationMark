package com.edumark.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 教师DTO
 *
 * @author EduMark
 */
@Schema(description = "教师请求DTO")
public class TeacherDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "所属学校ID", required = true)
    private Long schoolId;

    @Schema(description = "工号")
    private String jobNumber;

    @Schema(description = "姓名", required = true)
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号", required = true)
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "任教科目")
    private String subject;

    @Schema(description = "职称")
    private String title;

    @Schema(description = "入职日期")
    private String entryDate;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否创建用户账号")
    private Boolean createUser;

    @Schema(description = "角色ID")
    private Long roleId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getJobNumber() { return jobNumber; }
    public void setJobNumber(String jobNumber) { this.jobNumber = jobNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEntryDate() { return entryDate; }
    public void setEntryDate(String entryDate) { this.entryDate = entryDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Boolean getCreateUser() { return createUser; }
    public void setCreateUser(Boolean createUser) { this.createUser = createUser; }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}
