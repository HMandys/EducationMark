package com.edumark.marking.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 阅卷会话VO
 *
 * @author EduMark
 */
@Schema(description = "阅卷会话VO")
public class MarkingSessionVO {

    @Schema(description = "会话令牌")
    private String sessionToken;

    @Schema(description = "任务ID")
    private Long taskId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "题目满分")
    private Integer fullScore;

    @Schema(description = "评阅角色")
    private Integer markingRole;

    @Schema(description = "评阅角色名称")
    private String markingRoleName;

    @Schema(description = "总份数")
    private Integer totalCount;

    @Schema(description = "已完成份数")
    private Integer completedCount;

    @Schema(description = "待阅份数")
    private Integer pendingCount;

    // Getters and Setters
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public Integer getMarkingRole() { return markingRole; }
    public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }

    public String getMarkingRoleName() { return markingRoleName; }
    public void setMarkingRoleName(String markingRoleName) { this.markingRoleName = markingRoleName; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }

    public Integer getPendingCount() { return pendingCount; }
    public void setPendingCount(Integer pendingCount) { this.pendingCount = pendingCount; }
}
