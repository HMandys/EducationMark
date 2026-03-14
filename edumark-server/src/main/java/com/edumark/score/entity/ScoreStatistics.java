package com.edumark.score.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 成绩统计实体
 *
 * @author EduMark
 */
@TableName(value = "score_statistics", autoResultMap = true)
@Schema(description = "成绩统计")
public class ScoreStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试科目ID(为空表示总分统计)")
    private Long examSubjectId;

    @Schema(description = "班级ID(为空表示年级统计)")
    private Long classId;

    @Schema(description = "统计类型: 1-班级科目 2-班级总分 3-年级科目 4-年级总分")
    private Integer statType;

    @Schema(description = "参考人数")
    private Integer studentCount;

    @Schema(description = "满分")
    private BigDecimal fullScore;

    @Schema(description = "最高分")
    private BigDecimal maxScore;

    @Schema(description = "最低分")
    private BigDecimal minScore;

    @Schema(description = "平均分")
    private BigDecimal avgScore;

    @Schema(description = "及格人数")
    private Integer passCount;

    @Schema(description = "及格率(%)")
    private BigDecimal passRate;

    @Schema(description = "优秀人数")
    private Integer excellentCount;

    @Schema(description = "优秀率(%)")
    private BigDecimal excellentRate;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "分数段统计")
    private Map<String, Integer> scoreSegments;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "科目名称")
    private String subjectName;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Long getExamSubjectId() { return examSubjectId; }
    public void setExamSubjectId(Long examSubjectId) { this.examSubjectId = examSubjectId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Integer getStatType() { return statType; }
    public void setStatType(Integer statType) { this.statType = statType; }

    public Integer getStudentCount() { return studentCount; }
    public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }

    public BigDecimal getFullScore() { return fullScore; }
    public void setFullScore(BigDecimal fullScore) { this.fullScore = fullScore; }

    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }

    public BigDecimal getMinScore() { return minScore; }
    public void setMinScore(BigDecimal minScore) { this.minScore = minScore; }

    public BigDecimal getAvgScore() { return avgScore; }
    public void setAvgScore(BigDecimal avgScore) { this.avgScore = avgScore; }

    public Integer getPassCount() { return passCount; }
    public void setPassCount(Integer passCount) { this.passCount = passCount; }

    public BigDecimal getPassRate() { return passRate; }
    public void setPassRate(BigDecimal passRate) { this.passRate = passRate; }

    public Integer getExcellentCount() { return excellentCount; }
    public void setExcellentCount(Integer excellentCount) { this.excellentCount = excellentCount; }

    public BigDecimal getExcellentRate() { return excellentRate; }
    public void setExcellentRate(BigDecimal excellentRate) { this.excellentRate = excellentRate; }

    public Map<String, Integer> getScoreSegments() { return scoreSegments; }
    public void setScoreSegments(Map<String, Integer> scoreSegments) { this.scoreSegments = scoreSegments; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
