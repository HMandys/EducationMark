package com.edumark.exam.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 考试查询DTO
 *
 * @author EduMark
 */
@Schema(description = "考试查询DTO")
public class ExamQueryDTO extends PageQuery {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "考试名称(模糊)")
    private String name;

    @Schema(description = "考试类型")
    private Integer type;

    @Schema(description = "学年")
    private String academicYear;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "考试状态")
    private Integer status;

    // Getters and Setters
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
