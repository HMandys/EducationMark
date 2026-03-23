package com.edumark.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 裁题进度VO
 *
 * @author EduMark
 */
@Schema(description = "裁题进度VO")
public class CropProgressVO {

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "总答题卡数")
    private Integer totalAnswerSheets;

    @Schema(description = "已裁题答题卡数")
    private Integer croppedAnswerSheets;

    @Schema(description = "总题目数")
    private Integer totalQuestions;

    @Schema(description = "已裁题题目数")
    private Integer croppedQuestions;

    @Schema(description = "异常题目数")
    private Integer anomalyQuestions;

    @Schema(description = "裁题进度百分比")
    private Double progressPercent;

    @Schema(description = "状态：0-未开始 1-进行中 2-已完成")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    private LocalDateTime endTime;

    // Getters and Setters
    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Integer getTotalAnswerSheets() { return totalAnswerSheets; }
    public void setTotalAnswerSheets(Integer totalAnswerSheets) { this.totalAnswerSheets = totalAnswerSheets; }

    public Integer getCroppedAnswerSheets() { return croppedAnswerSheets; }
    public void setCroppedAnswerSheets(Integer croppedAnswerSheets) { this.croppedAnswerSheets = croppedAnswerSheets; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getCroppedQuestions() { return croppedQuestions; }
    public void setCroppedQuestions(Integer croppedQuestions) { this.croppedQuestions = croppedQuestions; }

    public Integer getAnomalyQuestions() { return anomalyQuestions; }
    public void setAnomalyQuestions(Integer anomalyQuestions) { this.anomalyQuestions = anomalyQuestions; }

    public Double getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Double progressPercent) { this.progressPercent = progressPercent; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
