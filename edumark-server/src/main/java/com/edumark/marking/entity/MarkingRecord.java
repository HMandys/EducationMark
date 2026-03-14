package com.edumark.marking.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 阅卷记录实体
 *
 * @author EduMark
 */
@TableName("marking_record")
@Schema(description = "阅卷记录")
public class MarkingRecord extends BaseEntity {

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "阅卷教师ID")
    private Long teacherId;

    @Schema(description = "评阅角色: 1-一评 2-二评 3-仲裁")
    private Integer markingRole;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "评语")
    private String comment;

    @Schema(description = "阅卷时间")
    private LocalDateTime markingTime;

    @Schema(description = "状态: 0-待评 1-已评 2-待仲裁 3-仲裁完成")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "教师姓名")
    private String teacherName;

    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Schema(description = "学号")
    private String studentNumber;

    @TableField(exist = false)
    @Schema(description = "题号")
    private String questionNo;

    // Getters and Setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Integer getMarkingRole() { return markingRole; }
    public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getMarkingTime() { return markingTime; }
    public void setMarkingTime(LocalDateTime markingTime) { this.markingTime = markingTime; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }
}
