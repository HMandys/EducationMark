package com.edumark.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡实体
 *
 * @author EduMark
 */
@TableName("answer_sheet")
@Schema(description = "答题卡")
public class AnswerSheet extends BaseEntity {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学号")
    private String studentNumber;

    @Schema(description = "座位号")
    private String seatNumber;

    @Schema(description = "图片数量")
    private Integer imageCount;

    @Schema(description = "状态: 0-识别中 1-已识别 2-待阅卷 3-阅卷中 4-已完成 5-识别异常")
    private Integer status;

    @Schema(description = "客观题得分")
    private Integer objectiveScore;

    @Schema(description = "主观题得分")
    private Integer subjectiveScore;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "科目名称")
    private String subjectName;

    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;

    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Integer getImageCount() { return imageCount; }
    public void setImageCount(Integer imageCount) { this.imageCount = imageCount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getObjectiveScore() { return objectiveScore; }
    public void setObjectiveScore(Integer objectiveScore) { this.objectiveScore = objectiveScore; }

    public Integer getSubjectiveScore() { return subjectiveScore; }
    public void setSubjectiveScore(Integer subjectiveScore) { this.subjectiveScore = subjectiveScore; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
