package com.edumark.marking.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 阅卷项VO（单份待阅题目）
 *
 * @author EduMark
 */
@Schema(description = "阅卷项VO")
public class MarkingItemVO {

    @Schema(description = "阅卷记录ID")
    private Long recordId;

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "题目图片URL")
    private String questionImage;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "当前序号")
    private Integer currentIndex;

    @Schema(description = "总数")
    private Integer totalCount;

    // Getters and Setters
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }

    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionImage() { return questionImage; }
    public void setQuestionImage(String questionImage) { this.questionImage = questionImage; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public Integer getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(Integer currentIndex) { this.currentIndex = currentIndex; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
}
