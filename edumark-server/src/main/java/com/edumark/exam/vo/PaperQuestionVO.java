package com.edumark.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷题目VO
 *
 * @author EduMark
 */
@Schema(description = "试卷题目响应VO")
public class PaperQuestionVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "试卷ID")
    private Long paperId;

    @Schema(description = "试卷名称")
    private String paperName;

    @Schema(description = "题号")
    private String questionNo;

    @Schema(description = "大题号")
    private Integer sectionNo;

    @Schema(description = "大题名称")
    private String sectionName;

    @Schema(description = "小题号")
    private Integer itemNo;

    @Schema(description = "题目类型")
    private Integer questionType;

    @Schema(description = "题目类型名称")
    private String questionTypeName;

    @Schema(description = "是否客观题")
    private Integer isObjective;

    @Schema(description = "满分")
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

    @Schema(description = "关联的知识点")
    private List<KnowledgePointVO> knowledgePoints;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }

    public String getPaperName() { return paperName; }
    public void setPaperName(String paperName) { this.paperName = paperName; }

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

    public String getQuestionTypeName() { return questionTypeName; }
    public void setQuestionTypeName(String questionTypeName) { this.questionTypeName = questionTypeName; }

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

    public List<KnowledgePointVO> getKnowledgePoints() { return knowledgePoints; }
    public void setKnowledgePoints(List<KnowledgePointVO> knowledgePoints) { this.knowledgePoints = knowledgePoints; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
