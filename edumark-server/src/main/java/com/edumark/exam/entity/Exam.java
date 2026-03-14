package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 考试实体
 *
 * @author EduMark
 */
@TableName("exam")
@Schema(description = "考试")
public class Exam extends BaseEntity {

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "考试名称")
    private String name;

    @Schema(description = "考试编码")
    private String code;

    @Schema(description = "考试类型: 1-期中考试 2-期末考试 3-月考 4-模拟考试 5-其他")
    private Integer type;

    @Schema(description = "学年")
    private String academicYear;

    @Schema(description = "学期: 1-第一学期 2-第二学期")
    private Integer semester;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "考试开始时间")
    private LocalDateTime startTime;

    @Schema(description = "考试结束时间")
    private LocalDateTime endTime;

    @Schema(description = "考试状态: 0-草稿 1-待考试 2-考试中 3-阅卷中 4-已完成 5-已发布")
    private Integer status;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "参考人数")
    private Integer studentCount;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "学校名称")
    private String schoolName;

    @TableField(exist = false)
    @Schema(description = "年级名称")
    private String gradeName;

    @TableField(exist = false)
    @Schema(description = "科目数量")
    private Integer subjectCount;

    // Getters and Setters
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

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public Integer getSubjectCount() { return subjectCount; }
    public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }
}
