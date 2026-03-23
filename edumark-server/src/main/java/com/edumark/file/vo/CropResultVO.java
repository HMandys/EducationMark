package com.edumark.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 裁题结果VO
 *
 * @author EduMark
 */
@Schema(description = "裁题结果VO")
public class CropResultVO {

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "成功裁题数")
    private Integer successCount;

    @Schema(description = "失败裁题数")
    private Integer failCount;

    @Schema(description = "裁题详情列表")
    private List<CropItemVO> items;

    @Schema(description = "错误信息")
    private String errorMessage;

    // Getters and Setters
    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

    public Integer getFailCount() { return failCount; }
    public void setFailCount(Integer failCount) { this.failCount = failCount; }

    public List<CropItemVO> getItems() { return items; }
    public void setItems(List<CropItemVO> items) { this.items = items; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
