package com.edumark.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 裁题项VO
 *
 * @author EduMark
 */
@Schema(description = "裁题项VO")
public class CropItemVO {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "是否客观题")
    private Boolean isObjective;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "裁题图片URL")
    private String imageUrl;

    @Schema(description = "错误信息")
    private String errorMessage;

    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public Boolean getIsObjective() { return isObjective; }
    public void setIsObjective(Boolean isObjective) { this.isObjective = isObjective; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
