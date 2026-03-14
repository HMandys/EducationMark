package com.edumark.marking.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 阅卷任务分配实体
 *
 * @author EduMark
 */
@TableName("marking_task_assign")
@Schema(description = "阅卷任务分配")
public class MarkingTaskAssign extends BaseEntity {

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "教师ID")
    private Long teacherId;

    @Schema(description = "分配份数")
    private Integer assignCount;

    @Schema(description = "已完成份数")
    private Integer completedCount;

    @Schema(description = "评阅角色: 1-一评 2-二评 3-仲裁")
    private Integer markingRole;

    @Schema(description = "状态: 0-未开始 1-进行中 2-已完成")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "教师姓名")
    private String teacherName;

    @TableField(exist = false)
    @Schema(description = "任务名称")
    private String taskName;

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Integer getAssignCount() { return assignCount; }
    public void setAssignCount(Integer assignCount) { this.assignCount = assignCount; }

    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }

    public Integer getMarkingRole() { return markingRole; }
    public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
}
