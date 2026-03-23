package com.edumark.marking.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 阅卷任务实体
 *
 * @author EduMark
 */
@TableName("marking_task")
@Schema(description = "阅卷任务")
public class MarkingTask extends BaseEntity {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "任务名称")
    private String name;

    @Schema(description = "任务类型: 1-客观题 2-主观题")
    private Integer taskType;

    @Schema(description = "总份数")
    private Integer totalCount;

    @Schema(description = "已完成份数")
    private Integer completedCount;

    @Schema(description = "待阅份数")
    private Integer pendingCount;

    @Schema(description = "是否启用双评: 0-否 1-是")
    private Integer enableDoubleMarking;

    @Schema(description = "双评阈值")
    private Integer doubleMarkingThreshold;

    @Schema(description = "状态: 0-未开始 1-进行中 2-已完成")
    private Integer status;

    @Schema(description = "阅卷码(8位数字)")
    private String accessCode;

    @Schema(description = "二评阅卷码(8位数字)")
    private String secondAccessCode;

    @Schema(description = "阅卷码过期时间")
    private LocalDateTime accessCodeExpireTime;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "科目名称")
    private String subjectName;

    @TableField(exist = false)
    @Schema(description = "题号")
    private String questionNo;

    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

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

    public Integer getEnableDoubleMarking() { return enableDoubleMarking; }
    public void setEnableDoubleMarking(Integer enableDoubleMarking) { this.enableDoubleMarking = enableDoubleMarking; }

    public Integer getDoubleMarkingThreshold() { return doubleMarkingThreshold; }
    public void setDoubleMarkingThreshold(Integer doubleMarkingThreshold) { this.doubleMarkingThreshold = doubleMarkingThreshold; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public String getSecondAccessCode() { return secondAccessCode; }
    public void setSecondAccessCode(String secondAccessCode) { this.secondAccessCode = secondAccessCode; }

    public LocalDateTime getAccessCodeExpireTime() { return accessCodeExpireTime; }
    public void setAccessCodeExpireTime(LocalDateTime accessCodeExpireTime) { this.accessCodeExpireTime = accessCodeExpireTime; }
}
