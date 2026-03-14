package com.edumark.school.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 教师查询DTO
 *
 * @author EduMark
 */
@Schema(description = "教师查询DTO")
public class TeacherQueryDTO extends PageQuery {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "工号")
    private String jobNumber;

    @Schema(description = "姓名(模糊)")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "任教科目")
    private String subject;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getJobNumber() { return jobNumber; }
    public void setJobNumber(String jobNumber) { this.jobNumber = jobNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
