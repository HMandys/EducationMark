package com.edumark.ai.dto;

import com.edumark.common.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 批改审计查询参数
 */
@Schema(description = "AI批改审计查询参数")
public class AiMarkingRecordQueryDTO extends PageQuery {

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题号")
    private Integer questionNo;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "协议类型")
    private String protocol;

    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Integer getQuestionNo() { return questionNo; }
    public void setQuestionNo(Integer questionNo) { this.questionNo = questionNo; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
}
