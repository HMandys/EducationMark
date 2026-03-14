package com.edumark.school.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 家长查询DTO
 *
 * @author EduMark
 */
@Schema(description = "家长查询DTO")
public class ParentQueryDTO extends PageQuery {

    @Schema(description = "姓名(模糊)")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "绑定的学生ID")
    private Long studentId;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
}
