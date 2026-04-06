package com.edumark.ai.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 批改提供商查询参数
 */
@Schema(description = "AI批改提供商查询参数")
public class AiMarkingProviderQueryDTO extends PageQuery {

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "协议类型")
    private String protocol;

    @Schema(description = "状态")
    private Integer enabled;

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }
}
