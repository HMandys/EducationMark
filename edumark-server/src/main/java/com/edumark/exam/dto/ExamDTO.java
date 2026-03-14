package com.edumark.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 考试DTO
 *
 * @author EduMark
 */
@Schema(description = "考试请求DTO")
public class ExamDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "所属学校ID", required = true)
    private Long schoolId;

    @Schema(description = "考试名称", required = true)
    private String name;

    @Schema(description = "考试编码")
    private String code;

    @Schema(description = "考试类型")
    private Integer type;

    @Schema(description = "学年")
    private String academicYear;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "考试开始时间")
    private String startTime;

    @Schema(description = "考试结束时间")
    private String endTime;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "参考班级ID列表")
    private List<Long> classIds;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<Long> getClassIds() { return classIds; }
    public void setClassIds(List<Long> classIds) { this.classIds = classIds; }
}
