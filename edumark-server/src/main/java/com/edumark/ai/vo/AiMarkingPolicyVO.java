package com.edumark.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * AI 批改策略响应
 */
@Schema(description = "AI批改策略响应")
public class AiMarkingPolicyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "是否启用 AI 自动批改")
    private Integer enabled;

    @Schema(description = "低置信度阈值")
    private Double lowConfidenceThreshold;

    @Schema(description = "失败回退策略")
    private String failureStrategy;

    @Schema(description = "默认提示词模板")
    private String promptTemplate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public Double getLowConfidenceThreshold() { return lowConfidenceThreshold; }
    public void setLowConfidenceThreshold(Double lowConfidenceThreshold) { this.lowConfidenceThreshold = lowConfidenceThreshold; }

    public String getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(String failureStrategy) { this.failureStrategy = failureStrategy; }

    public String getPromptTemplate() { return promptTemplate; }
    public void setPromptTemplate(String promptTemplate) { this.promptTemplate = promptTemplate; }
}
