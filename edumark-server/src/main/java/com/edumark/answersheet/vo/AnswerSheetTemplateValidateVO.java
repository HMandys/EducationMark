package com.edumark.answersheet.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 答题卡模板校验结果VO
 *
 * @author EduMark
 */
@Schema(description = "答题卡模板校验结果")
public class AnswerSheetTemplateValidateVO {

    @Schema(description = "是否通过校验")
    private Boolean passed;

    @Schema(description = "区域总数")
    private Integer totalRegionCount;

    @Schema(description = "已标注区域数")
    private Integer annotatedRegionCount;

    @Schema(description = "问题数量")
    private Integer issueCount;

    @Schema(description = "问题列表")
    private List<ValidationIssue> issues = new ArrayList<>();

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public Integer getTotalRegionCount() { return totalRegionCount; }
    public void setTotalRegionCount(Integer totalRegionCount) { this.totalRegionCount = totalRegionCount; }

    public Integer getAnnotatedRegionCount() { return annotatedRegionCount; }
    public void setAnnotatedRegionCount(Integer annotatedRegionCount) { this.annotatedRegionCount = annotatedRegionCount; }

    public Integer getIssueCount() { return issueCount; }
    public void setIssueCount(Integer issueCount) { this.issueCount = issueCount; }

    public List<ValidationIssue> getIssues() { return issues; }
    public void setIssues(List<ValidationIssue> issues) { this.issues = issues; }

    @Schema(description = "模板校验问题")
    public static class ValidationIssue {

        @Schema(description = "区域名称")
        private String regionName;

        @Schema(description = "字段名")
        private String field;

        @Schema(description = "问题描述")
        private String message;

        public ValidationIssue() {
        }

        public ValidationIssue(String regionName, String field, String message) {
            this.regionName = regionName;
            this.field = field;
            this.message = message;
        }

        public String getRegionName() { return regionName; }
        public void setRegionName(String regionName) { this.regionName = regionName; }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
