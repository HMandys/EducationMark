package com.edumark.school.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 学生VO
 *
 * @author EduMark
 */
@Schema(description = "学生响应VO")
public class StudentVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "所属班级ID")
    private Long classId;

    @Schema(description = "所属年级ID")
    private Long gradeId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "年级名称")
    private String gradeName;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "出生日期")
    private String birthday;

    @Schema(description = "入学日期")
    private String enrollDate;

    @Schema(description = "绑定码")
    private String bindCode;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

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

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
