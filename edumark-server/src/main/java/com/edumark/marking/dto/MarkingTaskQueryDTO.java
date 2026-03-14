package com.edumark.marking.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 阅卷任务查询DTO
 *
 * @author EduMark
 */
@Schema(description = "阅卷任务查询DTO")
public class MarkingTaskQueryDTO extends PageQuery {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "任务类型")
    private Integer taskType;

    @Schema(description = "状态")
    private Integer status;

    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Integer getTaskType() { return taskType; }
    public void setTaskType(Integer taskType) { this.taskType = taskType; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
