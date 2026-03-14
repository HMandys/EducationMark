package com.edumark.school.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 年级VO
 *
 * @author EduMark
 */
@Schema(description = "年级响应VO")
public class GradeVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "年级名称")
    private String name;

    @Schema(description = "年级编码")
    private String code;

    @Schema(description = "入学年份")
    private Integer enrollYear;

    @Schema(description = "年级序号")
    private Integer gradeNum;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "班级数量")
    private Integer classCount;

    @Schema(description = "学生数量")
    private Integer studentCount;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getEnrollYear() { return enrollYear; }
    public void setEnrollYear(Integer enrollYear) { this.enrollYear = enrollYear; }

    public Integer getGradeNum() { return gradeNum; }
    public void setGradeNum(Integer gradeNum) { this.gradeNum = gradeNum; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getClassCount() { return classCount; }
    public void setClassCount(Integer classCount) { this.classCount = classCount; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
