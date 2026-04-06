package com.edumark.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 批改提供商配置
 */
@TableName("ai_marking_provider")
@Schema(description = "AI批改提供商配置")
public class AiMarkingProvider extends BaseEntity {

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "协议类型")
    private String protocol;

    @Schema(description = "接口地址")
    private String baseUrl;

    @Schema(description = "API Key")
    private String apiKey;

    @Schema(description = "模型名称")
    private String model;

    @Schema(description = "是否启用")
    private Integer enabled;

    @Schema(description = "是否默认提供商")
    private Integer isDefault;

    @Schema(description = "超时时间(毫秒)")
    private Integer timeoutMs;

    @Schema(description = "最大输出Token")
    private Integer maxTokens;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "备注")
    private String remark;

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }

    public Integer getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(Integer timeoutMs) { this.timeoutMs = timeoutMs; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
