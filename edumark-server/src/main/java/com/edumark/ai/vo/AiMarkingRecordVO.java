package com.edumark.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 批改审计响应
 */
@Schema(description = "AI批改审计响应")
public class AiMarkingRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题号")
    private Integer questionNo;

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "协议类型")
    private String protocol;

    @Schema(description = "模型名称")
    private String model;

    @Schema(description = "标准答案")
    private String referenceAnswer;

    @Schema(description = "识别文本")
    private String recognizedText;

    @Schema(description = "建议得分")
    private Integer suggestedScore;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "判分理由")
    private String judgeReason;

    @Schema(description = "执行状态")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "原始响应")
    private String rawResponse;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Integer getQuestionNo() { return questionNo; }
    public void setQuestionNo(Integer questionNo) { this.questionNo = questionNo; }

    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getReferenceAnswer() { return referenceAnswer; }
    public void setReferenceAnswer(String referenceAnswer) { this.referenceAnswer = referenceAnswer; }

    public String getRecognizedText() { return recognizedText; }
    public void setRecognizedText(String recognizedText) { this.recognizedText = recognizedText; }

    public Integer getSuggestedScore() { return suggestedScore; }
    public void setSuggestedScore(Integer suggestedScore) { this.suggestedScore = suggestedScore; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getJudgeReason() { return judgeReason; }
    public void setJudgeReason(String judgeReason) { this.judgeReason = judgeReason; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
