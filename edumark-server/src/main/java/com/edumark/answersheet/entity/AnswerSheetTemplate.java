package com.edumark.answersheet.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.edumark.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * 答题卡模板实体
 *
 * @author EduMark
 */
@TableName(value = "answer_sheet_template", autoResultMap = true)
@Schema(description = "答题卡模板")
public class AnswerSheetTemplate extends BaseEntity {

    @Schema(description = "关联试卷ID")
    private Long paperId;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "纸张大小: A4/A3/B5")
    private String pageSize;

    @Schema(description = "方向: 1-纵向 2-横向")
    private Integer orientation;

    @Schema(description = "列数: 1-单列 2-双列")
    private Integer columns;

    @Schema(description = "上边距(mm)")
    private Integer marginTop;

    @Schema(description = "下边距(mm)")
    private Integer marginBottom;

    @Schema(description = "左边距(mm)")
    private Integer marginLeft;

    @Schema(description = "右边距(mm)")
    private Integer marginRight;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "页眉配置")
    private Map<String, Object> headerConfig;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "学生信息区配置")
    private Map<String, Object> studentInfoConfig;

    @Schema(description = "状态: 0-草稿 1-已发布")
    private Integer status;

    @Schema(description = "PDF存储路径")
    private String pdfObjectName;

    @Schema(description = "模板图片路径")
    private String templateImagePath;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "四角定位点配置")
    private Map<String, Object> cornerConfig;

    @TableField(exist = false)
    @Schema(description = "试卷名称")
    private String paperName;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;

    @TableField(exist = false)
    @Schema(description = "科目名称")
    private String subjectName;

    @TableField(exist = false)
    @Schema(description = "区域列表")
    private List<AnswerSheetRegion> regions;

    // Getters and Setters
    public Long getPaperId() { return paperId; }
    public void setPaperId(Long paperId) { this.paperId = paperId; }

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getPdfObjectName() { return pdfObjectName; }
    public void setPdfObjectName(String pdfObjectName) { this.pdfObjectName = pdfObjectName; }

    public String getTemplateImagePath() { return templateImagePath; }
    public void setTemplateImagePath(String templateImagePath) { this.templateImagePath = templateImagePath; }

    public Map<String, Object> getCornerConfig() { return cornerConfig; }
    public void setCornerConfig(Map<String, Object> cornerConfig) { this.cornerConfig = cornerConfig; }

    public String getPaperName() { return paperName; }
    public void setPaperName(String paperName) { this.paperName = paperName; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public List<AnswerSheetRegion> getRegions() { return regions; }
    public void setRegions(List<AnswerSheetRegion> regions) { this.regions = regions; }
}
