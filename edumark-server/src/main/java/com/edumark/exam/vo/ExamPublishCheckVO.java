package com.edumark.exam.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 考试发布前检查结果
 *
 * @author EduMark
 */
@Schema(description = "考试发布前检查结果")
public class ExamPublishCheckVO {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "是否通过检查")
    private Boolean canPublish;

    @Schema(description = "参考班级数量")
    private Integer classCount;

    @Schema(description = "考试科目数量")
    private Integer subjectCount;

    @Schema(description = "已完成试卷数量")
    private Integer completedPaperCount;

    @Schema(description = "已发布模板数量")
    private Integer publishedTemplateCount;

    @Schema(description = "存在题目的科目数量")
    private Integer subjectWithQuestionCount;

    @Schema(description = "缺失项列表")
    private List<String> missingItems;

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Boolean getCanPublish() { return canPublish; }
    public void setCanPublish(Boolean canPublish) { this.canPublish = canPublish; }

    public Integer getClassCount() { return classCount; }
    public void setClassCount(Integer classCount) { this.classCount = classCount; }

    public Integer getSubjectCount() { return subjectCount; }
    public void setSubjectCount(Integer subjectCount) { this.subjectCount = subjectCount; }

    public Integer getCompletedPaperCount() { return completedPaperCount; }
    public void setCompletedPaperCount(Integer completedPaperCount) { this.completedPaperCount = completedPaperCount; }

    public Integer getPublishedTemplateCount() { return publishedTemplateCount; }
    public void setPublishedTemplateCount(Integer publishedTemplateCount) { this.publishedTemplateCount = publishedTemplateCount; }

    public Integer getSubjectWithQuestionCount() { return subjectWithQuestionCount; }
    public void setSubjectWithQuestionCount(Integer subjectWithQuestionCount) { this.subjectWithQuestionCount = subjectWithQuestionCount; }

    public List<String> getMissingItems() { return missingItems; }
    public void setMissingItems(List<String> missingItems) { this.missingItems = missingItems; }
}
