package com.edumark.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 答题卡明细实体
 *
 * @author EduMark
 */
@TableName("answer_sheet_detail")
@Schema(description = "答题卡明细")
public class AnswerSheetDetail extends BaseEntity {

    @Schema(description = "答题卡ID")
    private Long answerSheetId;

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "学生答案")
    private String studentAnswer;

    @Schema(description = "得分")
    private Integer score;

    @Schema(description = "状态：0-待处理/待阅卷，1-已完成评分，2-主观题已核验通过，3-主观题待修正")
    private Integer status;

    // Getters and Setters
    public Long getAnswerSheetId() { return answerSheetId; }
    public void setAnswerSheetId(Long answerSheetId) { this.answerSheetId = answerSheetId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
