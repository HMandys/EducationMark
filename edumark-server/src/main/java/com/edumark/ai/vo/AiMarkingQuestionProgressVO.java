package com.edumark.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * AI 题目进度
 */
@Schema(description = "AI题目进度")
public class AiMarkingQuestionProgressVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题号")
    private Integer questionNo;

    @Schema(description = "总数")
    private Integer totalCount;

    @Schema(description = "已完成数")
    private Integer completedCount;

    @Schema(description = "待处理数")
    private Integer pendingCount;

    @Schema(description = "异常数")
    private Integer anomalyCount;

    @Schema(description = "进度百分比")
    private Double progress;

    public Integer getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(Integer questionNo) {
        this.questionNo = questionNo;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
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
