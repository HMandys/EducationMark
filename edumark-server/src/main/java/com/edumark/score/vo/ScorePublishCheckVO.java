package com.edumark.score.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 成绩发布前检查结果
 *
 * @author EduMark
 */
@Schema(description = "成绩发布前检查结果")
public class ScorePublishCheckVO {

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "考试状态")
    private Integer examStatus;

    @Schema(description = "是否可发布")
    private Boolean canPublish;

    @Schema(description = "科目数")
    private Integer subjectCount;

    @Schema(description = "答题卡总数")
    private Integer answerSheetCount;

    @Schema(description = "已完成答题卡数")
    private Integer completedAnswerSheetCount;

    @Schema(description = "识别中答题卡数")
    private Integer pendingRecognitionCount;

    @Schema(description = "识别异常答题卡数")
    private Integer recognitionExceptionCount;

    @Schema(description = "待阅卷答题卡数")
    private Integer pendingMarkingAnswerSheetCount;

    @Schema(description = "阅卷任务总数")
    private Integer markingTaskCount;

    @Schema(description = "未完成阅卷任务数")
    private Integer unfinishedTaskCount;

    @Schema(description = "待仲裁数")
    private Integer pendingArbitrationCount;

    @Schema(description = "总分汇总记录数")
    private Integer examScoreCount;

    @Schema(description = "科目成绩记录数")
    private Integer subjectScoreCount;

    @Schema(description = "统计记录数")
    private Integer statisticsCount;

    @Schema(description = "阻塞项")
    private List<String> blockingItems;

    @Schema(description = "提示项")
    private List<String> warningItems;

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Integer getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    public Boolean getCanPublish() {
        return canPublish;
    }

    public void setCanPublish(Boolean canPublish) {
        this.canPublish = canPublish;
    }

    public Integer getSubjectCount() {
        return subjectCount;
    }

    public void setSubjectCount(Integer subjectCount) {
        this.subjectCount = subjectCount;
    }

    public Integer getAnswerSheetCount() {
        return answerSheetCount;
    }

    public void setAnswerSheetCount(Integer answerSheetCount) {
        this.answerSheetCount = answerSheetCount;
    }

    public Integer getCompletedAnswerSheetCount() {
        return completedAnswerSheetCount;
    }

    public void setCompletedAnswerSheetCount(Integer completedAnswerSheetCount) {
        this.completedAnswerSheetCount = completedAnswerSheetCount;
    }

    public Integer getPendingRecognitionCount() {
        return pendingRecognitionCount;
    }

    public void setPendingRecognitionCount(Integer pendingRecognitionCount) {
        this.pendingRecognitionCount = pendingRecognitionCount;
    }

    public Integer getRecognitionExceptionCount() {
        return recognitionExceptionCount;
    }

    public void setRecognitionExceptionCount(Integer recognitionExceptionCount) {
        this.recognitionExceptionCount = recognitionExceptionCount;
    }

    public Integer getPendingMarkingAnswerSheetCount() {
        return pendingMarkingAnswerSheetCount;
    }

    public void setPendingMarkingAnswerSheetCount(Integer pendingMarkingAnswerSheetCount) {
        this.pendingMarkingAnswerSheetCount = pendingMarkingAnswerSheetCount;
    }

    public Integer getMarkingTaskCount() {
        return markingTaskCount;
    }

    public void setMarkingTaskCount(Integer markingTaskCount) {
        this.markingTaskCount = markingTaskCount;
    }

    public Integer getUnfinishedTaskCount() {
        return unfinishedTaskCount;
    }

    public void setUnfinishedTaskCount(Integer unfinishedTaskCount) {
        this.unfinishedTaskCount = unfinishedTaskCount;
    }

    public Integer getPendingArbitrationCount() {
        return pendingArbitrationCount;
    }

    public void setPendingArbitrationCount(Integer pendingArbitrationCount) {
        this.pendingArbitrationCount = pendingArbitrationCount;
    }

    public Integer getExamScoreCount() {
        return examScoreCount;
    }

    public void setExamScoreCount(Integer examScoreCount) {
        this.examScoreCount = examScoreCount;
    }

    public Integer getSubjectScoreCount() {
        return subjectScoreCount;
    }

    public void setSubjectScoreCount(Integer subjectScoreCount) {
        this.subjectScoreCount = subjectScoreCount;
    }

    public Integer getStatisticsCount() {
        return statisticsCount;
    }

    public void setStatisticsCount(Integer statisticsCount) {
        this.statisticsCount = statisticsCount;
    }

    public List<String> getBlockingItems() {
        return blockingItems;
    }

    public void setBlockingItems(List<String> blockingItems) {
        this.blockingItems = blockingItems;
    }

    public List<String> getWarningItems() {
        return warningItems;
    }

    public void setWarningItems(List<String> warningItems) {
        this.warningItems = warningItems;
    }
}
