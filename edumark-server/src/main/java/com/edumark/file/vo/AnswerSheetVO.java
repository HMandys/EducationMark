package com.edumark.file.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 答题卡VO
 *
 * @author EduMark
 */
@Schema(description = "答题卡响应VO")
public class AnswerSheetVO {

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

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "座位号")
    private String seatNumber;

    @Schema(description = "图片数量")
    private Integer imageCount;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "客观题得分")
    private Integer objectiveScore;

    @Schema(description = "主观题得分")
    private Integer subjectiveScore;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "图片列表")
    private List<AnswerSheetImageVO> images;

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

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Integer getImageCount() { return imageCount; }
    public void setImageCount(Integer imageCount) { this.imageCount = imageCount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public Integer getObjectiveScore() { return objectiveScore; }
    public void setObjectiveScore(Integer objectiveScore) { this.objectiveScore = objectiveScore; }

    public Integer getSubjectiveScore() { return subjectiveScore; }
    public void setSubjectiveScore(Integer subjectiveScore) { this.subjectiveScore = subjectiveScore; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<AnswerSheetImageVO> getImages() { return images; }
    public void setImages(List<AnswerSheetImageVO> images) { this.images = images; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
