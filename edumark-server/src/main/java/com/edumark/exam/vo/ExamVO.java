package com.edumark.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试VO
 *
 * @author EduMark
 */
@Schema(description = "考试响应VO")
public class ExamVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "所属学校ID")
    private Long schoolId;

    @Schema(description = "学校名称")
    private String schoolName;

    @Schema(description = "考试名称")
    private String name;

    @Schema(description = "考试编码")
    private String code;

    @Schema(description = "考试类型")
    private Integer type;

    @Schema(description = "考试类型名称")
    private String typeName;

    @Schema(description = "学年")
    private String academicYear;

    @Schema(description = "学期")
    private Integer semester;

    @Schema(description = "年级ID")
    private Long gradeId;

    @Schema(description = "年级名称")
    private String gradeName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "考试开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "考试结束时间")
    private LocalDateTime endTime;

    @Schema(description = "考试状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "参考人数")
    private Integer studentCount;

    @Schema(description = "科目数量")
    private Integer subjectCount;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "参考班级列表")
    private List<ExamClassVO> classes;

    @Schema(description = "科目列表")
    private List<ExamSubjectVO> subjects;

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

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }

    public Integer getSubjectCount() { return subjectCount; }
    public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<ExamClassVO> getClasses() { return classes; }
    public void setClasses(List<ExamClassVO> classes) { this.classes = classes; }

    public List<ExamSubjectVO> getSubjects() { return subjects; }
    public void setSubjects(List<ExamSubjectVO> subjects) { this.subjects = subjects; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    /**
     * 考试班级VO
     */
    @Schema(description = "考试班级VO")
    public static class ExamClassVO {
        @Schema(description = "班级ID")
        private Long classId;

        @Schema(description = "班级名称")
        private String className;

        @Schema(description = "学生数量")
        private Integer studentCount;

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public Integer getStudentCount() { return studentCount; }
        public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    }
}
