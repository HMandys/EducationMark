package com.edumark.school.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 班级查询DTO
 *
 * @author EduMark
 */
@Schema(description = "班级查询DTO")
public class ClassInfoQueryDTO extends PageQuery {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "所属年级ID")
    private Long gradeId;

    @Schema(description = "班级名称(模糊)")
    private String name;

    @Schema(description = "班主任教师ID")
    private Long headTeacherId;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getHeadTeacherId() { return headTeacherId; }
    public void setHeadTeacherId(Long headTeacherId) { this.headTeacherId = headTeacherId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
