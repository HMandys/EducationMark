package com.edumark.marking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 阅卷任务分配DTO
 *
 * @author EduMark
 */
@Schema(description = "阅卷任务分配DTO")
public class MarkingTaskAssignDTO {

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "分配列表")
    private List<TeacherAssign> assigns;

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public List<TeacherAssign> getAssigns() { return assigns; }
    public void setAssigns(List<TeacherAssign> assigns) { this.assigns = assigns; }

    /**
     * 教师分配
     */
    @Schema(description = "教师分配")
    public static class TeacherAssign {
        @Schema(description = "教师ID")
        private Long teacherId;

        @Schema(description = "分配份数")
        private Integer assignCount;

        @Schema(description = "评阅角色: 1-一评 2-二评 3-仲裁")
        private Integer markingRole;

        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

        public Integer getAssignCount() { return assignCount; }
        public void setAssignCount(Integer assignCount) { this.assignCount = assignCount; }

        public Integer getMarkingRole() { return markingRole; }
        public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }
    }
}
