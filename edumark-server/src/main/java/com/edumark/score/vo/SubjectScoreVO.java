package com.edumark.score.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * 科目成绩VO
 *
 * @author EduMark
 */
@Schema(description = "科目成绩VO")
public class SubjectScoreVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "得分")
    private BigDecimal score;

    @Schema(description = "客观题得分")
    private BigDecimal objectiveScore;

    @Schema(description = "主观题得分")
    private BigDecimal subjectiveScore;

    @Schema(description = "班级排名")
    private Integer classRank;

    @Schema(description = "年级排名")
    private Integer gradeRank;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "班级名称")
    private String className;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }

    public BigDecimal getObjectiveScore() { return objectiveScore; }
    public void setObjectiveScore(BigDecimal objectiveScore) { this.objectiveScore = objectiveScore; }

    public BigDecimal getSubjectiveScore() { return subjectiveScore; }
    public void setSubjectiveScore(BigDecimal subjectiveScore) { this.subjectiveScore = subjectiveScore; }

    public Integer getClassRank() { return classRank; }
    public void setClassRank(Integer classRank) { this.classRank = classRank; }

    public Integer getGradeRank() { return gradeRank; }
    public void setGradeRank(Integer gradeRank) { this.gradeRank = gradeRank; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
