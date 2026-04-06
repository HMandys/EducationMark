package com.edumark.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 批改策略配置
 */
@TableName("ai_marking_policy")
@Schema(description = "AI批改策略配置")
public class AiMarkingPolicy extends BaseEntity {

    @Schema(description = "是否启用 AI 自动批改")
    private Integer enabled;

    @Schema(description = "低置信度阈值")
    private Double lowConfidenceThreshold;

    @Schema(description = "失败回退策略")
    private String failureStrategy;

    @Schema(description = "默认提示词模板")
    private String promptTemplate;

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public Double getLowConfidenceThreshold() { return lowConfidenceThreshold; }
    public void setLowConfidenceThreshold(Double lowConfidenceThreshold) { this.lowConfidenceThreshold = lowConfidenceThreshold; }

    public String getFailureStrategy() { return failureStrategy; }
    public void setFailureStrategy(String failureStrategy) { this.failureStrategy = failureStrategy; }

    public String getPromptTemplate() { return promptTemplate; }
    public void setPromptTemplate(String promptTemplate) { this.promptTemplate = promptTemplate; }
}
