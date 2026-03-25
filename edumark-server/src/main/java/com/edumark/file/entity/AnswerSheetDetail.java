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

    @Schema(description = "题目ID（关联试卷时使用）")
    private Long questionId;

    @Schema(description = "区域ID（独立模式时使用）")
    private Long regionId;

    @Schema(description = "题号")
    private Integer questionNo;

    @Schema(description = "满分")
    private Integer fullScore;

    @Schema(description = "正确答案")
    private String correctAnswer;

    @Schema(description = "是否客观题: 0-否 1-是")
    private Integer isObjective;

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

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public Integer getQuestionNo() { return questionNo; }
    public void setQuestionNo(Integer questionNo) { this.questionNo = questionNo; }

    public Integer getFullScore() { return fullScore; }
    public void setFullScore(Integer fullScore) { this.fullScore = fullScore; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public Integer getIsObjective() { return isObjective; }
    public void setIsObjective(Integer isObjective) { this.isObjective = isObjective; }

    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
