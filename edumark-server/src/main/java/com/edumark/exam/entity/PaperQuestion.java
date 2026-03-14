package com.edumark.exam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 试卷题目实体
 *
 * @author EduMark
 */
@TableName("paper_question")
@Schema(description = "试卷题目")
public class PaperQuestion extends BaseEntity {

    @Schema(description = "试卷ID")
    private Long paperId;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "大题号")
    private Integer sectionNo;

    @Schema(description = "大题名称")
    private String sectionName;

    @Schema(description = "小题号")
    private Integer itemNo;

    @Schema(description = "题目类型: 1-单选 2-多选 3-判断 4-填空 5-简答 6-计算 7-作文 9-其他")
    private Integer questionType;

    @Schema(description = "是否客观题: 0-否 1-是")
    private Integer isObjective;

    @Schema(description = "满分")
    private Integer score;

    @Schema(description = "正确答案")
    private String correctAnswer;

    @Schema(description = "评分标准")
    private String scoringCriteria;

    @Schema(description = "双评阈值")
    private Integer doubleMarkingThreshold;

    @Schema(description = "是否启用双评: 0-否 1-是")
    private Integer enableDoubleMarking;

    @Schema(description = "排序号")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @TableField(exist = false)
    @Schema(description = "试卷名称")
    private String paperName;

    @TableField(exist = false)
    @Schema(description = "关联的知识点")
    private String knowledgePoints;

    // Getters and Setters
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

    public String getPaperName() { return paperName; }
    public void setPaperName(String paperName) { this.paperName = paperName; }

    public String getKnowledgePoints() { return knowledgePoints; }
    public void setKnowledgePoints(String knowledgePoints) { this.knowledgePoints = knowledgePoints; }
}
