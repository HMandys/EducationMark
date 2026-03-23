package com.edumark.marking.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 阅卷会话实体
 *
 * @author EduMark
 */
@TableName("marking_session")
@Schema(description = "阅卷会话")
public class MarkingSession extends BaseEntity {

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "使用的阅卷码")
    private String accessCode;

    @Schema(description = "评阅角色: 1-一评 2-二评")
    private Integer markingRole;

    @Schema(description = "会话令牌")
    private String sessionToken;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public Integer getMarkingRole() { return markingRole; }
    public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
}
