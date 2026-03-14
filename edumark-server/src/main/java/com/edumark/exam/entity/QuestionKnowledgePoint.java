package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 题目知识点关联实体
 *
 * @author EduMark
 */
@TableName("question_knowledge_point")
@Schema(description = "题目知识点关联")
public class QuestionKnowledgePoint extends BaseEntity {

    @Schema(description = "题目ID")
    private Long questionId;

    @Schema(description = "知识点ID")
    private Long knowledgePointId;

    // Getters and Setters
    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
}
