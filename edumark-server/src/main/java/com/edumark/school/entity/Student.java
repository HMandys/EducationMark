package com.edumark.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学生实体
 *
 * @author EduMark
 */
@TableName("student")
@Schema(description = "学生")
public class Student extends BaseEntity {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "所属班级ID")
    private Long classId;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别: 0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "出生日期")
    private String birthday;

    @Schema(description = "入学日期")
    private String enrollDate;

    @Schema(description = "绑定码-家长绑定用")
    private String bindCode;

    @Schema(description = "状态: 0-休学 1-在读 2-毕业 3-退学")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "学校名称")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;

    @TableField(exist = false)
    @Schema(description = "年级名称")
    private String gradeName;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getEnrollDate() { return enrollDate; }
    public void setEnrollDate(String enrollDate) { this.enrollDate = enrollDate; }

    public String getBindCode() { return bindCode; }
    public void setBindCode(String bindCode) { this.bindCode = bindCode; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }
}
