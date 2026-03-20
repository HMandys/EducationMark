package com.edumark.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡题目明细VO
 *
 * @author EduMark
 */
@Schema(description = "答题卡题目明细VO")
public class AnswerSheetQuestionDetailVO {

    @Schema(description = "答题卡明细ID")
    private Long id;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "题型")
    private Integer questionType;

    @Schema(description = "题型名称")
    private String questionTypeName;

    @Schema(description = "是否客观题")
    private Integer isObjective;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "标准答案")
    private String correctAnswer;

    @Schema(description = "学生答案")
    private String studentAnswer;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "区域用途")
    private String regionRole;

    @Schema(description = "区域用途名称")
    private String regionRoleName;

    @Schema(description = "裁题模式")
    private String cropMode;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "选项数量")
    private Integer optionCount;

    @Schema(description = "是否可生成预览")
    private Boolean previewAvailable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswerSheetId() {
        return answerSheetId;
    }

    public void setAnswerSheetId(Long answerSheetId) {
        this.answerSheetId = answerSheetId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(String questionNo) {
        this.questionNo = questionNo;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public String getQuestionTypeName() {
        return questionTypeName;
    }

    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    public Integer getIsObjective() {
        return isObjective;
    }

    public void setIsObjective(Integer isObjective) {
        this.isObjective = isObjective;
    }

    public Integer getFullScore() {
        return fullScore;
    }

    public void setFullScore(Integer fullScore) {
        this.fullScore = fullScore;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getRegionRole() {
        return regionRole;
    }

    public void setRegionRole(String regionRole) {
        this.regionRole = regionRole;
    }

    public String getRegionRoleName() {
        return regionRoleName;
    }

    public void setRegionRoleName(String regionRoleName) {
        this.regionRoleName = regionRoleName;
    }

    public String getCropMode() {
        return cropMode;
    }

    public void setCropMode(String cropMode) {
        this.cropMode = cropMode;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getOptionCount() {
        return optionCount;
    }

    public void setOptionCount(Integer optionCount) {
        this.optionCount = optionCount;
    }

    public Boolean getPreviewAvailable() {
        return previewAvailable;
    }

    public void setPreviewAvailable(Boolean previewAvailable) {
        this.previewAvailable = previewAvailable;
    }
}
