package com.edumark.score.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成绩统计VO
 *
 * @author EduMark
 */
@Schema(description = "成绩统计VO")
public class ScoreStatisticsVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "考试科目ID")
    private Long examSubjectId;

    @Schema(description = "科目名称")
    private String subjectName;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "统计类型")
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

    @Schema(description = "分数段统计")
    private Map<String, Integer> scoreSegments;

    @Schema(description = "分数段列表(图表用)")
    private List<ScoreSegmentVO> segmentList;

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

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

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

    public List<ScoreSegmentVO> getSegmentList() { return segmentList; }
    public void setSegmentList(List<ScoreSegmentVO> segmentList) { this.segmentList = segmentList; }

    /**
     * 分数段VO
     */
    public static class ScoreSegmentVO {
        private String segment;
        private Integer count;

        public ScoreSegmentVO() {}

        public ScoreSegmentVO(String segment, Integer count) {
            this.segment = segment;
            this.count = count;
        }

        public String getSegment() { return segment; }
        public void setSegment(String segment) { this.segment = segment; }

        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}
