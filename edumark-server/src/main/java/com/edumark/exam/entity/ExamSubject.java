package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 考试科目实体
 *
 * @author EduMark
 */
@TableName("exam_subject")
@Schema(description = "考试科目")
public class ExamSubject extends BaseEntity {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "科目编码")
    private String subjectCode;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "及格分")
    private Integer passScore;

    @Schema(description = "优秀分")
    private Integer excellentScore;

    @Schema(description = "考试时长(分钟)")
    private Integer duration;

    @Schema(description = "考试开始时间")
    private LocalDateTime startTime;

    @Schema(description = "考试结束时间")
    private LocalDateTime endTime;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "题目数量")
    private Integer questionCount;

    // Getters and Setters
    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public Integer getPassScore() { return passScore; }
    public void setPassScore(Integer passScore) { this.passScore = passScore; }

    public Integer getExcellentScore() { return excellentScore; }
    public void setExcellentScore(Integer excellentScore) { this.excellentScore = excellentScore; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }
}
