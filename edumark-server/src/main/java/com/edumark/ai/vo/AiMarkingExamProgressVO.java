package com.edumark.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 考试进度
 */
@Schema(description = "AI考试进度")
public class AiMarkingExamProgressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "AI题目数")
    private Integer aiQuestionCount;

    @Schema(description = "进入AI链路的答题卡数")
    private Integer answerSheetCount;

    @Schema(description = "总任务数")
    private Integer totalTaskCount;

    @Schema(description = "已完成数")
    private Integer completedCount;

    @Schema(description = "待处理数")
    private Integer pendingCount;

    @Schema(description = "异常数")
    private Integer anomalyCount;

    @Schema(description = "进度百分比")
    private Double progress;

    @Schema(description = "状态: 0-未开始 1-进行中 2-已完成 3-异常待处理")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "模板更新时间")
    private LocalDateTime updateTime;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Long getExamSubjectId() {
        return examSubjectId;
    }

    public void setExamSubjectId(Long examSubjectId) {
        this.examSubjectId = examSubjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getAiQuestionCount() {
        return aiQuestionCount;
    }

    public void setAiQuestionCount(Integer aiQuestionCount) {
        this.aiQuestionCount = aiQuestionCount;
    }

    public Integer getAnswerSheetCount() {
        return answerSheetCount;
    }

    public void setAnswerSheetCount(Integer answerSheetCount) {
        this.answerSheetCount = answerSheetCount;
    }

    public Integer getTotalTaskCount() {
        return totalTaskCount;
    }

    public void setTotalTaskCount(Integer totalTaskCount) {
        this.totalTaskCount = totalTaskCount;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
