package com.edumark.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * App AI 学情分析结果
 *
 * @author EduMark
 */
@Schema(description = "App AI学情分析结果")
public class AppAiReportVO {

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "分析摘要")
    private String summary;

    @Schema(description = "优势项")
    private List<String> strengths;

    @Schema(description = "待提升项")
    private List<String> weaknesses;

    @Schema(description = "学习建议")
    private List<String> suggestions;

    @Schema(description = "最近一次总分")
    private BigDecimal latestTotalScore;

    @Schema(description = "平均总分")
    private BigDecimal averageTotalScore;

    @Schema(description = "成绩趋势描述")
    private String trend;

    @Schema(description = "AI 提供商")
    private String providerName;

    @Schema(description = "协议类型")
    private String protocol;

    @Schema(description = "模型名称")
    private String model;

    @Schema(description = "生成时间")
    private LocalDateTime generatedAt;

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public BigDecimal getLatestTotalScore() { return latestTotalScore; }
    public void setLatestTotalScore(BigDecimal latestTotalScore) { this.latestTotalScore = latestTotalScore; }

    public BigDecimal getAverageTotalScore() { return averageTotalScore; }
    public void setAverageTotalScore(BigDecimal averageTotalScore) { this.averageTotalScore = averageTotalScore; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
