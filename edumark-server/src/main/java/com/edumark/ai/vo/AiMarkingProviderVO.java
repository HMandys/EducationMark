package com.edumark.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 批改提供商响应
 */
@Schema(description = "AI批改提供商响应")
public class AiMarkingProviderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "协议类型")
    private String protocol;

    @Schema(description = "接口地址")
    private String baseUrl;

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

    @Schema(description = "已脱敏的 API Key")
    private String apiKeyMasked;

    @Schema(description = "是否已配置 API Key")
    private Boolean hasApiKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

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

    public String getApiKeyMasked() { return apiKeyMasked; }
    public void setApiKeyMasked(String apiKeyMasked) { this.apiKeyMasked = apiKeyMasked; }

    public Boolean getHasApiKey() { return hasApiKey; }
    public void setHasApiKey(Boolean hasApiKey) { this.hasApiKey = hasApiKey; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
