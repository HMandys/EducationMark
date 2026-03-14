package com.edumark.answersheet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡模板查询DTO
 *
 * @author EduMark
 */
@Schema(description = "答题卡模板查询DTO")
public class AnswerSheetTemplateQueryDTO {

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 10;

    @Schema(description = "试卷ID")
    private Long paperId;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "状态: 0-草稿 1-已发布")
    private Integer status;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "科目名称")
    private String subjectName;

    // Getters and Setters
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}
