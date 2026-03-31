package com.edumark.answersheet.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * 答题区域配置VO
 *
 * @author EduMark
 */
@Schema(description = "答题区域配置VO")
public class AnswerSheetRegionVO {

    @Schema(description = "区域ID")
    private Long id;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "区域类型: 1-选择题 3-主观题 5-条码区")
    private Integer regionType;

    @Schema(description = "区域类型名称")
    private String regionTypeName;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "页码")
    private Integer pageNo;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "起始题号")
    private Integer questionStart;

    @Schema(description = "结束题号")
    private Integer questionEnd;

    @Schema(description = "关联题目ID列表")
    private List<Long> questionIds;

    @Schema(description = "区域配置")
    private Map<String, Object> config;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public Integer getRegionType() { return regionType; }
    public void setRegionType(Integer regionType) { this.regionType = regionType; }

    public String getRegionTypeName() { return regionTypeName; }
    public void setRegionTypeName(String regionTypeName) { this.regionTypeName = regionTypeName; }

    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getQuestionStart() { return questionStart; }
    public void setQuestionStart(Integer questionStart) { this.questionStart = questionStart; }

    public Integer getQuestionEnd() { return questionEnd; }
    public void setQuestionEnd(Integer questionEnd) { this.questionEnd = questionEnd; }

    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }

    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
}
