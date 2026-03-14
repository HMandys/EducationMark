package com.edumark.marking.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 阅卷仲裁记录实体
 *
 * @author EduMark
 */
@TableName("marking_arbitration")
@Schema(description = "阅卷仲裁记录")
public class MarkingArbitration extends BaseEntity {

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "一评记录ID")
    private Long firstMarkingId;

    @Schema(description = "一评分数")
    private Integer firstScore;

    @Schema(description = "一评教师ID")
    private Long firstTeacherId;

    @Schema(description = "二评记录ID")
    private Long secondMarkingId;

    @Schema(description = "二评分数")
    private Integer secondScore;

    @Schema(description = "二评教师ID")
    private Long secondTeacherId;

    @Schema(description = "分差")
    private Integer scoreDiff;

    @Schema(description = "仲裁教师ID")
    private Long arbitrationTeacherId;

    @Schema(description = "仲裁分数")
    private Integer arbitrationScore;

    @Schema(description = "仲裁时间")
    private LocalDateTime arbitrationTime;

    @Schema(description = "仲裁说明")
    private String arbitrationComment;

    @Schema(description = "状态: 0-待仲裁 1-已仲裁")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Schema(description = "一评教师姓名")
    private String firstTeacherName;

    @TableField(exist = false)
    @Schema(description = "二评教师姓名")
    private String secondTeacherName;

    @TableField(exist = false)
    @Schema(description = "仲裁教师姓名")
    private String arbitrationTeacherName;

    @TableField(exist = false)
    @Schema(description = "题号")
    private String questionNo;

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getFirstMarkingId() { return firstMarkingId; }
    public void setFirstMarkingId(Long firstMarkingId) { this.firstMarkingId = firstMarkingId; }

    public Integer getFirstScore() { return firstScore; }
    public void setFirstScore(Integer firstScore) { this.firstScore = firstScore; }

    public Long getFirstTeacherId() { return firstTeacherId; }
    public void setFirstTeacherId(Long firstTeacherId) { this.firstTeacherId = firstTeacherId; }

    public Long getSecondMarkingId() { return secondMarkingId; }
    public void setSecondMarkingId(Long secondMarkingId) { this.secondMarkingId = secondMarkingId; }

    public Integer getSecondScore() { return secondScore; }
    public void setSecondScore(Integer secondScore) { this.secondScore = secondScore; }

    public Long getSecondTeacherId() { return secondTeacherId; }
    public void setSecondTeacherId(Long secondTeacherId) { this.secondTeacherId = secondTeacherId; }

    public Integer getScoreDiff() { return scoreDiff; }
    public void setScoreDiff(Integer scoreDiff) { this.scoreDiff = scoreDiff; }

    public Long getArbitrationTeacherId() { return arbitrationTeacherId; }
    public void setArbitrationTeacherId(Long arbitrationTeacherId) { this.arbitrationTeacherId = arbitrationTeacherId; }

    public Integer getArbitrationScore() { return arbitrationScore; }
    public void setArbitrationScore(Integer arbitrationScore) { this.arbitrationScore = arbitrationScore; }

    public LocalDateTime getArbitrationTime() { return arbitrationTime; }
    public void setArbitrationTime(LocalDateTime arbitrationTime) { this.arbitrationTime = arbitrationTime; }

    public String getArbitrationComment() { return arbitrationComment; }
    public void setArbitrationComment(String arbitrationComment) { this.arbitrationComment = arbitrationComment; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getFirstTeacherName() { return firstTeacherName; }
    public void setFirstTeacherName(String firstTeacherName) { this.firstTeacherName = firstTeacherName; }

    public String getSecondTeacherName() { return secondTeacherName; }
    public void setSecondTeacherName(String secondTeacherName) { this.secondTeacherName = secondTeacherName; }

    public String getArbitrationTeacherName() { return arbitrationTeacherName; }
    public void setArbitrationTeacherName(String arbitrationTeacherName) { this.arbitrationTeacherName = arbitrationTeacherName; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }
}
