package com.edumark.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 考试科目DTO
 *
 * @author EduMark
 */
@Schema(description = "考试科目请求DTO")
public class ExamSubjectDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "考试ID", required = true)
    private Long examId;

    @Schema(description = "科目名称", required = true)
    private String subjectName;

    @Schema(description = "科目编码")
    private String subjectCode;

    @Schema(description = "满分", required = true)
    private Integer fullScore;

    @Schema(description = "及格分")
    private Integer passScore;

    @Schema(description = "优秀分")
    private Integer excellentScore;

    @Schema(description = "考试时长(分钟)")
    private Integer duration;

    @Schema(description = "考试开始时间")
    private String startTime;

    @Schema(description = "考试结束时间")
    private String endTime;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public Integer getPassScore() { return passScore; }
    public void setPassScore(Integer passScore) { this.passScore = passScore; }

    public Integer getExcellentScore() { return excellentScore; }
    public void setExcellentScore(Integer excellentScore) { this.excellentScore = excellentScore; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
