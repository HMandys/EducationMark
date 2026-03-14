package com.edumark.exam.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 试卷题目DTO
 *
 * @author EduMark
 */
@Schema(description = "试卷题目请求DTO")
public class PaperQuestionDTO {

    @Schema(description = "ID(更新时必填)")
    private Long id;

    @Schema(description = "试卷ID", required = true)
    private Long paperId;

    @Schema(description = "题号", required = true)
    private String questionNo;

    @Schema(description = "大题号")
    private Integer sectionNo;

    @Schema(description = "大题名称")
    private String sectionName;

    @Schema(description = "小题号")
    private Integer itemNo;

    @Schema(description = "题目类型", required = true)
    private Integer questionType;

    @Schema(description = "是否客观题")
    private Integer isObjective;

    @Schema(description = "满分", required = true)
    private Integer score;

    @Schema(description = "正确答案")
    private String correctAnswer;

    @Schema(description = "评分标准")
    private String scoringCriteria;

    @Schema(description = "双评阈值")
    private Integer doubleMarkingThreshold;

    @Schema(description = "是否启用双评")
    private Integer enableDoubleMarking;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联的知识点ID列表")
    private List<Long> knowledgePointIds;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }

    public String getQuestionNo() { return questionNo; }
    public void setQuestionNo(String questionNo) { this.questionNo = questionNo; }

    public Integer getSectionNo() { return sectionNo; }
    public void setSectionNo(Integer sectionNo) { this.sectionNo = sectionNo; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public Integer getItemNo() { return itemNo; }
    public void setItemNo(Integer itemNo) { this.itemNo = itemNo; }

    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }

    public Integer getIsObjective() { return isObjective; }
    public void setIsObjective(Integer isObjective) { this.isObjective = isObjective; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getScoringCriteria() { return scoringCriteria; }
    public void setScoringCriteria(String scoringCriteria) { this.scoringCriteria = scoringCriteria; }

    public Integer getDoubleMarkingThreshold() { return doubleMarkingThreshold; }
    public void setDoubleMarkingThreshold(Integer doubleMarkingThreshold) { this.doubleMarkingThreshold = doubleMarkingThreshold; }

    public Integer getEnableDoubleMarking() { return enableDoubleMarking; }
    public void setEnableDoubleMarking(Integer enableDoubleMarking) { this.enableDoubleMarking = enableDoubleMarking; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<Long> getKnowledgePointIds() { return knowledgePointIds; }
    public void setKnowledgePointIds(List<Long> knowledgePointIds) { this.knowledgePointIds = knowledgePointIds; }
}
