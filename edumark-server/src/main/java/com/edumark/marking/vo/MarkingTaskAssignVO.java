package com.edumark.marking.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 阅卷任务分配VO
 *
 * @author EduMark
 */
@Schema(description = "阅卷任务分配VO")
public class MarkingTaskAssignVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "分配份数")
    private Integer assignCount;

    @Schema(description = "已完成份数")
    private Integer completedCount;

    @Schema(description = "完成进度")
    private Double progress;

    @Schema(description = "评阅角色")
    private Integer markingRole;

    @Schema(description = "评阅角色名称")
    private String markingRoleName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "任务状态")
    private Integer taskStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public Integer getAssignCount() { return assignCount; }
    public void setAssignCount(Integer assignCount) { this.assignCount = assignCount; }

    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public Integer getMarkingRole() { return markingRole; }
    public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }

    public String getMarkingRoleName() { return markingRoleName; }
    public void setMarkingRoleName(String markingRoleName) { this.markingRoleName = markingRoleName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public Integer getTaskStatus() { return taskStatus; }
    public void setTaskStatus(Integer taskStatus) { this.taskStatus = taskStatus; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
