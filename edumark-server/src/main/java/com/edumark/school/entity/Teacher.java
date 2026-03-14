package com.edumark.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 教师实体
 *
 * @author EduMark
 */
@TableName("teacher")
@Schema(description = "教师")
public class Teacher extends BaseEntity {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "工号")
    private String jobNumber;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别: 0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "手机号")
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

    @Schema(description = "状态: 0-离职 1-在职")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "学校名称")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "用户名")
    private String username;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
