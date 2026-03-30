package com.edumark.marking.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阅卷记录VO
 *
 * @author EduMark
 */
@Schema(description = "阅卷记录VO")
public class MarkingRecordVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "阅卷任务ID")
    private Long taskId;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "阅卷教师ID")
    private Long teacherId;

    @Schema(description = "教师姓名")
    private String teacherName;

    @Schema(description = "评阅角色")
    private Integer markingRole;

    @Schema(description = "评阅角色名称")
    private String markingRoleName;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "评语")
    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "阅卷时间")
    private LocalDateTime markingTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "答题卡图片列表")
    private List<String> answerImages;

    @Schema(description = "答题卡图片URL")
    private String answerImageUrl;

    @Schema(description = "原始答题卡图片URL")
    private String originalImageUrl;

    @Schema(description = "标注数据(JSON)")
    private String annotations;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public Integer getMarkingRole() { return markingRole; }
    public void setMarkingRole(Integer markingRole) { this.markingRole = markingRole; }

    public String getMarkingRoleName() { return markingRoleName; }
    public void setMarkingRoleName(String markingRoleName) { this.markingRoleName = markingRoleName; }

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

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public List<String> getAnswerImages() { return answerImages; }
    public void setAnswerImages(List<String> answerImages) { this.answerImages = answerImages; }

    public String getAnswerImageUrl() { return answerImageUrl; }
    public void setAnswerImageUrl(String answerImageUrl) { this.answerImageUrl = answerImageUrl; }

    public String getOriginalImageUrl() { return originalImageUrl; }
    public void setOriginalImageUrl(String originalImageUrl) { this.originalImageUrl = originalImageUrl; }

    public String getAnnotations() { return annotations; }
    public void setAnnotations(String annotations) { this.annotations = annotations; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
