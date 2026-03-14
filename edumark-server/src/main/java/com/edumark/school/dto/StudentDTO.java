package com.edumark.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学生DTO
 *
 * @author EduMark
 */
@Schema(description = "学生请求DTO")
public class StudentDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "所属学校ID", required = true)
    private Long schoolId;

    @Schema(description = "所属班级ID", required = true)
    private Long classId;

    @Schema(description = "学号", required = true)
    private String studentNumber;

    @Schema(description = "姓名", required = true)
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

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否创建用户账号")
    private Boolean createUser;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Boolean getCreateUser() { return createUser; }
    public void setCreateUser(Boolean createUser) { this.createUser = createUser; }
}
