package com.edumark.marking.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 阅卷仲裁VO
 *
 * @author EduMark
 */
@Schema(description = "阅卷仲裁VO")
public class MarkingArbitrationVO {

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

    @Schema(description = "一评分数")
    private Integer firstScore;

    @Schema(description = "一评教师ID")
    private Long firstTeacherId;

    @Schema(description = "一评教师姓名")
    private String firstTeacherName;

    @Schema(description = "二评分数")
    private Integer secondScore;

    @Schema(description = "二评教师ID")
    private Long secondTeacherId;

    @Schema(description = "二评教师姓名")
    private String secondTeacherName;

    @Schema(description = "分差")
    private Integer scoreDiff;

    @Schema(description = "仲裁教师ID")
    private Long arbitrationTeacherId;

    @Schema(description = "仲裁教师姓名")
    private String arbitrationTeacherName;

    @Schema(description = "仲裁分数")
    private Integer arbitrationScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "仲裁时间")
    private LocalDateTime arbitrationTime;

    @Schema(description = "仲裁说明")
    private String arbitrationComment;

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

    @Schema(description = "一评记录ID")
    private Long firstMarkingId;

    @Schema(description = "二评记录ID")
    private Long secondMarkingId;

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

    public Integer getFirstScore() { return firstScore; }
    public void setFirstScore(Integer firstScore) { this.firstScore = firstScore; }

    public Long getFirstTeacherId() { return firstTeacherId; }
    public void setFirstTeacherId(Long firstTeacherId) { this.firstTeacherId = firstTeacherId; }

    public String getFirstTeacherName() { return firstTeacherName; }
    public void setFirstTeacherName(String firstTeacherName) { this.firstTeacherName = firstTeacherName; }

    public Integer getSecondScore() { return secondScore; }
    public void setSecondScore(Integer secondScore) { this.secondScore = secondScore; }

    public Long getSecondTeacherId() { return secondTeacherId; }
    public void setSecondTeacherId(Long secondTeacherId) { this.secondTeacherId = secondTeacherId; }

    public String getSecondTeacherName() { return secondTeacherName; }
    public void setSecondTeacherName(String secondTeacherName) { this.secondTeacherName = secondTeacherName; }

    public Integer getScoreDiff() { return scoreDiff; }
    public void setScoreDiff(Integer scoreDiff) { this.scoreDiff = scoreDiff; }

    public Long getArbitrationTeacherId() { return arbitrationTeacherId; }
    public void setArbitrationTeacherId(Long arbitrationTeacherId) { this.arbitrationTeacherId = arbitrationTeacherId; }

    public String getArbitrationTeacherName() { return arbitrationTeacherName; }
    public void setArbitrationTeacherName(String arbitrationTeacherName) { this.arbitrationTeacherName = arbitrationTeacherName; }

    public Integer getArbitrationScore() { return arbitrationScore; }
    public void setArbitrationScore(Integer arbitrationScore) { this.arbitrationScore = arbitrationScore; }

    public LocalDateTime getArbitrationTime() { return arbitrationTime; }
    public void setArbitrationTime(LocalDateTime arbitrationTime) { this.arbitrationTime = arbitrationTime; }

    public String getArbitrationComment() { return arbitrationComment; }
    public void setArbitrationComment(String arbitrationComment) { this.arbitrationComment = arbitrationComment; }

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

    public Long getFirstMarkingId() { return firstMarkingId; }
    public void setFirstMarkingId(Long firstMarkingId) { this.firstMarkingId = firstMarkingId; }

    public Long getSecondMarkingId() { return secondMarkingId; }
    public void setSecondMarkingId(Long secondMarkingId) { this.secondMarkingId = secondMarkingId; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
