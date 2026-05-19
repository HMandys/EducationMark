package com.edumark.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * AI 异常复核结果更新 DTO
 */
@Schema(description = "AI异常复核结果更新DTO")
public class AnswerSheetAiReviewResultDTO {

    @Schema(description = "学生答案/人工修正识别文本")
    private String studentAnswer;

    @Schema(description = "人工确认得分")
    private Integer score;

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
}
