package com.edumark.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 客观题答案更新DTO
 *
 * @author EduMark
 */
@Schema(description = "客观题答案更新DTO")
public class AnswerSheetObjectiveAnswerDTO {

    @Schema(description = "学生答案")
    private String studentAnswer;

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }
}
