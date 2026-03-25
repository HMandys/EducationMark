package com.edumark.answersheet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

/**
 * 答题卡模板DTO
 *
 * @author EduMark
 */
@Schema(description = "答题卡模板请求DTO")
public class AnswerSheetTemplateDTO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "关联试卷ID（可选，支持独立创建答题卡模板）")
    private Long paperId;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "科目名称")
    private String subjectName;

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "纸张大小: A4/A3/B5")
    private String pageSize = "A4";

    @Schema(description = "方向: 1-纵向 2-横向")
    private Integer orientation = 1;

    @Schema(description = "列数: 1-单列 2-双列")
    private Integer columns = 1;

    @Schema(description = "上边距(mm)")
    private Integer marginTop = 20;

    @Schema(description = "下边距(mm)")
    private Integer marginBottom = 20;

    @Schema(description = "左边距(mm)")
    private Integer marginLeft = 15;

    @Schema(description = "右边距(mm)")
    private Integer marginRight = 15;

    @Schema(description = "页眉配置")
    private Map<String, Object> headerConfig;

    @Schema(description = "学生信息区配置")
    private Map<String, Object> studentInfoConfig;

    @Schema(description = "区域列表")
    private List<AnswerSheetRegionDTO> regions;

    @Schema(description = "模板图片路径")
    private String templateImagePath;

    @Schema(description = "四角定位点配置")
    private Map<String, Object> cornerConfig;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPageSize() { return pageSize; }
    public void setPageSize(String pageSize) { this.pageSize = pageSize; }

    public Integer getOrientation() { return orientation; }
    public void setOrientation(Integer orientation) { this.orientation = orientation; }

    public Integer getColumns() { return columns; }
    public void setColumns(Integer columns) { this.columns = columns; }

    public Integer getMarginTop() { return marginTop; }
    public void setMarginTop(Integer marginTop) { this.marginTop = marginTop; }

    public Integer getMarginBottom() { return marginBottom; }
    public void setMarginBottom(Integer marginBottom) { this.marginBottom = marginBottom; }

    public Integer getMarginLeft() { return marginLeft; }
    public void setMarginLeft(Integer marginLeft) { this.marginLeft = marginLeft; }

    public Integer getMarginRight() { return marginRight; }
    public void setMarginRight(Integer marginRight) { this.marginRight = marginRight; }

    public Map<String, Object> getHeaderConfig() { return headerConfig; }
    public void setHeaderConfig(Map<String, Object> headerConfig) { this.headerConfig = headerConfig; }

    public Map<String, Object> getStudentInfoConfig() { return studentInfoConfig; }
    public void setStudentInfoConfig(Map<String, Object> studentInfoConfig) { this.studentInfoConfig = studentInfoConfig; }

    public List<AnswerSheetRegionDTO> getRegions() { return regions; }
    public void setRegions(List<AnswerSheetRegionDTO> regions) { this.regions = regions; }

    public String getTemplateImagePath() { return templateImagePath; }
    public void setTemplateImagePath(String templateImagePath) { this.templateImagePath = templateImagePath; }

    public Map<String, Object> getCornerConfig() { return cornerConfig; }
    public void setCornerConfig(Map<String, Object> cornerConfig) { this.cornerConfig = cornerConfig; }
}
