package com.edumark.marking.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阅卷任务VO
 *
 * @author EduMark
 */
@Schema(description = "阅卷任务VO")
public class MarkingTaskVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务类型")
    private Integer taskType;

    @Schema(description = "总份数")
    private Integer totalCount;

    @Schema(description = "已完成份数")
    private Integer completedCount;

    @Schema(description = "待阅份数")
    private Integer pendingCount;

    @Schema(description = "完成进度")
    private Double progress;

    @Schema(description = "是否启用双评")
    private Integer enableDoubleMarking;

    @Schema(description = "双评阈值")
    private Integer doubleMarkingThreshold;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "分配列表")
    private List<MarkingTaskAssignVO> assigns;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTaskType() { return taskType; }
    public void setTaskType(Integer taskType) { this.taskType = taskType; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }

    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public Integer getEnableDoubleMarking() { return enableDoubleMarking; }
    public void setEnableDoubleMarking(Integer enableDoubleMarking) { this.enableDoubleMarking = enableDoubleMarking; }

    public Integer getDoubleMarkingThreshold() { return doubleMarkingThreshold; }
    public void setDoubleMarkingThreshold(Integer doubleMarkingThreshold) { this.doubleMarkingThreshold = doubleMarkingThreshold; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<MarkingTaskAssignVO> getAssigns() { return assigns; }
    public void setAssigns(List<MarkingTaskAssignVO> assigns) { this.assigns = assigns; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
