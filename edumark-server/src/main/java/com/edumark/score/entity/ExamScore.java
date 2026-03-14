package com.edumark.score.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生考试成绩汇总实体
 *
 * @author EduMark
 */
@TableName("exam_score")
@Schema(description = "学生考试成绩汇总")
public class ExamScore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "科目数")
    private Integer subjectCount;

    @Schema(description = "班级排名")
    private Integer classRank;

    @Schema(description = "年级排名")
    private Integer gradeRank;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Schema(description = "学号")
    private String studentNumber;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public Integer getSubjectCount() { return subjectCount; }
    public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }

    public Integer getClassRank() { return classRank; }
    public void setClassRank(Integer classRank) { this.classRank = classRank; }

    public Integer getGradeRank() { return gradeRank; }
    public void setGradeRank(Integer gradeRank) { this.gradeRank = gradeRank; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
