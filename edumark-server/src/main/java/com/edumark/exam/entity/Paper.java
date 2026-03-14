package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 试卷实体
 *
 * @author EduMark
 */
@TableName("paper")
@Schema(description = "试卷")
public class Paper extends BaseEntity {

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "试卷名称")
    private String name;

    @Schema(description = "试卷编码")
    private String code;

    @Schema(description = "试卷类型: 1-A卷 2-B卷")
    private Integer type;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "题目数量")
    private Integer questionCount;

    @Schema(description = "客观题数量")
    private Integer objectiveCount;

    @Schema(description = "主观题数量")
    private Integer subjectiveCount;

    @Schema(description = "状态: 0-草稿 1-已完成")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "科目名称")
    private String subjectName;

    // Getters and Setters
    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public Integer getObjectiveCount() { return objectiveCount; }
    public void setObjectiveCount(Integer objectiveCount) { this.objectiveCount = objectiveCount; }

    public Integer getSubjectiveCount() { return subjectiveCount; }
    public void setSubjectiveCount(Integer subjectiveCount) { this.subjectiveCount = subjectiveCount; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}
