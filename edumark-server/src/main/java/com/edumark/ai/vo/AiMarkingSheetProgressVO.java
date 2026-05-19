package com.edumark.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * AI 答题卡进度
 */
@Schema(description = "AI答题卡进度")
public class AiMarkingSheetProgressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "班级")
    private String className;

    @Schema(description = "答题卡状态")
    private Integer answerSheetStatus;

    @Schema(description = "答题卡状态名称")
    private String answerSheetStatusName;

    @Schema(description = "AI题目总数")
    private Integer totalQuestionCount;

    @Schema(description = "已完成数")
    private Integer completedCount;

    @Schema(description = "待处理数")
    private Integer pendingCount;

    @Schema(description = "异常数")
    private Integer anomalyCount;

    @Schema(description = "进度百分比")
    private Double progress;

    public Long getAnswerSheetId() {
        return answerSheetId;
    }

    public void setAnswerSheetId(Long answerSheetId) {
        this.answerSheetId = answerSheetId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getAnswerSheetStatus() {
        return answerSheetStatus;
    }

    public void setAnswerSheetStatus(Integer answerSheetStatus) {
        this.answerSheetStatus = answerSheetStatus;
    }

    public String getAnswerSheetStatusName() {
        return answerSheetStatusName;
    }

    public void setAnswerSheetStatusName(String answerSheetStatusName) {
        this.answerSheetStatusName = answerSheetStatusName;
    }

    public Integer getTotalQuestionCount() {
        return totalQuestionCount;
    }

    public void setTotalQuestionCount(Integer totalQuestionCount) {
        this.totalQuestionCount = totalQuestionCount;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }

    public Integer getAnomalyCount() {
        return anomalyCount;
    }

    public void setAnomalyCount(Integer anomalyCount) {
        this.anomalyCount = anomalyCount;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }
}
