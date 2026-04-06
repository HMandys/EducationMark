package com.edumark.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * AI 批改提供商请求参数
 */
@Schema(description = "AI批改提供商请求参数")
public class AiMarkingProviderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    private Long id;

    @NotBlank(message = "提供商名称不能为空")
    @Schema(description = "提供商名称")
    private String providerName;

    @NotBlank(message = "协议类型不能为空")
    @Schema(description = "协议类型")
    private String protocol;

    @NotBlank(message = "接口地址不能为空")
    @Schema(description = "接口地址")
    private String baseUrl;

    @Schema(description = "API Key")
    private String apiKey;

    @NotBlank(message = "模型名称不能为空")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
