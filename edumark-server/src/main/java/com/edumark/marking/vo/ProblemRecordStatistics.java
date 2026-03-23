package com.edumark.marking.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 问题卷统计VO
 *
 * @author EduMark
 */
@Schema(description = "问题卷统计VO")
public class ProblemRecordStatistics {

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "总记录数")
    private Integer totalRecords;

    @Schema(description = "问题卷数")
    private Integer problemRecords;

    @Schema(description = "问题卷比例")
    private Double problemPercent;

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Integer getTotalRecords() { return totalRecords; }
    public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }

    public Integer getProblemRecords() { return problemRecords; }
    public void setProblemRecords(Integer problemRecords) { this.problemRecords = problemRecords; }

    public Double getProblemPercent() { return problemPercent; }
    public void setProblemPercent(Double problemPercent) { this.problemPercent = problemPercent; }
}
